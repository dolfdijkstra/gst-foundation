/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.controller.action.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.PreviewContext;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.mda.DefaultLocaleService;
import com.fatwire.gst.foundation.facade.mda.DimensionUtils;
import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.facade.mda.LocaleUtils;
import com.fatwire.gst.foundation.facade.search.SimpleSearchEngine;
import com.fatwire.gst.foundation.include.DefaultIncludeService;
import com.fatwire.gst.foundation.include.IncludeService;
import com.fatwire.gst.foundation.mapping.IcsMappingService;
import com.fatwire.gst.foundation.mapping.MappingService;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.navigation.support.SimpleNavigationHelper;
import com.fatwire.gst.foundation.properties.AssetApiPropertyDao;
import com.fatwire.gst.foundation.properties.PropertyDao;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.wra.navigation.WraNavigationService;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionException;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionSetInstance;

/**
 * Factory implementation that works with a method naming convention to create
 * objects. Objects are created in a delegated factory method. The delegated
 * method is found by looking for a method that is prefixed by 'create' and then
 * the the simple name of the class (classname without package prefix).
 * <p>
 * For instance to create a object of class 'com.bar.Foo' if will look for a
 * method <tt>public Foo createFoo(ICS ics);</tt>. The method has to be public
 * and has to accept one argument of type ICS.
 * </p>
 * 
 * 
 * @author Dolf.Dijkstra
 * @since Apr 20, 2011
 * 
 */
public class IcsBackedObjectFactoryTemplate extends BaseFactory {
    /**
     * Constructor.
     * 
     * @param ics the Content Server context
     */
    public IcsBackedObjectFactoryTemplate(final ICS ics) {
        super(ics);
    }

    /**
     * @param ics
     * @param roots
     */
    public IcsBackedObjectFactoryTemplate(ICS ics, Factory[] roots) {
        super(ics,roots);
    }

    /**
     * Should the created object be cached on the ICS scope.
     * 
     * @param c
     * @return true is object should be cached locally
     */
    @Override
    public boolean shouldCache(final Class<?> c) {
        // don't cache the model as this is bound to the jsp page context and
        // not
        // to ICS. It would leak variables into other elements if we allowed it
        // to cache.
        // TODO:medium, figure out if this should be done more elegantly. It
        // seems that scoping logic is
        // brought into the factory, that might be a bad thing.
        if (Model.class.isAssignableFrom(c)) {
            return false;
        }

        return true;
    }

    public ICS createICS(final ICS ics) {
        return ics;
    }

    public WraCoreFieldDao createWraCoreFieldDao(final ICS ics) {
        return AssetApiWraCoreFieldDao.getInstance(ics);
    }

    public AliasCoreFieldDao createAliasCoreFieldDao(final ICS ics) {
        final WraCoreFieldDao wraCoreFieldDao = getObject("wraCoreFieldDao", WraCoreFieldDao.class);
        return new AssetApiAliasCoreFieldDao(ics, wraCoreFieldDao);
    }

    public PropertyDao createPropertyDao(final ICS ics) {
        return AssetApiPropertyDao.newInstance(ics);
    }

    public WraPathTranslationService createWraPathTranslationService(final ICS ics) {
        return WraPathTranslationServiceFactory.getService(ics);
    }

    public IncludeService createIncludeService(final ICS ics) {
        return new DefaultIncludeService(ics);
    }

    public ScatteredAssetAccessTemplate createScatteredAssetAccessTemplate(final ICS ics) {
        return new ScatteredAssetAccessTemplate(ics);
    }

    public AssetAccessTemplate createAssetAccessTemplate(final ICS ics) {
        return new AssetAccessTemplate(ics);
    }

    public MappingService createMappingService(final ICS ics) {
        return new IcsMappingService(ics);
    }

    public LocaleService createLocaleService(final ICS ics) {
        return new DefaultLocaleService(ics);
    }

