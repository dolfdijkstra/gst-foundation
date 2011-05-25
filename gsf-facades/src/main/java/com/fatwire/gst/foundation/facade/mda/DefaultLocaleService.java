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

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadByName;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;
import com.fatwire.mda.DimensionableAssetManager;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultLocaleService implements LocaleService {
    private static final Log _log = LogFactory.getLog(DefaultLocaleService.class.getPackage().getName());

    private final ICS ics;
    private Session session;

    /**
     * @param ics
     */
    public DefaultLocaleService(final ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ICS must not be null.");
        }
        this.ics = ics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(java
     * .lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public AssetId findTranslation(final String c, final String cid, final String site,
            final String preferredLocaleDimensionId) {
        return findTranslation(new AssetIdImpl(c, Long.parseLong(cid)), site,
                Long.parseLong(preferredLocaleDimensionId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(com
     * .fatwire.assetapi.data.AssetId, java.lang.String, long)
     */

    public AssetId findTranslation(final AssetId id, final String site, final long preferredDimension) {

        final long dimensionSetId = _locateDimensionSetForSite(site);

        return findTranslation(id, preferredDimension, dimensionSetId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(com
     * .fatwire.assetapi.data.AssetId, long, long)
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final long dimensionSetId) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }

        if (_isInputAssetDimensionPreferred(id, preferredDimension)) {
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
        final DimensionSetInstance dimset = _getDimensionSet(dimensionSetId);
        return findTranslation(id, preferredDimension, dimset);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(com
     * .fatwire.assetapi.data.AssetId, long, java.lang.String)
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final String dimensionSetName) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }
        final Dimension locale = getLocaleForAsset(id);

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
        final DimensionSetInstance dimset = getDimensionSet(dimensionSetName);

        return findTranslation(id, preferredDimension, dimset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(com
     * .fatwire.assetapi.data.AssetId, long,
     * com.fatwire.mda.DimensionSetInstance)
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final DimensionSetInstance dimset) {

        final DimensionFilterInstance filter = _getPopulatedDimensionFilter(dimset, preferredDimension);
        // Get the relatives using the appropriate filter
        final Collection<AssetId> relatives = getDAM().getRelatives(id, filter, "Locale");
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
                    final AssetId relative = relatives.iterator().next();
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

    // ///////////////////////////////////////////////////////////////////////////
    // Helper functions

    private boolean _isInputAssetDimensionPreferred(final AssetId id, final long preferredDimension) {
        final Dimension dim = getLocaleForAsset(id);
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
     * @param site site containing a dimension set
     * @return DimensionSet ID
     */
    private long _locateDimensionSetForSite(final String site) {
        if (StringUtils.isBlank(site)) {
            throw new IllegalArgumentException("Required site name missing");
        }
        final StatementParam params = FIND_DIMSET_FOR_SITE_PREPAREDSTMT.newParam();
        params.setString(0, site);
        final IListIterable list = SqlHelper.select(ics, FIND_DIMSET_FOR_SITE_PREPAREDSTMT, params);

        final int numRows = list.size();
        if (numRows == 0) {
            throw new IllegalStateException(
                    "A DimensionSet has not been defined for site '"
                            + site
                            + "'. Cannot determine any translation unless some locales (Dimensions) are enabled for that site. Aborting operation.");
        }
        if (numRows > 1) {
            final StringBuilder msg = new StringBuilder("More than one dimension set found in site " + site
                    + ".  Exactly one is expected.  Dimension set ids: ");
            for (final Row row : list) {
                final String id = row.getString("id");
                LogDep.logDep(ics, "DimensionSet", id);
                msg.append(id).append(" ");
            }
            throw new IllegalStateException(msg.append(".").toString());
        }

        final String id = list.iterator().next().getString("id");
        LogDep.logDep(ics, "DimensionSet", id);
        return Long.parseLong(id);
    }

    private DimensionFilterInstance _getPopulatedDimensionFilter(final DimensionSetInstance dimset,
            final long dimensionId) {

        // Set the filter's preferred dimension
        // Equivalent to:
        // %><dimensionset:asset assettype="Dimension"
        // assetid="<%=localeDimensionId%>" /><%
        final Dimension preferredDimension = getDM().loadDimension(dimensionId);
        if (preferredDimension == null) {
            throw new RuntimeException("Attempted to load Dimension with id " + dimensionId + " but it came back null");
        }
        return _getPopulatedDimensionFilter(dimset, preferredDimension);
    }

    private DimensionFilterInstance _getPopulatedDimensionFilter(final DimensionSetInstance dimset,
            final Dimension preferredDimension) {
        DimensionFilterInstance filter;
        try {
            filter = dimset.getFilter();
        } catch (final DimensionException e) {
            throw new RuntimeException("Could not get Dimension Filter from DimensionSet", e);
        }

        filter.setDimensonPreference(Collections.singletonList(preferredDimension));
        return filter;
    }

    private DimensionSetInstance _getDimensionSet(final long dimSetId) {
        final String DIMSET_OBJ_NAME = "LocaleUtils:findTranslation:theDimensionSet:DimensionSet";
        // Load the site-specific DimensionSet asset
        ics.SetObj(DIMSET_OBJ_NAME, null); // clear first
        final FTValList args = new FTValList();
        args.put("NAME", DIMSET_OBJ_NAME);
        args.put("TYPE", "DimensionSet");
        args.put("OBJECTID", Long.toString(dimSetId));
        args.put("EDITABLE", "FALSE");
        ics.runTag("ASSET.LOAD", args);

        if (ics.GetErrno() < 0) {
            throw new IllegalStateException("Could not load dimension set.  Errno: " + ics.GetErrno());
        }

        final Object o = ics.GetObj(DIMSET_OBJ_NAME);
        ics.SetObj(DIMSET_OBJ_NAME, null);
        if (o == null) {
            throw new IllegalStateException("Could not load dimension set but we got no errno... unexpected...");
        }

        DimensionSetInstance dimset;
        if (o instanceof DimensionSetInstance) {
            dimset = (DimensionSetInstance) o;
        } else {
            throw new IllegalStateException("Loaded an asset that is not a DimensionSetInstance.");
        }
        return dimset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#getDimensionSet(java
     * .lang.String)
     */

    public DimensionSetInstance getDimensionSet(final String name) {
        final String DIMSET_OBJ_NAME = "LocaleUtils:findTranslation:theDimensionSet:DimensionSet";
        ics.SetObj(DIMSET_OBJ_NAME, null);
        final AssetLoadByName a = new AssetLoadByName();
        a.setAssetType("DimensionSet");
        a.setAssetName(name);
        a.setEditable(false);
        a.setName(DIMSET_OBJ_NAME);
        a.execute(ics);

        if (ics.GetErrno() < 0) {
            throw new IllegalStateException("Could not load dimension set.  Errno: " + ics.GetErrno());
        }

        final Object o = ics.GetObj(DIMSET_OBJ_NAME);
        ics.SetObj(DIMSET_OBJ_NAME, null);
        if (o == null) {
            throw new IllegalStateException("Could not load dimension set but we got no errno... unexpected...");
        }

        DimensionSetInstance dimset;
        if (o instanceof DimensionSetInstance) {
            dimset = (DimensionSetInstance) o;
        } else {
            throw new IllegalStateException("Loaded an asset that is not a DimensionSetInstance");
        }
        return dimset;
    }

    DimensionableAssetManager dam;

    /**
     * Shorthand function for returning the DimensionableAssetManager.
     * 
     * @return DimensionableAssetManager
     */
    public DimensionableAssetManager getDAM() {
        if (dam == null) {
            dam = getManager(DimensionableAssetManager.class);
        }
        return dam;
    }

    private DimensionManager dm;

    /**
     * Shorthand function for returning the DimensionManager.
     * 
     * @return DimensionManager
     */
    protected DimensionManager getDM() {
        if (dm == null) {
            dm = getManager(DimensionManager.class);
        }
        return dm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#getLocaleForAsset
     * (com.fatwire.assetapi.data.AssetId)
     */

    public Dimension getLocaleForAsset(final AssetId id) {
        final Collection<Dimension> dims = getDAM().getDimensionsForAsset(id);
        for (final Dimension dim : dims) {
            if ("locale".equalsIgnoreCase(dim.getGroup())) {
                return dim;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#getDimensionIdForName
     * (java.lang.String)
     */

    public long getDimensionIdForName(final String name) {
        final AssetId id = getDimensionAssetIdForName(name);
        return id == null ? -1 : id.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.facade.mda.LocaleService#
     * getDimensionAssetIdForName(java.lang.String)
     */

    public AssetId getDimensionAssetIdForName(final String name) {
        final Dimension dim = getDimensionForName(name);
        return dim == null ? null : dim.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#getDimensionForName
     * (java.lang.String)
     */

    public Dimension getDimensionForName(final String name) {
        return getDM().loadDimension(name);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#getNameForDimensionId
     * (long)
     */

    public String getNameForDimensionId(final long dimensionid) {
        final Dimension dim = getDM().loadDimension(dimensionid);
        return dim == null ? null : dim.getName();
    }

    protected Session getSession() {
        if (session == null) {
            session = SessionFactory.getSession(ics);

        }
        return session;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getManager(final Class<T> c) {
        return (T) getSession().getManager(c.getName());
    }

}
