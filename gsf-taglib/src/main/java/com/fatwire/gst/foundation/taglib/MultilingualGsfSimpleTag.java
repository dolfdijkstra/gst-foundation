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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.mda.DimensionUtils;
import com.fatwire.gst.foundation.facade.mda.LocaleUtils;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * simple tag for translating an asset
 *
 * @author Tony Field
 * @since 2011-11-28
 */
public abstract class MultilingualGsfSimpleTag extends GsfSimpleTag {
    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.taglib");

    private String dimensionSetName = null;
    private long dimensionSetId = -1L;
    private String localeName = null;
    private long localeId = -1L;

    public final void setDimset(String s) {
        try {
            dimensionSetId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            dimensionSetName = s;
            dimensionSetId = -1L;
        }
    }

    public final void setLocale(String s) {
        try {
            localeId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            localeName = s;
            localeId = -1L;
        }
    }

    /**
     * Return a dimension filter instance corresponding to the dimension set specified by the user (or discovered by
     * the tag).  The dimension filter is configured with the preferred dimensions of the user (also onfigured).
     *
     * The preferred locales are identified by checking the following locations, in the order specified:
     * 1) set by the locale attribute by id of locale
     * 2) set by locale attribute by name of locale
     * 3) detected by finding the locale dimension id in the ics variable "locale"
     * 4) detected by finding the locale name in the ics variable "locale"
     * 5) detected by finding the locale dimension id in the ics session variable "locale"
     * 6) detected by finding the locale name in the ics session variable "locale"
     * 7) detected by reading the Accept-Language header
     *
     * The dimension set is identified by checking in the following places, in order:
     * 1) set by the dimset attribute by name of dimension set
     * 2) set by dimset attribute by the id of the dimension set
     * 3) looked up by finding the site name in the ics variable "site" and loading the single dimension set associated with that site
     *
     * @return a dimension filter, configured with the set preferred locales, or null, if either the dimenion set or the
     * preferred dimensions could not be found (with extensive errors)
     */
    protected final DimensionFilterInstance getDimensionFilter() {
        ICS ics = getICS();
        DimensionFilterInstance filter;
        try {
            Collection<AssetId> preferredLocales = getPreferredLocales();
            DimensionSetInstance dimSet = getDimensionSet();
            filter = DimensionUtils.getDimensionFilter(DimensionUtils.getDM(ics), preferredLocales, dimSet);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Multilingual-enabled tag located dimension filter: " + filter + " in dimensionSet " + dimSet + " with preferred locales: " + preferredLocales + " ");
            }
        } catch (DimensionException e) {
            LOG.error("Multilingual-enabled tag could not locate dimension filter", e);
            filter = null;
        } catch (RuntimeException e) {
            LOG.error("Multilingual-enabled tag could not locate dimension filter", e);
            filter = null;
        }
        return filter;
    }

    /**
     * Get the locale that the user explicitly specified.  If not set, null is returned.
     * @return the id of the locale that the user explicitly set.  Handles setting by name or assetid.
     */
    protected final AssetId getExplicitlySpecifiedLocale() {
        ICS ics = getICS();
        // first, check for explicitly specified by ID
        if (localeId != -1L) {
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeId);
            if (d != null) {
                LOG.trace("Preferred locale explicitly set to " + localeId);
                return d.getId();
            }
        }

        // next, check for explicitly specified by name
        if (localeName != null) {
            Dimension d = DimensionUtils.getDimensionForName(ics, localeName);
            if (d != null) {
                LOG.trace("Preferred locale explicitly set to " + localeName);
                return d.getId();
            }
        }
        return null;
    }
    /**
     * Get the ordered list of preferred locales that the user wants.  Multiple attempts are made to figure out the right locale.
     * @return
     */
    protected final Collection<AssetId> getPreferredLocales() {
        AssetId result = getExplicitlySpecifiedLocale();
        if (result != null) return Collections.singleton(result);

        ICS ics = getICS();

        // next, check for implicitly specified by ID using locale variable
        String localeVar = getICS().GetVar("locale");
        try {
            long localeIdFromVar = Long.parseLong(localeVar);
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

        // next, check for implicitly specified by ID using locale session variable
        String localeSSVar = getICS().GetSSVar("locale");
        try {
            long localeIdFromSSVar = Long.parseLong(localeSSVar);
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeIdFromSSVar);
            if (d != null) {
                LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: " + localeIdFromSSVar);
                return Collections.singletonList(d.getId());
            }
        } catch (NumberFormatException e) {
            // maybe it's a locale name...
            try {
                Dimension d = DimensionUtils.getDimensionForName(ics, localeSSVar);
                if (d != null) {
                    LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: " + localeSSVar);
                    return Collections.singletonList(d.getId());
                }
            } catch (Exception ex) {
                // nope... don't worry, we'll find it....
            }
        }

        // finally, get the locale from the servlet request's Accept-Language header..
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
                        preferredLocales.add(dimension.getId());
                        LOG.trace("Found registered locale in user's Accept-Language header (or default): " + localeName);
                    } catch (RuntimeException e) {
                        // don't care if the dimension is not in the system - they probably won't all be there
                        // and we're guessing anyway, so it's okay.
                        LOG.trace("Found a locale in the user's Accept-Language header, but it was not registered as a dimension: " + localeName + " (this is not usually an error)", e);
                    }
                }
            }
        }
        return preferredLocales;
    }

    private final DimensionSetInstance getDimensionSet() {
        if (dimensionSetName != null) {
            return LocaleUtils.getDimensionSet(getICS(), dimensionSetName);
        }
        if (dimensionSetId != -1L) {
            return LocaleUtils.getDimensionSet(getICS(), dimensionSetId);
        }
        try {
            ICS ics = getICS();
            String site = ics.GetVar("site");
            if (site != null && site.length() > 0) {
                long discoveredId = LocaleUtils.locateDimensionSetForSite(ics, site);
                LOG.trace("Auto-discovered dimension set because there is only one in site " + site + ": DimensionSet:" + discoveredId);
                return LocaleUtils.getDimensionSet(ics, discoveredId);
            }
        } catch (RuntimeException e) {
            LOG.trace("Could not auto-discovered dimensionset: " + e);
        }
        throw new IllegalArgumentException("DimensionSet not found");
    }
}
