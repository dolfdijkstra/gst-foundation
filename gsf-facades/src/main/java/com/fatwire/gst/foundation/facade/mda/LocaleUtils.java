/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.mda;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;
import com.fatwire.mda.DimensionableAssetManager;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles Locale-specific functions efficiently in Java.
 *
 * @author Tony Field
 * @since May 8, 2009
 */

public final class LocaleUtils {
    private static final Log _log = LogFactory.getLog(LocaleUtils.class);

    private LocaleUtils() {
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     *
     * @param c                          asset type of asset to look up
     * @param cid                        asset id of asset to look up
     * @param preferredLocaleDimensionId id of locale desired
     * @param site                       name of site
     * @return AssetId of translation asset.
     * @deprecated Use #findTranslation(ICS,String,String,String,String)
     *             instead. This version is unable to correctly record
     *             compositional dependencies on the DimensionSet asset for the
     *             site, which can result in the translation function not
     *             flushing pages when the dimension set is altered (for
     *             example, to change the filter, or change enabled dimensions).
     */
    public static AssetId findTranslation(String c, String cid, String preferredLocaleDimensionId, String site) {
        return findTranslation(ICSFactory.newICS(), new AssetIdImpl(c, Long.valueOf(cid)), preferredLocaleDimensionId, site);
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     *
     * @param c                          asset type of asset to look up
     * @param cid                        asset id of asset to look up
     * @param preferredLocaleDimensionId id of locale desired
     * @param site                       name of site
     * @param ics                        ics context
     * @return AssetId of translation asset.
     */
    public static AssetId findTranslation(ICS ics, String c, String cid, String preferredLocaleDimensionId, String site) {
        return findTranslation(ics, new AssetIdImpl(c, Long.valueOf(cid)), preferredLocaleDimensionId, site);
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     *
     * @param id   id of asset to look up
     * @param preferredLocaleDimensionIdString
     *             id of locale desired
     * @param site name of site
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter.
     * @deprecated Use #findTranslation(ICS,AssetId,String,String) instead. This
     *             version is unable to correctly record compositional
     *             dependencies on the DimensionSet asset for the site, which
     *             can result in the translation function not flushing pages
     *             when the dimension set is altered (for example, to change the
     *             filter, or change enabled dimensions).
     */
    public static AssetId findTranslation(AssetId id, String preferredLocaleDimensionIdString, String site) {
        ICS ics = ICSFactory.newICS();
        if (preferredLocaleDimensionIdString == null) {
            throw new IllegalArgumentException("Required preferred locale dimension ID not provided");
        }
        long preferredDimension = Long.valueOf(preferredLocaleDimensionIdString);

        // Generate an ICS object out of "nowhere"
        long dimensionSetId = _locateDimensionSetForSite(ics, site);

        return findTranslation(ics, id, preferredDimension, dimensionSetId);
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     *
     * @param ics  context
     * @param id   id of asset to look up
     * @param preferredLocaleDimensionIdString
     *             id of locale desired
     * @param site name of site
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter.
     */
    public static AssetId findTranslation(ICS ics, AssetId id, String preferredLocaleDimensionIdString, String site) {
        if (preferredLocaleDimensionIdString == null) {
            throw new IllegalArgumentException("Required preferred locale dimension ID not provided");
        }
        long preferredDimension = Long.valueOf(preferredLocaleDimensionIdString);

        // Generate an ICS object out of "nowhere"
        long dimensionSetId = _locateDimensionSetForSite(ics, site);

        return findTranslation(ics, id, preferredDimension, dimensionSetId);
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     *
     * @param id                 id of asset to look up
     * @param preferredDimension id of locale desired
     * @param dimensionSetId     dimension set to use to find the translation
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter.
     */
    public static AssetId findTranslation(ICS ics, AssetId id, long preferredDimension, long dimensionSetId) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }
        Session ses = SessionFactory.getSession(ics);
        DimensionableAssetManager mgr = (DimensionableAssetManager) ses.getManager(DimensionableAssetManager.class.getName());

        if (_isInputAssetDimensionPreferred(mgr, id, preferredDimension)) {
            _log.debug("Input dimension is already in the preferred dimension.  Not invoking dimension set filter.  Asset: " + id + ", dimension: " + preferredDimension);
            return id;
        } else {
            _log.debug("About to look for translations.  Input asset id: " + id + ", dimension set: " + dimensionSetId + ", preferred dimension: " + preferredDimension);
        }

        // *****************************************************************************
        // The core business logic of this helper class is encapsulated in these
        // 3 lines
        DimensionSetInstance dimset = _getDimensionSet(ics, dimensionSetId);
        DimensionFilterInstance filter = _getPopulatedDimensionFilter(ses, dimset, preferredDimension);
        // Get the relatives using the appropriate filter
        Collection<AssetId> relatives = mgr.getRelatives(id, filter, "Locale");
        // *****************************************************************************

        // make the result pretty
        if (relatives == null) {
            _log.warn("No translation found for asset " + id + " in dimension set " + dimensionSetId + " for dimension " + preferredDimension + ".");
            return null;
        } else {
            switch (relatives.size()) {
                case 0: {
                    _log.warn("No translation found for " + id + " in dimension set " + dimensionSetId + " for dimension " + preferredDimension + ".");
                    // Note May 4, 2010 by Tony Field - this had been changed to
                    // return the input ID but that
                    // is incorrect. The contract clearly states that null is to
                    // be returned if no matching
                    // relatives are found. When null is returned and it is not
                    // expected, often the incorrect
                    // dimension filter is configured.
                    return null;
                }
                case 1: {
                    AssetId relative = relatives.iterator().next();
                    _log.trace("LocaleUtils.findTranslation: RELATIVE FOUND... " + relative.getType() + " '" + relative.getId() + "' // errno = " + ics.GetErrno());
                    return relative;

                }
                default: {
                    throw new IllegalStateException("found more than one translation for asset " + id + " and that is not supposed to be possible.");
                }
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Helper functions

    private static boolean _isInputAssetDimensionPreferred(DimensionableAssetManager mgr, AssetId id, long preferredDimension) {
        Collection<Dimension> in_dims = mgr.getDimensionsForAsset(id);
        for (Dimension dim : in_dims) {
            long in_dim_id = dim.getId().getId();
            if (preferredDimension == in_dim_id) {
                return true;
            }
        }
        return false;
    }

    private static final PreparedStmt FIND_DIMSET_FOR_SITE_PREPAREDSTMT = new PreparedStmt("select ds.id as id from dimensionset ds, publication p, assetpublication ap where p.name = ? and p.id = ap.pubid and ap.assetid = ds.id and ds.status != 'VO' order by ds.updateddate", Arrays.asList("DimensionSet", "AssetPublication", "Publication"));

    static {
        FIND_DIMSET_FOR_SITE_PREPAREDSTMT.setElement(0, "Publication", "name");
    }

    /**
     * Locates a single dimension set in a site. If no match is found, an
     * exception is thrown. If more than one match is found, an exception is
     * thrown.
     *
     * @param ics  context
     * @param site site containing a dimension set
     * @return DimensionSet ID
     */
    private static long _locateDimensionSetForSite(ICS ics, String site) {
        if (site == null) {
            throw new IllegalArgumentException("Required site name missing");
        }
        StatementParam params = FIND_DIMSET_FOR_SITE_PREPAREDSTMT.newParam();
        params.setString(0, site);
        IList results = ics.SQL(FIND_DIMSET_FOR_SITE_PREPAREDSTMT, params, true);
        int numRows = results != null && results.hasData() ? results.numRows() : 0;
        if (numRows == 0) {
            throw new IllegalStateException("A DimensionSet has not been defined for site '" + site + "'. Cannot determine any translation unless some locales (Dimensions) are enabled for that site. Aborting operation.");
        }
        if (numRows > 1) {
            String msg = "More than one dimension set found in site " + site + ".  Exactly one is expected.  Dimension set ids: ";
            for (IList row : new IterableIListWrapper(results)) {
                String id = IListUtils.getStringValue(row, "id");
                LogDep.logDep(ics, "DimensionSet", id);
                msg += id + " ";
            }
            throw new IllegalStateException(msg + ".");
        }
        results.moveTo(1);
        String id = IListUtils.getStringValue(results, "id");
        LogDep.logDep(ics, "DimensionSet", id);
        return Long.valueOf(id);
    }

    private static DimensionFilterInstance _getPopulatedDimensionFilter(Session ses, DimensionSetInstance dimset, long localeDimensionId) {
        DimensionFilterInstance filter;
        try {
            filter = dimset.getFilter();
        } catch (DimensionException e) {
            throw new RuntimeException("Could not get Dimension Filter from DimensionSet", e);
        }

        // Set the filter's preferred dimension
        // Equivalent to:
        // %><dimensionset:asset assettype="Dimension"
        // assetid="<%=localeDimensionId%>" /><%
        Dimension thePreferredDimension = ((DimensionManager) ses.getManager(DimensionManager.class.getName())).loadDimension(localeDimensionId);
        if (thePreferredDimension == null) {
            throw new RuntimeException("Attempted to load Dimension with id " + localeDimensionId + " but it came back null");
        }
        filter.setDimensonPreference(Collections.singletonList(thePreferredDimension));
        return filter;
    }

    private static DimensionSetInstance _getDimensionSet(ICS ics, long theDimSetId) {
        final String DIMSET_OBJ_NAME = "LocaleUtils:findTranslation:theDimensionSet:DimensionSet";

        // Load the site-specific DimensionSet asset
        ics.SetObj(DIMSET_OBJ_NAME, null); // clear first
        FTValList args = new FTValList();
        args.put("NAME", DIMSET_OBJ_NAME);
        args.put("TYPE", "DimensionSet");
        args.put("OBJECTID", Long.toString(theDimSetId));
        args.put("EDITABLE", "FALSE");
        ics.runTag("ASSET.LOAD", args);

        if (ics.GetErrno() < 0) {
            throw new IllegalStateException("Could not load dimension set.  Errno: " + ics.GetErrno());
        }

        Object o = ics.GetObj(DIMSET_OBJ_NAME);
        if (o == null) {
            throw new IllegalStateException("Could not load dimension set but we got no errno... unexpected...");
        }

        DimensionSetInstance dimset;
        if (o instanceof DimensionSetInstance) {
            dimset = (DimensionSetInstance) o;
        } else {
            throw new IllegalStateException("Loaded an asset that is not a Dimension Set");
        }
        return dimset;
    }
}
