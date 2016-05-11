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

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.site.SiteInfo;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.navigation.NavigationNode;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.mda.DimensionFilterInstance;
import com.openmarket.xcelerate.asset.AssetIdImpl;


/**
 * 
 * @author Dolf Dijkstra
 * @deprecated replaced with {@link NavigationService}
 */
public abstract class AbstractNavigationHelper {

    /**
     * ICS context
     */
    protected final ICS ics;

    protected abstract NavigationNode getSitePlan(final int depth, final AssetId pageId, final int level,
            final DimensionFilterInstance dimensionFilter);

    protected final TemplateAssetAccess assetTemplate;
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
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractNavigationHelper.class);
    /**
     * Effective date for the purposes of startdate/enddate comparisons for an
     * asset.
     */
    protected final Date assetEffectiveDate;
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
     * Constant containing the asset type of the GST Alias asset.
     */
    public final String GST_ALIAS_TYPE = Alias.ALIAS_ASSET_TYPE_NAME;

    public AbstractNavigationHelper(final ICS ics) {
        this.ics = ics;
        this.wraDao = new AssetApiWraCoreFieldDao(ics);

        this.aliasDao = new AssetApiAliasCoreFieldDao(ics, wraDao);
        this.assetEffectiveDate = null;
        assetTemplate = new TemplateAssetAccess(ics);
    }

    /**
     * Constructor with all the dependencies listed. Initializes
     * assetEffectiveDate to null.
     * 
     * @param ics Content Server context object
     * @param assetTemplate template asset access object
     * @param wraDao WRA Core Field DAO
     * @param aliasDao Alias Core Field DAO
     */
    public AbstractNavigationHelper(final ICS ics, TemplateAssetAccess assetTemplate, WraCoreFieldDao wraDao,
            AliasCoreFieldDao aliasDao) {
        this.ics = ics;
        this.wraDao = wraDao;
        this.aliasDao = aliasDao;
        this.assetTemplate = assetTemplate;
        this.assetEffectiveDate = null;
    }

    /**
     * @param name the name of the Page asset
     * @return NavNode for the Page with the name
     */
    public NavigationNode getSitePlanByPage(final String name) {
        return getSitePlanByPage(1, name);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided name.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param name the name of the Page asset
     * @return NavNode for the Page with the name
     */
    public NavigationNode getSitePlanByPage(final int depth, final String name) {
        final String sitename = ics.GetVar("site");
        if (StringUtils.isBlank(sitename)) {
            throw new IllegalStateException(
                    "site is not a ics variable. This function needs this variable to be avaible and contain the name of the site.");
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
    public NavigationNode getSitePlanByPage(final int depth, final String name, final DimensionFilterInstance dimensionFilter) {
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
    public NavigationNode getSitePlanByPage(final int depth, final String name, final String sitename) {
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
    public NavigationNode getSitePlanByPage(final int depth, final String name, final String sitename,
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
    public NavigationNode getSitePlan(final String pageid) {
        return getSitePlan(new AssetIdImpl("Page", Long.parseLong(pageid)));
    }

    /**
     * Get the NavNode for the current page with unlimited depth.
     * 
     * @param pageid asset id containing page id
     * @return the NavNode associated with this pageid.
     */
    public NavigationNode getSitePlan(final AssetId pageid) {
        return getSitePlan(-1, pageid, 0, null);
    }

    /**
     * Retrieves the NavNode for the given Page with the provided id.
     * 
     * @param depth the maximum depth to retrieve, -1 for no limit.
     * @param pageid the AssetId for the page
     * @return the NavNode for this page
     */
    public NavigationNode getSitePlan(final int depth, final AssetId pageid) {
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
    public NavigationNode getSitePlan(final int depth, final AssetId pageid, final DimensionFilterInstance dimensionFilter) {
        LOG.debug("Dimension filter " + dimensionFilter + " provided for site plan lookup");
        return getSitePlan(depth, pageid, 0, dimensionFilter);
    }

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

}
