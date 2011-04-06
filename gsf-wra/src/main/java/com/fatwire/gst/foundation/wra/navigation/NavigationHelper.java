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
package com.fatwire.gst.foundation.wra.navigation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.site.Site;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.Children;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraUriBuilder;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate.isValidOnDate;

/**
 * Used to retrieve the Navigation Bar data. See the description of
 * {@link #getSitePlan(String)} for more details.
 * <p/>
 * TODO: low priority: add multilingual support
 * <p/>
 * 
 * @author David Chesebro
 * @author Dolf Dijkstra
 * @since Jun 17, 2010
 */
public class NavigationHelper {
    /**
     * ICS context
     */
    protected final ICS ics;
    /**
     * Local instance of the WraCoreFieldDao, pre-instantiated and ready to go
     */
    protected final WraCoreFieldDao wraDao;

    /**
     * Local instance of the AliasCoreFieldDao.
     */
    protected final AliasCoreFieldDao aliasDao;
    /**
     * Log file
     */
    protected final Log LOG = LogFactory.getLog(NavigationHelper.class);
    /**
     * Effective date for the purposes of startdate/enddate comparisons for an
     * asset.
     */
    protected final Date assetEffectiveDate;

    /**
     * Constructor. Initializes assetEffectiveDate to null.
     * 
     * @param ics object
     */
    public NavigationHelper(final ICS ics) {
        this.ics = ics;
        this.wraDao = new WraCoreFieldDao(ics);
        aliasDao = new AliasCoreFieldDao(ics);
        this.assetEffectiveDate = null;
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
     * <li><code>level</code>: int the number of levels down the site plan tree
     * of the page asset (starting with the pageid you originally pass in =
     * level 0)</li>
     * <li><code>id</code>: AssetId of asset associated to the Page in the
     * unnamed association field. Should be either a WRA or an alias</li>
     * <li><code>bean</code>: WebReferenceableAsset object containing the field
     * data of the unnamed associated asset.</li>
     * <li><code>url</code>: String url for the nav entry</li>
     * <li><code>linktext</code>: String linktext for the nav entry. Images are
     * not supported.</li>
     * <li><code>children</code>: (a List<Map<String,Object>> of the children
     * (in the site plan tree) of the page, where each Map contains the above
     * attributes</li>
     * </ul>
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p/>
     * StartDate and EndDate are checked and invalid pages aren't added. If a
     * Page asset is not valid, its children are not even examined.
     * 
     * @param pageid AssetId of (usually a page) in the site plan tree to start
     *            with. Typically this would be a nav name. The nav name would
     *            be included in the output object. Recursion is automatic
     * @return Map<String,Object> of the site plan tree (see above)
     * @deprecated replaced by {@link #getSitePlan(String)}.
     */
    @Deprecated
    public Map<String, Object> getSitePlanAsMap(final String pageid) {
        return getSitePlanAsMap(pageid, 0);
    }

    /**
     * @param name the name of the Page asset
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final String name) {
        return getSitePlanByPage(1, name);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final int depth, final String name) {
        String sitename = ics.GetVar("site");
        if (StringUtils.isBlank(sitename))
            throw new IllegalStateException(
                    "site is not a ics variable. This function needs this variable to be aviable and contain the name of the site.");

        return getSitePlanByPage(depth, name, ics.GetVar("site"));
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @param sitename the name of the site you want the navigation for.
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final int depth, final String name, String sitename) {
        final AssetAccessTemplate assetTemplate = new AssetAccessTemplate(ics);
        Site site = assetTemplate.readSite(sitename);
        if (site == null)
            throw new RuntimeException("Site with name '" + sitename + "' not found.");
        final AssetId pageid = assetTemplate.findByName(ics, "Page", name, site.getId());
        return getSitePlan(depth, pageid);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * The NavNode contains all the attributes necessary to create a nav bar.
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p/>
     * StartDate and EndDate are checked and invalid pages aren't added. If a
     * Page asset is not valid, its children are not even examined.
     * 
     * 
     * @param pageid the assetid of the Page asset.
     * @return the NavNode for this page
     */
    public NavNode getSitePlan(final String pageid) {
        return getSitePlan(new AssetIdImpl("Page", Long.parseLong(pageid)));
    }

    /**
     * Get the NavNode for the current page with unlimited depth.
     * 
     * @param pageid
     * @return the NavNode associated with this pageid.
     */
    public NavNode getSitePlan(final AssetId pageid) {
        return getSitePlan(-1, pageid, 0);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param pageid the AssetId for the page
     * @return the NavNode for this page
     */
    public NavNode getSitePlan(final int depth, final AssetId pageid) {
        return getSitePlan(depth, pageid, 0);
    }

    /**
     * Called from public {@link #getSitePlan(int, AssetId)}. See that
     * function's description for details
     * 
     * @param depth the depth of the tree to retrieve, -1 for unlimited depth.
     * @param pageId id of the page asset
     * @param level starting level number when traversing the site plan tree
     * @return NavNode of the site plan tree
     */
    private NavNode getSitePlan(final int depth, final AssetId pageId, final int level) {
        LogDep.logDep(ics, pageId);
        if (!isValidOnDate(ics, pageId, assetEffectiveDate)) {
            // the input object is not valid. Abort
            if (LOG.isDebugEnabled()) {
                LOG.debug("Input asset " + pageId + " is not effective on " + assetEffectiveDate);
            }
            return null;
        }

        // determine if it's a wra, a placeholder or an alias

        final AssetData pageData = AssetDataUtils.getAssetData(pageId, "subtype", "name");
        final String subtype = AttributeDataUtils.asString(pageData.getAttributeData("subtype"));
        final String name = AttributeDataUtils.asString(pageData.getAttributeData("name"));
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);
        final NavNode node = new NavNode();
        if (isNavigationPlaceholder) {
            node.setPage(pageId);
            node.setLevel(level);
            node.setPagesubtype(subtype);
            node.setPagename(name);
        } else {
            // no link if it's just a placeholder
            // retrieve the unnamed association(s)
            final List<AssetId> ids = Children.getOptionalMultivaluedAssociation(ics, "Page", Long.toString(pageId
                    .getId()), "-");
            if (ids.size() < 1) {
                // tolerate bad data
                LOG.warn("Page " + pageId.getId()
                        + " has no unnamed association value so a link cannot be generated for it.  Skipping.");
            } else {
                final ArrayList<AssetId> wra = new ArrayList<AssetId>();
                for (final AssetId id : ids) {
                    if (isValidOnDate(ics, id, assetEffectiveDate)) {
                        wra.add(id);
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Page content " + id + " is not effective on date " + assetEffectiveDate);
                    }

                }
                if (wra.size() < 1) {
                    LOG.debug("Page " + pageId.getId() + " does not have any valid unnamed associations for date "
                            + assetEffectiveDate + ", so a link cannot be generated for it.  Skipping.");
                } else if (wra.size() > 1) {
                    LOG.warn("Page " + pageId.getId()
                            + " has more than one unnamed association that is valid for date " + assetEffectiveDate
                            + ", so no link can be generated for it.  Skipping.");
                } else {
                    // NavNode node = new NavNode();
                    node.setPage(pageId);
                    node.setLevel(level);
                    node.setPagesubtype(subtype);
                    node.setPagename(name);
                    final AssetId id = wra.get(0);
                    node.setId(id);
                    if (isGstAlias(id)) {
                        decorateAsAlias(id, node);
                    } else {
                        decorateAsWra(id, node);
                    }
                }

            }
        }

        if (depth < 0 || depth > level) {
            // get the children in the Site Plan
            final List<AssetId> childrenIDs = ListPages.getChildPages(ics, pageId.getId());
            for (final AssetId aid : childrenIDs) {
                // note recursing here
                final NavNode kidInfo = getSitePlan(depth, aid, level + 1);
                if (kidInfo != null && kidInfo.getPage() != null) {
                    node.addChild(kidInfo);
                }
            }
        }
        return node;
    }

    /**
     * 
     * @param pageid
     * @param level
     * @return
     * @deprecated
     */
    @Deprecated
    private Map<String, Object> getSitePlanAsMap(final String pageid, final int level) {
        LogDep.logDep(ics, "Page", pageid);
        // object to hold results
        final Map<String, Object> result = new HashMap<String, Object>();
        final AssetId pageId = new AssetIdImpl("Page", Long.parseLong(pageid));
        if (!isValidOnDate(ics, pageId, assetEffectiveDate)) {
            // the input object is not valid. Abort
            if (LOG.isDebugEnabled()) {
                LOG.debug("Input asset " + pageId + " is not effective on " + assetEffectiveDate);
            }
            return result;
        }

        // determine if it's a wra, a placeholder or an alias
        final AssetData pageData = AssetDataUtils.getAssetData(pageId, "subtype", "name");
        final String subtype = pageData.getAttributeData("subtype").getData().toString();
        final String name = pageData.getAttributeData("name").getData().toString();
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);

        if (isNavigationPlaceholder) {
            result.put("page", pageId);
            result.put("level", level);
            result.put("pagesubtype", subtype);
            result.put("pagename", name);
        } else {
            // no link if it's just a placeholder
            // retrieve the unnamed association(s)
            final List<AssetId> ids = Children.getOptionalMultivaluedAssociation(ics, "Page", pageid, "-");
            if (ids.size() < 1) {
                // tolerate bad data
                LOG.warn("Page " + pageid
                        + " has no unnamed association value so a link cannot be generated for it.  Skipping.");
            } else {
                final ArrayList<AssetId> wra = new ArrayList<AssetId>();
                for (final AssetId id : ids) {
                    if (isValidOnDate(ics, id, assetEffectiveDate)) {
                        wra.add(id);
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug("Page content " + id + " is not effective on date " + assetEffectiveDate);
                    }

                }
                if (wra.size() < 1) {
                    LOG.debug("Page " + pageid + " does not have any valid unnamed associations for date "
                            + assetEffectiveDate + ", so a link cannot be generated for it.  Skipping.");
                } else if (wra.size() > 1) {
                    LOG.warn("Page " + pageid + " has more than one unnamed association that is valid for date "
                            + assetEffectiveDate + ", so no link can be generated for it.  Skipping.");
                } else {
                    result.put("page", pageId);
                    result.put("level", level);
                    result.put("pagesubtype", subtype);
                    result.put("pagename", name);
                    final AssetId id = wra.get(0);
                    result.put("id", id);
                    if (isGstAlias(id)) {
                        result.putAll(extractAttrFromAlias(id));
                    } else {
                        result.putAll(extractAttrFromWra(id));
                    }
                }

            }
        }

        // get the children in the Site Plan
        final List<AssetId> childrenIDs = ListPages.getChildPages(ics, Long.parseLong(pageid));
        final List<Map<String, Object>> navChildren = new ArrayList<Map<String, Object>>();
        for (final AssetId aid : childrenIDs) {
            final String childPageID = Long.toString(aid.getId());
            // note recursing here
            final Map<String, Object> kidInfo = getSitePlanAsMap(childPageID, level + 1);
            if (kidInfo.keySet().size() > 0) {
                navChildren.add(kidInfo);
            }
        }
        if (navChildren.size() > 0) {
            result.put("children", navChildren);
        }
        return result;
    }

    /**
     * Constant containing the asset type of the GST Alias asset.
     */
    public final String GST_ALIAS_TYPE = Alias.ALIAS_ASSET_TYPE_NAME;

    /**
     * Return true if the asset type is a GSTAlias asset type. May be overridden
     * if customers are attempting to retrofit this class for alias-like
     * functionality that is not implemented by the GSTAlias asset type.
     * 
     * @param id asset for which a link is required
     * @return true if the asset is an alias, false if it is a web-referenceable
     *         asset
     */
    protected boolean isGstAlias(final AssetId id) {
        return GST_ALIAS_TYPE.equals(id.getType());
    }

    /**
     * Get the URL for the alias.
     * 
     * For external links, the targeturl attribute is rendered.
     * 
     * For Aliases that refer to another WRA, the alias is allowed to override
     * any WRA fields. For instance, the path, and the template can be
     * overridden by an alias for a WRA (though the template in the Alias had
     * better be typeless, or a template of the same name must exist in the
     * WRA's asset type or there will be a problem).
     * 
     * @param alias Alias bean, which of course is also a WRA.
     * @return url
     */
    protected String getUrlForAlias(final Alias alias) {
        if (alias.getTargetUrl() != null) {
            return alias.getTargetUrl();
        } else {
            return getUrlForWra(alias);
        }
    }

    /**
     * Get the linktext for the specified alias asset. If only linkimage is
     * specified and there is no way to locate the linktext, null is returned.
     * <p/>
     * If linktext is specified in the alias, it is returned. If it is not, the
     * target linktext is returned. If no target is found and linktext is not
     * specified, null is returned and a warning is issued.
     * 
     * @param alias Alias bean
     * @return linktext or null on failure.
     * @deprecated See {@link #getLinktext}
     */
    @Deprecated
    protected String getLinktextForAlias(final Alias alias) {
        return alias.getLinkText();
    }

    /**
     * Get the URL to use for the web-referenceable asset.
     * 
     * @param wra WebReferenceableAsset bean
     * @return url
     */
    protected String getUrlForWra(final WebReferenceableAsset wra) {
        if (wra.getTemplate() == null || wra.getTemplate().length() == 0) {
            LOG.warn("Asset " + wra + " does not have a valid template set.");
            return null;
        }
        String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher",
                "ServletRequest.properties", true);
        if (!Utilities.goodString(wrapper)) {
            wrapper = "GST/Dispatcher";
        }
        return new WraUriBuilder(wra, wrapper).toURI(ics);

    }

    /**
     * Return the linktext to use for the web-referenceable asset
     * 
     * @param wra WebReferenceableAsset bean
     * @return linktext
     * @deprecated See {@link #getLinktext}
     */
    @Deprecated
    protected String getLinktextForWra(final WebReferenceableAsset wra) {
        if (wra.getLinkText() != null && wra.getLinkText().length() > 0) {
            return wra.getLinkText();
        } else if (wra.getH1Title() != null && wra.getH1Title().length() > 0) {
            return wra.getH1Title();
        } else {
            LOG.warn("Could not retrieve linktext for WRA: " + wra
                    + " (This is expected if the asset is not a web-referenceable asset).");
            return null;
        }
    }

    /**
     * Return the linktext for the WRA. Note that aliases extend WRAs.
     * 
     * @param wra WebReferenceableAsset or Alias
     * @return linktext
     */
    protected String getLinktext(final WebReferenceableAsset wra) {
        return wra.getLinkText();
    }

    /**
     * Extracts attributes from the provided Alias asset. Separated into its own
     * method to facilitate overriding this method for custom Alias assets or
     * adding additional attributes.
     * 
     * @param id
     * @return map containing string-object mappings for use in things like
     *         placing in page scope
     * @deprecated
     */
    @Deprecated
    protected Map<String, Object> extractAttrFromAlias(final AssetId id) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final Alias alias = aliasDao.getAlias(id);
        final String url = getUrlForAlias(alias);
        final String linktext = getLinktextForAlias(alias);
        result.put("bean", alias);
        if (url != null) {
            result.put("url", url);
        }
        if (linktext != null) {
            result.put("linktext", linktext);
        }
        return result;
    }

