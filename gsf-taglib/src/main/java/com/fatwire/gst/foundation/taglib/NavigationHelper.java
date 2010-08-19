/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.foundation.taglib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.Children;
import com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate.isValidOnDate;

/**
 * Used to retrieve the Navigation Bar data. See the description of
 * getSitePlanAsMap(String pageid) for more details.
 * <p/>
 * TODO: add multilingual support
 *
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class NavigationHelper {
    /**
     * ICS context
     */
    protected final ICS ics;
    /**
     * Local instance of WRAUtils object, pre-instantiated and ready to go
     */
    protected final WRAUtils wraUtils;
    /**
     * Log file
     */
    protected final Log LOG = LogFactory.getLog(NavigationHelper.class);
    /**
     * Effective date for the purposes of startdate/enddate comparisons for
     * an asset.
     */
    protected final Date assetEffectiveDate;


    /**
     * Constructor
     *
     * @param ics object
     */
    public NavigationHelper(ICS ics) {
        this.ics = ics;
        this.wraUtils = new WRAUtils(ics);
        this.assetEffectiveDate = new Date(); // todo: look for preview date variable
    }

    /**
     * Name of the page subtype indicating that this page is NOT rendered on the
     * site but is instead merely used to group navigation components on the
     * site.
     */
    public static final String NAVBAR_NAME = "GSTNavName";

    /**
     * Get a Map<String,Object> object of the site plan tree containing all the
     * attributes necessary to create a nav bar. The Map contains the following
     * keys:
     * <ul>
     * <li><code>page</code>: AssetId of page asset</li>
     * <li><code>pagesubtype</code>: String subtype of page asset</li>
     * <li><code>pagename</code>: String name of page asset</li>
     * <li><code>level</code>: int the number of levels down the site plan tree of the page asset (starting with the
     * pageid you originally pass in = level 0)</li>
     * <li><code>id</code>: AssetId of asset associated to the Page in the unnamed association field.  Should
     * be either a WRA or an alias</li>
     * <li><code>bean</code>: WebReferenceableAsset object containing the field data of the unnamed associated asset.</li>
     * <li><code>url</code>: String url for the nav entry</li>
     * <li><code>linktext</code>: String linktext for the nav entry.  Images are not supported. </li>
     * <li><code>children</code>: (a List<Map<String,Object>> of the children (in the site plan tree) of the page,
     * where each Map contains the above attributes</li>
     * </ul>
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p/>
     * StartDate and EndDate are checked and invalid pages aren't added.  If a Page asset is not valid, its
     * children are not even examined.
     *
     * @param pageid AssetId of (usually a page) in the site plan tree to start
     *               with. Typically this would be a nav name. The nav name would
     *               be included in the output object. Recursion is automatic
     * @return Map<String,Object> of the site plan tree (see above)
     */
    public Map<String, Object> getSitePlanAsMap(String pageid) {
        return getSitePlanAsMap(pageid, 0);
    }

    /**
     * Called from public Map<String,Object> getSitePlanAsMap(String pageid).
     * See that function's description for details
     *
     * @param pageid id of the page assest
     * @param level  starting level number when traversing the site plan tree
     * @return Map<String,Object> of the site plan tree
     */
    private Map<String, Object> getSitePlanAsMap(String pageid, int level) {
        // object to hold results
        Map<String, Object> result = new HashMap<String, Object>();
        AssetId pageId = new AssetIdImpl("Page", Long.parseLong(pageid));
        if (!isValidOnDate(pageId, assetEffectiveDate)) {
            // the input object is not valid.  Abort
            if (LOG.isDebugEnabled()) LOG.debug("Input asset " + pageId + " is not effective on " + assetEffectiveDate);
            return result;
        }
        result.put("page", pageId);
        result.put("level", level);

        // determine if it's a wra, a placeholder or an alias
        AssetData pageData = AssetDataUtils.getAssetData(pageId, "subtype", "name");
        String subtype = pageData.getAttributeData("subtype").getData().toString();
        String name = pageData.getAttributeData("name").getData().toString();
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);
        result.put("pagesubtype", subtype);
        result.put("pagename", name);

        // no link if it's just a placeholder
        if (!isNavigationPlaceholder) {
            // retrieve the unnamed association(s)
            List<AssetId> ids = Children.getOptionalMultivaluedAssociation(ics, "Page", pageid, "-");
            if (ids.size() < 1) {
                // tolerate bad data
                LOG.warn("Page " + pageid + " has no unnamed association value so a link cannot be generated for it.");
            } else if (ids.size() > 1) {
                // tolerate even more bad data
                LOG.warn("Page " + pageid + " has more than one unnamed association values so a link cannot be generated for it.");
            } else {
            	AssetId id = ids.get(0);
                if (isValidOnDate(id, assetEffectiveDate)) {
                    result.put("id", id);
                    if (isGstAlias(id)) {
                        result.putAll(extractAttrFromAlias(id));
                    } else {
                        result.putAll(extractAttrFromWra(id));
                    }
                } else {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Page content " + id + " is not effective on date " + assetEffectiveDate);
                }
            }
        }

        // get the children in the Site Plan
        List<AssetId> childrenIDs = ListPages.getChildPages(ics, Long.parseLong(pageid));
        List<Map<String, Object>> navChildren = new ArrayList<Map<String, Object>>();
        for (AssetId aid : childrenIDs) {
            String childPageID = Long.toString(aid.getId());
            // note recursing here
            Map<String, Object> kidInfo = getSitePlanAsMap(childPageID, level + 1);
            if (kidInfo.keySet().size() > 0) navChildren.add(kidInfo);
        }
        if (navChildren.size() > 0) result.put("children", navChildren);
        return result;
    }

    /**
     * Constant containing the asset type of the GST Alias asset.
     */
    public final String GST_ALIAS_TYPE = "GSTAlias";

    /**
     * Return true if the asset type is a GSTAlias asset type.  May be overridden if
     * customers are attempting to retrofit this class for alias-like functionality that
     * is not implemented by the GSTAlias asset type.
     *
     * @param id asset for which a link is required
     * @return true if the asset is an alias, false if it is a web-referenceable asset
     */
    protected boolean isGstAlias(AssetId id) {
        return GST_ALIAS_TYPE.equals(id.getType());
    }

    /**
     * Get the URL for the alias.  Currently this just looks up the target and generates the URL for that.  However,
     * soon this function WILL CHANGE and will allow an alias to define the URL of the target also (if desired).  A
     * bug in the GSF prevents this for now.  This has to be fixed.  TODO: Reconcile this with revised spec.
     *
     * @param alias Alias bean
     * @return url
     */
    protected String getUrlForAlias(Alias alias) {
        if (alias.getTargetUrl() != null && alias.getTargetUrl().length() > 0) {
            return alias.getTargetUrl();
        } else {
            if (alias.getTarget() != null) {
                return getUrlForWra(wraUtils.getWra(alias.getTarget()));
            } else {
                LOG.warn("Alias asset " + alias + " does not specify a target asset or url.");
                return null;
            }
        }
    }

    /**
     * Get the linktext for the specified alias asset.  If only linkimage is specified and there is no way to
     * locate the linktext, null is returned.
     * <p/>
     * If linktext is specified in the alias, it is returned.  If it is not, the target linktext is returned.
     * If no target is found and linktext is not specified, null is returned and a warning is issued.
     *
     * @param alias Alias bean
     * @return linktext or null on failure.
     */
    protected String getLinktextForAlias(Alias alias) {
        if (alias.getLinkText() != null && alias.getLinkText().length() > 0) {
            return alias.getLinkText();
        } else {
            // it might be pointing directly to the target
            if (alias.getTarget() != null) {

                return getLinktextForWra(wraUtils.getWra(alias.getTarget()));
            } else {
                LOG.warn("Alias asset " + alias + " does not specify linktext.");
                return null;
            }
        }
    }

    /**
     * Get the URL to use for the web-referenceable asset.
     *
     * @param wra WebReferenceableAsset bean
     * @return url
     */
    protected String getUrlForWra(WebReferenceableAsset wra) {
        if (wra.getTemplate() == null || wra.getTemplate().length() == 0) {
            LOG.warn("Asset " + wra + " does not have a valid template set.");
            return null;
        }
        String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher", "ServletRequest.properties", true);
        if (!Utilities.goodString(wrapper)) {
            wrapper = "GSF/Dispatcher";
        }
        GetTemplateUrl gtu = new GetTemplateUrl(ics, wra.getId().getType(), wra.getId().getId() + "", wra.getTemplate(), wrapper, "nav");
        ics.RemoveVar("gspal-url");
        gtu.setOutstr("gspal-url");
        gtu.execute(ics);
        String url = ics.GetVar("gspal-url");
        ics.RemoveVar("gspal-url");
        return url;
    }

    /**
     * Return the linktext to use for the web-referenceable asset
     *
     * @param wra WebReferenceableAsset bean
     * @return linktext
     */
    protected String getLinktextForWra(WebReferenceableAsset wra) {
        if (wra.getLinkTitle() != null && wra.getLinkTitle().length() > 0) {
            return wra.getLinkTitle();
        } else if (wra.getH1Title() != null && wra.getH1Title().length() > 0) {
            return wra.getH1Title();
        } else {
            LOG.warn("Could not retrieve linktext for WRA: " + wra + " (This is expected if the asset is not a web-referenceable asset).");
            return null;
        }
    }

    /**
     * Extracts attributes from the provided Alias asset. Separated into
     * its own method to facilitate overriding this method for custom
     * Alias assets or adding additional attributes.
     *
     * @param id
     * @return map containing string-object mappings for use in things like placing in page scope
     */
    protected Map<String, Object> extractAttrFromAlias(AssetId id) {
        Map<String, Object> result = new HashMap<String, Object>();
        Alias alias = wraUtils.getAlias(id);
        String url = getUrlForAlias(alias);
        String linktext = getLinktextForAlias(alias);
        result.put("bean", alias);
        if (url != null) result.put("url", url);
        if (linktext != null) result.put("linktext", linktext);
        return result;
    }

    /**
     * Extracts attributes from the provided wra asset. Separated into
     * its own method to facilitate overriding to add additional attributes.
     *
     * @param id asset
     * @return map containing string-object mappings for use in things like placing in page scope
     */
    protected Map<String, Object> extractAttrFromWra(AssetId id) {
        Map<String, Object> result = new HashMap<String, Object>();
        WebReferenceableAsset wra = wraUtils.getWra(id);
        String url = getUrlForWra(wra);
        String linktext = getLinktextForWra(wra);
        result.put("bean", wra);
        if (url != null) result.put("url", url);
        if (linktext != null) result.put("linktext", linktext);
        return result;
    }

    static final PreparedStmt FIND_P = new PreparedStmt("SELECT art.oid\n\tFROM AssetRelationTree art, AssetPublication ap, Publication p\n\tWHERE ap.assetid = art.oid\n\t" + "AND ap.assettype = 'Page'\n\t" + "AND ap.pubid = p.id\n\t" + "AND p.name = ?\n\t" + "AND art.otype = ap.assettype\n\t" + "AND art.nid in (\n\t\t" + "SELECT nparentid FROM AssetRelationtree WHERE otype=? AND oid=? AND ncode='-'\n\t) ORDER BY ap.id", Arrays.asList("AssetRelationTree", "AssetPublication", "Publication"));

    static {
        FIND_P.setElement(0, "Publication", "name");
        FIND_P.setElement(1, "AssetRelationTree", "otype");
        FIND_P.setElement(2, "AssetRelationTree", "oid");
    }

    /**
     * Locate the page that contains the specified Web-Referenceable Asset.
     * <p/>
     * A WRA is supposed to just be placed on one page (in the unnamed association
     * block), and this method locates it.  If it is not found, 0L is returned.
     * <p/>
     * If multiple matches are found, a warning is logged and the first one is
     * returned.
     *
     * @param site_name  name of the site to search within
     * @param wraAssetId the asset id of the web-referenceable asset
     * @return page asset ID or 0L.
     */
    public long findP(String site_name, AssetId wraAssetId) {
        final StatementParam param = FIND_P.newParam();
        param.setString(0, site_name);
        param.setString(1, wraAssetId.getType());
        param.setLong(2, wraAssetId.getId());
        long result = 0L;
        for (final Row r : SqlHelper.select(ics, FIND_P, param)) {
            if (result != 0L)
                LOG.warn("Asset " + wraAssetId + " was found as the primary content on multiple pages in the site.  Web-referenceable assets should only be the primary content on one Page to comply with SEO rules.  Automatically selecting the oldest page");
            else result = r.getLong("oid");
        }
        if (result == 0L) {
            // could not locate
        }
        return result;

    }
}
