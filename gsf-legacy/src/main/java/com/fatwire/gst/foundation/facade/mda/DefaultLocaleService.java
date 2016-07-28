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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public class DefaultLocaleService implements LocaleService {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.mda.DefaultLocaleService");

    /** the variable name for the id of the current Dimension */

    private final String localeVar;

    /** the variable name for the current requested language */

    private final String langVar;

    private final ICS ics;

    private Session session;

    /**
     * Constructor with "lang" and "locale" as variable names for langVar and
     * localeVar.
     * 
     * @param ics Content Server context object
     */
    public DefaultLocaleService(final ICS ics) {
        this(ics, "lang", "locale");
    }

    /**
     * 
     * Constructor.
     * 
     * @param ics Content Server context object
     * @param langVar the name of the ics variable for the current 'language',
     *            as in en_US.
     * @param localeVar the name of the ics variable for the current dimension
     *            id.
     */
    public DefaultLocaleService(ICS ics, String langVar, String localeVar) {
        if (ics == null) {
            throw new IllegalArgumentException("ICS must not be null.");
        }

        this.ics = ics;
        this.langVar = langVar;
        this.localeVar = localeVar;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(java
     * .lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
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

    @Override
    public AssetId findTranslation(final AssetId id, final String site, final long preferredDimension) {

        final long dimensionSetId = locateDimensionSetForSite(site);

        return findTranslation(id, preferredDimension, dimensionSetId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.mda.LocaleService#findTranslation(com
     * .fatwire.assetapi.data.AssetId, long, long)
     */

    @Override
    public AssetId findTranslation(final AssetId id, final long preferredDimension, final long dimensionSetId) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }

        if (_isInputAssetDimensionPreferred(id, preferredDimension)) {
            LOG.debug("Input dimension is already in the preferred dimension.  Not invoking dimension set filter.  Asset: "
                    + id + ", dimension: " + preferredDimension);
            return id;
        } else {
            LOG.debug("About to look for translations.  Input asset id: " + id + ", dimension set: " + dimensionSetId
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

    @Override
    public AssetId findTranslation(final AssetId id, final long preferredDimension, final String dimensionSetName) {
        if (id == null) {
            throw new IllegalArgumentException("Required Asset ID missing");
        }
        final Dimension locale = getLocaleForAsset(id);

        if (locale == null) {
            LOG.debug("Asset is not localized.  Not invoking dimension set filter.  Asset: " + id);
            return id;
        }
        if (locale.getId().getId() == preferredDimension) {
            LOG.debug("Input dimension is already in the preferred dimension.  Not invoking dimension set filter.  Asset: "
                    + id + ", dimension: " + preferredDimension);
            return id;
        } else {
            LOG.debug("About to look for translations.  Input asset id: " + id + ", dimension set: " + dimensionSetName
                    + ", preferred dimension: " + preferredDimension);
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

    @Override
    public AssetId findTranslation(final AssetId id, final long preferredDimension, final DimensionSetInstance dimset) {

        final DimensionFilterInstance filter = _getPopulatedDimensionFilter(dimset, preferredDimension);
        AssetId translated = findTranslation(id, filter);
        if (translated == null)
            LOG.warn("No translation found for asset " + id + " in dimension set " + dimset.getId() + " for dimension "
                    + preferredDimension + ".");
        return translated;
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
    public long locateDimensionSetForSite(final String site) {
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

    @Override
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

    private DimensionableAssetManager dam;

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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public AssetId findTranslation(AssetId id, DimensionFilterInstance filter) {
        // Get the relatives using the appropriate filter
        final Collection<AssetId> relatives = getDAM().getRelatives(id, filter, "Locale");
        // *****************************************************************************

        // make the result pretty
        if (relatives == null) {
            LOG.debug("No translation found for asset " + id + ".");
            return null;
        } else {
            switch (relatives.size()) {
                case 0: {
                    LOG.debug("No translation found for " + id + ".");
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
                    LOG.trace("LocaleUtils.findTranslation: RELATIVE FOUND... " + relative.getType() + " '"
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
     * Return a dimension filter instance corresponding to the dimension set
     * specified by the user (or discovered by the tag). The dimension filter is
     * configured with the preferred dimensions of the user (also configured).
     * <p>
     * The preferred locales are identified by checking the following locations,
     * in the order specified:
     * <ol>
     * <li>set by the lang attribute by locale name
     * <li>detected by finding the locale dimension id in the ics variable
     * "locale"
     * <li>detected by finding the locale name in the ics variable "locale"
     * <li>detected by finding the locale dimension id in the ics session
     * variable "locale"
     * <li>detected by finding the locale name in the ics session variable
     * "locale"
     * <li>detected by reading the Accept-Language header
     * </ol>
     * The dimension set is identified by checking in the following places, in
     * order:
     * <ol>
     * <li>set by the dimset attribute by name of dimension set
     * <li>set by dimset attribute by the id of the dimension set
     * <li>looked up by finding the site name in the ics variable "site" and
     * loading the single dimension set associated with that site
     * </ol>
     * 
     * @return a dimension filter, configured with the set preferred locales, or
     *         null, if either the dimension set or the preferred dimensions
     *         could not be found (with extensive errors)
     */
    public DimensionFilterInstance getDimensionFilter(String site) {

        DimensionFilterInstance filter;
        try {
            DimensionSetInstance dimSet = locateDimensionSetInstanceForSite(site);
            if (dimSet == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("no DimensionSet returned from getDimensionSet().");
                }
                return null;
            }
            Collection<AssetId> preferredLocales = getPreferredLocales();

            filter = DimensionUtils.getDimensionFilter(DimensionUtils.getDM(ics), preferredLocales, dimSet);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Located dimension filter: " + filter + " in dimensionSet " + dimSet
                        + " with preferred locales: " + preferredLocales + " ");
            }
        } catch (DimensionException e) {
            LOG.error("Could not locate dimension filter", e);
            filter = null;
        } catch (RuntimeException e) {
            LOG.error("Could not locate dimension filter", e);
            filter = null;
        }
        return filter;
    }

    /**
     * Get the locale that the user explicitly specified. If not set, null is
     * returned.
     * 
     * @return the id of the locale that the user explicitly set. Handles
     *         setting by name or assetid.
     */
    protected final AssetId getExplicitlySpecifiedLocale() {

        String localeName = ics.GetVar(langVar);
        // check for explicitly specified by name
        if (StringUtils.isNotBlank(localeName)) {
            Dimension d = DimensionUtils.getDimensionForName(ics, localeName);
            if (d != null) {
                LOG.trace("Preferred locale explicitly set to " + localeName);
                return d.getId();
            }
        }
        return null;
    }

    /**
     * Get the ordered list of preferred locales that the user wants. Multiple
     * attempts are made to figure out the right locale.
     * 
     * @return collection of asset identifiers of the preferred locales
     */
    protected final Collection<AssetId> getPreferredLocales() {
        AssetId result = getExplicitlySpecifiedLocale();
        if (result != null)
            return Collections.singleton(result);

        // next, check for implicitly specified by ID using locale variable
        String l = ics.GetVar(localeVar);
        try {
            long localeIdFromVar = Long.parseLong(l);
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeIdFromVar);
            if (d != null) {
                LOG.trace("Preferred locale detected in ICS context using 'locale' variable: " + localeIdFromVar);
                return Collections.singletonList(d.getId());
            }
        } catch (NumberFormatException e) {
            // maybe it's a locale name...
            try {
                Dimension d = DimensionUtils.getDimensionForName(ics, localeVar);
                if (d != null) {
                    LOG.trace("Preferred locale detected in ICS context using 'locale' variable: " + localeVar);
                    return Collections.singletonList(d.getId());
                }
            } catch (Exception ex) {
                // nope... don't worry, we'll find it....
            }
        }

        // next, check for implicitly specified by ID using locale session
        // variable
        String localeSSVar = ics.GetSSVar(localeVar);
        try {
            long localeIdFromSSVar = Long.parseLong(localeSSVar);
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeIdFromSSVar);
            if (d != null) {
                LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: "
                        + localeIdFromSSVar);
                return Collections.singletonList(d.getId());
            }
        } catch (NumberFormatException e) {
            // maybe it's a locale name...
            try {
                Dimension d = DimensionUtils.getDimensionForName(ics, localeSSVar);
                if (d != null) {
                    LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: "
                            + localeSSVar);
                    return Collections.singletonList(d.getId());
                }
            } catch (Exception ex) {
                // nope... don't worry, we'll find it....
            }
        }

        // finally, get the locale from the servlet request's Accept-Language
        // header..
        List<AssetId> preferredLocales = new ArrayList<AssetId>();
        @SuppressWarnings({ "rawtypes", "deprecation" })
        Enumeration locales = ics.getIServlet().getServletRequest().getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = (Locale) locales.nextElement();
            if (locale != null) {
                String localeName = locale.toString();
                if (localeName != null && localeName.length() > 0) {
                    try {
                        Dimension dimension = DimensionUtils.getDimensionForName(ics, localeName);
                        if (dimension != null) {
                            preferredLocales.add(dimension.getId());
                            LOG.trace("Found registered locale in user's Accept-Language header (or default): "
                                    + localeName);
                        }
                    } catch (RuntimeException e) {
                        // don't care if the dimension is not in the system -
                        // they probably won't all be there
                        // and we're guessing anyway, so it's okay.
                        LOG.trace(
                                "Found a locale in the user's Accept-Language header, but it was not registered as a dimension: "
                                        + localeName + " (this is not usually an error)", e);
                    }
                }
            }
        }
        return preferredLocales;
    }

    /**
     * Returns the DimensionSetInstance. This implementation expects one
     * DimensionSet enabled for the current site.
     * 
     * @param site current site
     * @return dimension set instance
     */
    public final DimensionSetInstance locateDimensionSetInstanceForSite(String site) {
        try {
            if (StringUtils.isNotBlank(site)) {
                long discoveredId = locateDimensionSetForSite(site);
                LOG.trace("Auto-discovered dimension set because there is only one in site " + site + ": DimensionSet:"
                        + discoveredId);
                return _getDimensionSet(discoveredId);
            }
        } catch (RuntimeException e) {
            if (LOG.isTraceEnabled())
                LOG.trace("Could not auto-discover dimensionset: " + e);
        }
        return null;
    }
}