    /**
     * Extracts attributes from the provided wra asset. Separated into its own
     * method to facilitate overriding to add additional attributes.
     * 
     * @param id asset
     * @return map containing string-object mappings for use in things like
     *         placing in page scope
     * @deprecated
     */
    @Deprecated
    protected Map<String, Object> extractAttrFromWra(final AssetId id) {
        final Map<String, Object> result = new HashMap<String, Object>();
        final WebReferenceableAsset wra = wraDao.getWra(id);
        final String url = getUrlForWra(wra);
        final String linktext = getLinktextForWra(wra);
        result.put("bean", wra);
        if (url != null) {
            result.put("url", url);
        }
        if (linktext != null) {
            result.put("linktext", linktext);
        }
        return result;
    }

    protected void decorateAsWra(final AssetId id, final NavNode node) {

        final WebReferenceableAsset wra = wraDao.getWra(id);
        final String url = getUrlForWra(wra);
        final String linktext = getLinktextForWra(wra);
        node.setWra(wra);
        if (url != null) {
            node.setUrl(url);// result.put("url", url);
        }
        if (linktext != null) {
            node.setLinktext(linktext);// result.put("linktext", linktext);
        }
    }

    protected void decorateAsAlias(final AssetId id, final NavNode node) {
        final Alias alias = aliasDao.getAlias(id); // wraUtils.getAlias(id);
        final String url = getUrlForAlias(alias);
        final String linktext = getLinktextForAlias(alias);
        node.setWra(alias);
        if (url != null) {
            node.setUrl(url);
        }
        if (linktext != null) {
            node.setLinktext(linktext);
        }

    }

    /**
     * Locate the page that contains the specified Web-Referenceable Asset.
     * <p/>
     * A WRA is supposed to just be placed on one page (in the unnamed
     * association block), and this method locates it. If it is not found, 0L is
     * returned.
     * <p/>
     * If multiple matches are found, a warning is logged and the first one is
     * returned.
     * 
     * @param site_name name of the site to search within
     * @param wraAssetId the asset id of the web-referenceable asset
     * @return page asset ID or 0L.
     */
    public long findP(final String site_name, final AssetId wraAssetId) {
        return wraDao.findP(new AssetIdWithSite(wraAssetId.getType(), wraAssetId.getId(), site_name));
    }
}
