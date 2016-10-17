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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.site.SiteInfo;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetClosure;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.DateFilterClosure;
import com.fatwire.gst.foundation.facade.assetapi.asset.PreviewContext;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraUriBuilder;
import com.fatwire.mda.DimensionFilterInstance;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate.isValidOnDate;

/**
 * Used to retrieve the Navigation Bar data. See the description of
 * {@link #getSitePlan(String)} for more details.
 * 
 * @author David Chesebro
 * @author Dolf Dijkstra
 * @since Jun 17, 2010
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation (coming soon)
 * 
 */
@Deprecated
public class NavigationHelper {
    /**
     * ICS context
     */
    protected final ICS ics;

    final AssetAccessTemplate assetTemplate;
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
    protected final Logger LOG = LoggerFactory.getLogger("tools.gsf.legacy.wra.navigation.NavigationHelper");
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
        this.wraDao = new AssetApiWraCoreFieldDao(ics);

        this.aliasDao = new AssetApiAliasCoreFieldDao(ics, wraDao);
        this.assetEffectiveDate = null;
        assetTemplate = new AssetAccessTemplate(ics);
    }

    /**
     * Constructor with all the dependencies listed. Initializes
     * assetEffectiveDate to null.
     * 
     * @param ics Content Server context object
     * @param assetTemplate Asset Access Template
     * @param wraDao WRA DAO
     * @param aliasDao Alias Core Field DAO
     */
    public NavigationHelper(final ICS ics, AssetAccessTemplate assetTemplate, WraCoreFieldDao wraDao,
            AliasCoreFieldDao aliasDao) {
        this.ics = ics;
        this.wraDao = wraDao;
        this.aliasDao = aliasDao;
        this.assetTemplate = assetTemplate;
        this.assetEffectiveDate = null;
    }

    /**
     * Name of the page subtype indicating that this page is NOT rendered on the
     * site but is instead merely used to group navigation components on the
     * site.
     */
    public static final String NAVBAR_NAME = "GSTNavName";

    /**
     * Name of the page subtype indicating that this page is a Link, meaning
     * that the content is in the unnamed association
     */
    public static final String NAVBAR_LINK = "GSTNavLink";

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
        final String sitename = ics.GetVar("site");
        if (StringUtils.isBlank(sitename)) {
            throw new IllegalStateException(
                    "site is not a ics variable. This function needs this variable to be aviable and contain the name of the site.");
        }

        return getSitePlanByPage(depth, name, sitename);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @param dimensionFilter in order to translate the output.
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final int depth, final String name, final DimensionFilterInstance dimensionFilter) {
        final String sitename = ics.GetVar("site");
        if (StringUtils.isBlank(sitename)) {
            throw new IllegalStateException(
                    "site is not a ics variable. This function needs this variable to be aviable and contain the name of the site.");
        }

        return getSitePlanByPage(depth, name, sitename, dimensionFilter);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @param sitename the name of the site you want the navigation for.
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final int depth, final String name, final String sitename) {
        return getSitePlanByPage(depth, name, sitename, null);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @param sitename the name of the site you want the navigation for.
     * @param dimensionFilter in order to translate the output.
     * @return NavNode for the Page with the name
     */
    public NavNode getSitePlanByPage(final int depth, final String name, final String sitename,
            final DimensionFilterInstance dimensionFilter) {

        final SiteInfo site = assetTemplate.readSiteInfo(sitename);
        if (site == null) {
            throw new RuntimeException("Site with name '" + sitename + "' not found.");
        }
        final AssetId pageid = assetTemplate.findByName(ics, "Page", name, site.getId());
        if (pageid == null) {
            return null;
        }
        return getSitePlan(depth, pageid, dimensionFilter);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * The NavNode contains all the attributes necessary to create a nav bar.
     * <p>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p>
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
     * @param pageid AssetId object for the given page id
     * @return the NavNode associated with this pageid.
     */
    public NavNode getSitePlan(final AssetId pageid) {
        return getSitePlan(-1, pageid, 0, null);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param pageid the AssetId for the page
     * @return the NavNode for this page
     */
    public NavNode getSitePlan(final int depth, final AssetId pageid) {
        return getSitePlan(depth, pageid, 0, null);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param pageid the AssetId for the page
     * @param dimensionFilter in order to translate the output.
     * @return the NavNode for this page
     */
    public NavNode getSitePlan(final int depth, final AssetId pageid, final DimensionFilterInstance dimensionFilter) {
        LOG.debug("Dimension filter " + dimensionFilter + " provided for site plan lookup");
        return getSitePlan(depth, pageid, 0, dimensionFilter);
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
    private NavNode getSitePlan(final int depth, final AssetId pageId, final int level,
            final DimensionFilterInstance dimensionFilter) {
        // check the start/end date of the page asset
        LogDep.logDep(ics, pageId);
        if (!isValidOnDate(ics, pageId, assetEffectiveDate)) {
            // the input object is not valid. Abort
            if (LOG.isDebugEnabled()) {
                LOG.debug("Input asset " + pageId + " is not effective on " + assetEffectiveDate);
            }
            return null;
        }

        // determine if it's a wra, a placeholder or an alias

        final AssetData pageData = AssetDataUtils.getAssetData(ics, pageId, "subtype", "name");
        final String subtype = AttributeDataUtils.asString(pageData.getAttributeData("subtype"));
        final String name = AttributeDataUtils.asString(pageData.getAttributeData("name"));
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);
        final NavNode node = new NavNode();

        node.setPage(pageId);
        node.setLevel(level);
        node.setPagesubtype(subtype);
        node.setPagename(name);

        if (isNavigationPlaceholder) {
            // no link if it's just a placeholder
        } else {
            // not a GSTNavLink, probably 11g page
            final AssetClosure closure = new NodeWraAssetClosure(node);
            final DateFilterClosure dateFilterClosure = new DateFilterClosure(PreviewContext.getPreviewDate(ics,
                    assetEffectiveDate), closure);

            // retrieve the unnamed association(s) based on date filter
            if (dimensionFilter == null) {
                assetTemplate.readAssociatedAssets(pageId, "-", dateFilterClosure);
            } else {

                final Collection<AssetId> associatedWraList = assetTemplate.readAssociatedAssetIds(pageId, "-");
                for (final AssetId child : dimensionFilter.filterAssets(associatedWraList)) {
                    assetTemplate.readAsset(child, dateFilterClosure);
                }
            }

        }

        if (depth < 0 || depth > level) {
            // get the children in the Site Plan
            final List<AssetId> childrenIDs = ListPages.getChildPages(ics, pageId.getId());
            for (final AssetId aid : childrenIDs) {
                // note recursing here
                final NavNode kidInfo = getSitePlan(depth, aid, level + 1, dimensionFilter);
                if (kidInfo != null && kidInfo.getPage() != null) {
                    node.addChild(kidInfo);
                }
            }
        }
        return node;
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
     * Get the URL to use for the web-referenceable asset.
     * 
     * @param wra WebReferenceableAsset bean
     * @return url
     */
    protected String getUrlForWra(final VanityAsset wra) {
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

    protected void decorateAsWra(final AssetId id, final NavNode node) {

        final WebReferenceableAsset wra = wraDao.getWra(id);
        final String url = getUrlForWra(wra);
        final String linktext = wra.getLinkText();
        node.setWra(wra);
        if (url != null) {
            node.setUrl(url);
        }
        if (linktext != null) {
            node.setLinktext(linktext);
        }
    }

    protected void decorateAsAlias(final AssetId id, final NavNode node) {
        final Alias alias = aliasDao.getAlias(id);
        final String url = getUrlForAlias(alias);
        final String linktext = alias.getLinkText();
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
     * <p>
     * A WRA is supposed to just be placed on one page (in the unnamed
     * association block), and this method locates it. If it is not found, 0L is
     * returned.
     * <p>
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

    class NodeWraAssetClosure implements AssetClosure {

        private final NavNode node;

        public NodeWraAssetClosure(final NavNode node) {
            this.node = node;
        }

        @Override
        public boolean work(final AssetData asset) {
            final AssetId id = asset.getAssetId();
            node.setId(id);
            if (isGstAlias(id)) {
                decorateAsAlias(id, node);
            } else {
                decorateAsWra(id, node);
            }
            return false; // needs to return only one node
        }

    }

}
