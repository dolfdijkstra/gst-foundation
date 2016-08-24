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

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;
import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadByName;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;
import com.fatwire.mda.DimensionableAssetManager;
import com.fatwire.system.Session;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Handles Locale-specific functions efficiently in Java.
 *
 * @author Tony Field
 * @since May 8, 2009
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class LocaleUtils {
    private static final Logger _log = LoggerFactory.getLogger("tools.gsf.legacy.facade.mda.LocaleUtils");

    private LocaleUtils() {
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p>
     * If the desired translation is not available, null will be returned.
     * <p>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p>
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
     * <p>
     * If the desired translation is not available, null will be returned.
     * <p>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p>
     * Null, however, is a valid option.
     *
     * @param ics                              context
     * @param id                               id of asset to look up
     * @param preferredLocaleDimensionIdString id of locale desired
     * @param site                             name of site
     * @return AssetId of translation asset, or null if none is returned by the
     * dimension set filter.
     */
    public static AssetId findTranslation(ICS ics, AssetId id, String preferredLocaleDimensionIdString, String site) {
        if (preferredLocaleDimensionIdString == null) {
            throw new IllegalArgumentException("Required preferred locale dimension ID not provided");
        }
        long preferredDimension = Long.valueOf(preferredLocaleDimensionIdString);

        long dimensionSetId = locateDimensionSetForSite(ics, site);

        return findTranslation(ics, id, preferredDimension, dimensionSetId);
    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p>
     * If the desired translation is not available, null will be returned.
     * <p>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p>
     * Null, however, is a valid option.
     *
     * @param ics                Content Server context object
     * @param id                 id of asset to look up
     * @param preferredDimension id of locale desired
     * @param dimensionSetId     dimension set to use to find the translation
     * @return AssetId of translation asset, or null if none is returned by the
     * dimension set filter.
     */
    public static AssetId findTranslation(ICS ics, AssetId id, long preferredDimension, long dimensionSetId) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }

        DimensionableAssetManager mgr = DimensionUtils.getDAM(ics);

        if (_isInputAssetDimensionPreferred(mgr, id, preferredDimension)) {
            _log.debug("Input dimension is already in the preferred dimension.  Not invoking dimension set filter.  Asset: "
                    + id + ", dimension: " + preferredDimension);
            return id;
        } else {
            _log.debug("About to look for translations.  Input asset id: " + id + ", dimension set: " + dimensionSetId
                    + ", preferred dimension: " + preferredDimension);
        }

        // *****************************************************************************
        // The core business logic of this helper class is encapsulated in these
        // 3 lines
        DimensionSetInstance dimset = getDimensionSet(ics, dimensionSetId);
        return findTranslation(ics, id, preferredDimension, dimset);

    }

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p>
     * If the desired translation is not available, null will be returned.
     * <p>
     * If a dimension set has been configured that returns the asset other than
     * the preferred locale, that is considered to be fine and not really the
     * problem of the end user. In other words, a dimension set may dictate that
     * a "backup" language can be returned to the user.
     * <p>
     * Null, however, is a valid option.
     *
     * @param ics                Content Server context object
     * @param id                 id of asset to look up
     * @param preferredDimension id of locale desired
     * @param dimensionSetName   the name of the dimension set to use to find the
     *                           translation
     * @return AssetId of translation asset, or null if none is returned by the
     * dimension set filter. The id parameters is returned if the asset
     * does not have a locale or if the locale is already of the
     * preferredDimension
     */
    public static AssetId findTranslation(ICS ics, AssetId id, long preferredDimension, String dimensionSetName) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }
        Dimension locale = DimensionUtils.getLocaleAsDimension(ics, id);

        if (locale == null) {
            _log.debug("Asset is not localized.  Not invoking dimension set filter.  Asset: " + id);
            return id;
        }
        if (locale.getId().getId() == preferredDimension) {
            _log.debug("Input dimension is already in the preferred dimension.  Not invoking dimension set filter.  Asset: "
                    + id + ", dimension: " + preferredDimension);
            return id;
        } else {
            _log.debug("About to look for translations.  Input asset id: " + id + ", dimension set: "
                    + dimensionSetName + ", preferred dimension: " + preferredDimension);
        }

        // *****************************************************************************
        // The core business logic of this helper class is encapsulated in these
        // 3 lines
        DimensionSetInstance dimset = getDimensionSet(ics, dimensionSetName);

        return findTranslation(ics, id, preferredDimension, dimset);
    }

    /**
     * @param ics                Content Server context object
     * @param id                 asset id
     * @param preferredDimension id for preferred locale
     * @param dimset             dimension set instance
     * @return assetid of translated asset.
     * @throws IllegalStateException exception when illegal state is reached
     */
    public static AssetId findTranslation(ICS ics, AssetId id, long preferredDimension, DimensionSetInstance dimset)
            throws IllegalStateException {
        AssetId preferredDim = new AssetIdImpl("Dimension", preferredDimension);
        List<AssetId> preferredDims = Collections.singletonList(preferredDim);
        Collection<AssetId> relatives = findTranslation(DimensionUtils.getDM(ics), Collections.singletonList(id), preferredDims, dimset);
        // *****************************************************************************

        // make the result pretty
        if (relatives == null) {
            _log.warn("No translation found for asset " + id + " in dimension set " + dimset.getId()
                    + " for dimension " + preferredDimension + ".");
            return null;
        } else {
            switch (relatives.size()) {
                case 0: {
                    _log.warn("No translation found for " + id + " in dimension set " + dimset.getId()
                            + " for dimension " + preferredDimension + ".");
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
                    _log.trace("LocaleUtils.findTranslation: RELATIVE FOUND... " + relative.getType() + " '"
                            + relative.getId() + "' // errno = " + ics.GetErrno());
                    return relative;

                }
                default: {
                    throw new IllegalStateException("found more than one translation for asset " + id
                            + " and that is not supposed to be possible.");
                }
            }
        }
    }


    /**
     * Main translation lookup method.  Accesses the filter in the dimension set, configures it with the preferred
     * dimension IDs, then filters the input assets.
     *
     * @param dimensionManager      manager class for Dimension lookups
     * @param toFilterList          list of input assets that need to be translated.  Often it's just one, but a list is perfectly valid.
     * @param preferredDimensionIds preferred dimensions to be investigated for a result. Priority preference depends on the
     *                              configured filter
     * @param dimSet                DimensionSet to use for filtering.
     * @return list of assets based on the translation rules in the dimension filter from the specified dimension set.
     */
    public static Collection<AssetId> findTranslation(DimensionManager dimensionManager, List<AssetId> toFilterList, Collection<AssetId> preferredDimensionIds, DimensionSetInstance dimSet) {
        try {
            return DimensionUtils.filterAssets(dimensionManager, toFilterList, preferredDimensionIds, dimSet);
        } catch (DimensionException e) {
            throw new CSRuntimeException("Failed to translate assets.  Input assets:" + toFilterList + ", Preferred Dimensions: " + preferredDimensionIds + ", DimensionSet:" + dimSet, ftErrors.exceptionerr, e);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Helper functions

    private static boolean _isInputAssetDimensionPreferred(DimensionableAssetManager mgr, AssetId id,
                                                           long preferredDimension) {
        Dimension dim = DimensionUtils.getLocaleAsDimension(mgr, id);
        if (dim == null) {
            return true; // if locale not found, tell that the asset is expected
        }
        // locale
        return dim.getId().getId() == preferredDimension;
    }

    private static final PreparedStmt FIND_DIMSET_FOR_SITE_PREPAREDSTMT = new PreparedStmt(
            "select ds.id as id from DimensionSet ds, Publication p, AssetPublication ap where p.name = ? and p.id = ap.pubid and ap.assetid = ds.id and ds.status != 'VO' order by ds.updateddate",
            Arrays.asList("DimensionSet", "AssetPublication", "Publication"));

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
    public static long locateDimensionSetForSite(ICS ics, String site) {
        if (site == null) {
            throw new IllegalArgumentException("Required site name missing");
        }
        StatementParam params = FIND_DIMSET_FOR_SITE_PREPAREDSTMT.newParam();
        params.setString(0, site);
        IList results = ics.SQL(FIND_DIMSET_FOR_SITE_PREPAREDSTMT, params, true);
        int numRows = results != null && results.hasData() ? results.numRows() : 0;
        if (numRows == 0) {
            throw new IllegalStateException(
                    "A DimensionSet has not been defined for site '"
                            + site
                            + "'. Cannot determine any translation unless some locales (Dimensions) are enabled for that site. Aborting operation.");
        }
        if (numRows > 1) {
            String msg = "More than one dimension set found in site " + site
                    + ".  Exactly one is expected.  Dimension set ids: ";
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
        if (_log.isTraceEnabled()) {
            _log.trace("Looked up dimset for site " + site + " and found " + id);
        }
        return Long.valueOf(id);
    }

    private static DimensionFilterInstance _getPopulatedDimensionFilter(Session ses, DimensionSetInstance dimset,
                                                                        long localeDimensionId) {

        // Set the filter's preferred dimension
        // Equivalent to:
        // %><dimensionset:asset assettype="Dimension"
        // assetid="<%=localeDimensionId%>" /><%
        Dimension thePreferredDimension = ((DimensionManager) ses.getManager(DimensionManager.class.getName()))
                .loadDimension(localeDimensionId);
        if (thePreferredDimension == null) {
            throw new RuntimeException("Attempted to load Dimension with id " + localeDimensionId
                    + " but it came back null");
        }
        return _getPopulatedDimensionFilter(dimset, thePreferredDimension);
    }

    private static DimensionFilterInstance _getPopulatedDimensionFilter(DimensionSetInstance dimset,
                                                                        Dimension preferredDimension) {
        DimensionFilterInstance filter;
        try {
            filter = dimset.getFilter();
        } catch (DimensionException e) {
            throw new RuntimeException("Could not get Dimension Filter from DimensionSet", e);
        }

        filter.setDimensonPreference(Collections.singletonList(preferredDimension));
        return filter;
    }

    public static DimensionSetInstance getDimensionSet(ICS ics, long theDimSetId) {
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

    public static DimensionSetInstance getDimensionSet(ICS ics, String name) {
        final String DIMSET_OBJ_NAME = "LocaleUtils:findTranslation:theDimensionSet:DimensionSet";
        ics.SetObj(DIMSET_OBJ_NAME, null);
        AssetLoadByName a = new AssetLoadByName();
        a.setAssetType("DimensionSet");
        a.setAssetName(name);
        a.setEditable(false);
        a.setName(DIMSET_OBJ_NAME);
        a.execute(ics);

        if (ics.GetErrno() < 0) {
            throw new IllegalStateException("Could not load dimension set.  Errno: " + ics.GetErrno());
        }

        Object o = ics.GetObj(DIMSET_OBJ_NAME);
        ics.SetObj(DIMSET_OBJ_NAME, null);
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