    public TemplateAssetAccess createTemplateAssetAccess(final ICS ics) {
        return new TemplateAssetAccess(ics);
    }

    public Model createModel(final ICS ics) {
        return new Model();
    }

    public SimpleSearchEngine createSimpleSearchEngine(final ICS ics) {
        return new SimpleSearchEngine("lucene");
    }

    public NavigationService createNavigationService(final ICS ics) {
        // TODO: check if unnamed association is a valid association for a Page
        // assettype and create NavigationService based on that.

        boolean wraNavigationSupport = true;
        // TODO come up with a generalized Strategy for per-site dispatching
        if ("avisports".equalsIgnoreCase(ics.GetVar("site"))) {
            return new SimpleNavigationHelper(ics, getObject("templateAssetAccess", TemplateAssetAccess.class),
                    "title", "path");
        } else if (wraNavigationSupport) {
            // BE AWARE that the NavigationService is cached per request and
            // that the DimensionFilter is also reused per all the
            // NavigationService calls per request.
            // Depending on the outcome of the getDimensionFilter this may or
            // maynot what you want.
            DimensionFilterInstance filter = getDimensionFilter(ics);
            if (filter == null && LOG.isTraceEnabled()) {
                LOG.trace("No DimensionFilterInstance returned from getDimensionFilter(). Disabling Locale support for NavigationService.");
            }

            AliasCoreFieldDao aliasDao = getObject("aliasCoreFieldDao", AliasCoreFieldDao.class);
            Date date = PreviewContext.getPreviewDateFromCSVar(ics, "previewDate");
            return new WraNavigationService(ics, getObject("templateAssetAccess", TemplateAssetAccess.class), aliasDao,
                    filter, date);
        } else {
            return new SimpleNavigationHelper(ics, getObject("templateAssetAccess", TemplateAssetAccess.class),
                    "linktext", "path");

        }
    }

    /**
     * Return a dimension filter instance corresponding to the dimension set
     * specified by the user (or discovered by the tag). The dimension filter is
     * configured with the preferred dimensions of the user (also configured).
     * <p/>
     * The preferred locales are identified by checking the following locations,
     * in the order specified:
     * <ol>
     * <li>set by the locale attribute by id of locale
     * <li>set by locale attribute by name of locale
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
    protected final DimensionFilterInstance getDimensionFilter(ICS ics) {

        DimensionFilterInstance filter;
        try {
            DimensionSetInstance dimSet = getDimensionSet(ics);
            if (dimSet == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("no DimensionSet returned from getDimensionSet().");
                }
                return null;
            }
            Collection<AssetId> preferredLocales = getPreferredLocales(ics);

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
    protected final AssetId getExplicitlySpecifiedLocale(ICS ics) {

        String localeName = null;
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
    protected final Collection<AssetId> getPreferredLocales(ICS ics) {
        AssetId result = getExplicitlySpecifiedLocale(ics);
        if (result != null)
            return Collections.singleton(result);

        // next, check for implicitly specified by ID using locale variable
        String localeVar = ics.GetVar("locale");
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

        // next, check for implicitly specified by ID using locale session
        // variable
        String localeSSVar = ics.GetSSVar("locale");
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
                        preferredLocales.add(dimension.getId());
                        LOG.trace("Found registered locale in user's Accept-Language header (or default): "
                                + localeName);
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

    protected final DimensionSetInstance getDimensionSet(ICS ics) {

        try {
            String site = ics.GetVar("site");
            if (StringUtils.isNotBlank(site)) {
                long discoveredId = LocaleUtils.locateDimensionSetForSite(ics, site);
                LOG.trace("Auto-discovered dimension set because there is only one in site " + site + ": DimensionSet:"
                        + discoveredId);
                return LocaleUtils.getDimensionSet(ics, discoveredId);
            }
        } catch (RuntimeException e) {
            LOG.trace("Could not auto-discover dimensionset: " + e);
        }
        return null;
        // throw new IllegalArgumentException("DimensionSet not found");
    }

}
