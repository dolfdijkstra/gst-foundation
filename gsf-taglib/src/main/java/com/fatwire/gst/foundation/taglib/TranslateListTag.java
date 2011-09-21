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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.mda.DimensionUtils;
import com.fatwire.gst.foundation.facade.mda.LocaleUtils;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * simple tag for translating an asset that's in the form of an ASSETID,ASSETTYPE IList.
 *
 * @author Tony Field
 * @since 11-09-20
 */
public final class TranslateListTag extends GsfSimpleTag {
    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.taglib");

    private String inlist = null;
    private String dimensionSetName = null;
    private long dimensionSetId = -1L;
    private String localeName = null;
    private long localeId = -1L;
    private String outlist = null;

    public void setInlist(String s) {
        this.inlist = s;
    }

    public void setDimset(String s) {
        try {
            dimensionSetId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            dimensionSetName = s;
            dimensionSetId = -1L;
        }
    }

    public void setLocale(String s) {
        try {
            localeId = Long.parseLong(s);
        } catch (NumberFormatException e) {
            localeName = s;
            localeId = -1L;
        }
    }

    public void setOutlist(String s) {
        outlist = s;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        final ICS ics = getICS();


        DimensionManager dm = DimensionUtils.getDM(ics);

        List<AssetId> toFilterList = _getInputList();
        Collection<AssetId> preferredLocales = _getPreferredLocales();
        DimensionSetInstance dimSet = _getDimensionSet();
        String outputListName = _getOutputListName();

        Collection<AssetId> result;
        try {
            if (preferredLocales == null || preferredLocales.size() == 0) {
                result = toFilterList;
                LOG.debug("Preferred locale net detected.  Not translating input assets.");
            } else if (dimSet == null) {
                result = toFilterList;
                LOG.debug("DimensionSet not specified.  Not translating input assets.");
            } else {
                // finally! an actual translation!
                result = DimensionUtils.filterAssets(dm, toFilterList, preferredLocales, dimSet);
                if (LOG.isDebugEnabled())
                    LOG.debug("Successfully filtered assets.  Input: " + toFilterList + ", preferredLocales:" + preferredLocales + ", dimset:" + dimSet.getId() + ", result:" + result);
            }
        } catch (DimensionException e) {
            LOG.error("Unexpected failure translating assets. Returning input assets.", e);
            result = toFilterList;
        }


        // register the IList in ICS
        IList resultIList = new AssetIdIList(outputListName, result);
        ics.RegisterList(outputListName, resultIList);
        // bonus!
        getJspContext().setAttribute(outputListName, result);

        super.doTag();
    }

    private List<AssetId> _getInputList() throws JspException {
        if (inlist == null || inlist.length() == 0)
            throw new JspException("No inlist specified in gsf:translate-list tag");
        IList in = getICS().GetList(inlist);
        List<AssetId> result = new ArrayList<AssetId>();
        for (Row row : new IListIterable(in)) {
            AssetId id = AssetIdUtils.createAssetId(row.getString("assettype"), row.getString("assetid"));
            result.add(id);
        }
        if (result.size() == 0) {
            LOG.debug("Input list does not contain any items in gsf:translate-list tag");
        }
        return result;
    }

    private Collection<AssetId> _getPreferredLocales() {
        ICS ics = getICS();
        // first, check for explicitly specified by ID
        if (localeId != -1L) {
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeId);
            LOG.trace("Preferred locale explicitly set to " + localeId);
            return Collections.singletonList(d.getId());
        }

        // next, check for explicitly specified by name
        if (localeName != null) {
            Dimension d = DimensionUtils.getDimensionForName(ics, localeName);
            LOG.trace("Preferred locale explicitly set to " + localeName);
            return Collections.singletonList(d.getId());
        }

        // next, check for implicitly specified by ID using locale variable
        String localeVar = getICS().GetVar("locale");
        try {
            long localeIdFromVar = Long.parseLong(localeVar);
            DimensionManager dm = DimensionUtils.getDM(ics);
            Dimension d = dm.loadDimension(localeIdFromVar);
            LOG.trace("Preferred locale detected in ICS context using 'locale' variable: " + localeIdFromVar);
            return Collections.singletonList(d.getId());
        } catch (NumberFormatException e) {
            // maybe it's a locale name...
            try {
                Dimension d = DimensionUtils.getDimensionForName(ics, localeVar);
                LOG.trace("Preferred locale detected in ICS context using 'locale' variable: " + localeVar);
                return Collections.singletonList(d.getId());
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
            LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: " + localeIdFromSSVar);
            return Collections.singletonList(d.getId());
        } catch (NumberFormatException e) {
            // maybe it's a locale name...
            try {
                Dimension d = DimensionUtils.getDimensionForName(ics, localeSSVar);
                LOG.trace("Preferred locale detected in ICS context using 'locale' session variable: " + localeSSVar);
                return Collections.singletonList(d.getId());
            } catch (Exception ex) {
                // nope... don't worry, we'll find it....
            }
        }

        // finally, get the locale from the servlet request's Accept-Language header..
        List<AssetId> preferredLocales = new ArrayList<AssetId>();
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

    private DimensionSetInstance _getDimensionSet() {
        if (dimensionSetName != null) {
            return LocaleUtils.getDimensionSet(getICS(), dimensionSetName);
        }
        if (dimensionSetId != -1L) {
            return LocaleUtils.getDimensionSet(getICS(), dimensionSetId);
        }
        return null;
    }

    private String _getOutputListName() {
        if (outlist == null) return inlist;
        return outlist;
    }
}
