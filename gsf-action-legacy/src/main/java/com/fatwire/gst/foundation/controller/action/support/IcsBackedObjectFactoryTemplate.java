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

import java.util.Date;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.site.SiteManager;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.PreviewContext;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.mda.DefaultLocaleService;
import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.facade.search.SimpleSearchEngine;
import com.fatwire.gst.foundation.include.DefaultIncludeService;
import com.fatwire.gst.foundation.include.IncludeService;
import com.fatwire.gst.foundation.mapping.IcsMappingService;
import com.fatwire.gst.foundation.mapping.MappingService;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.properties.AssetApiPropertyDao;
import com.fatwire.gst.foundation.properties.PropertyDao;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.db.DbSimpleWRADao;
import com.fatwire.gst.foundation.url.db.UrlRegistry2;
import com.fatwire.gst.foundation.url.db.UrlRegistryDao;
import com.fatwire.gst.foundation.url.db.UrlRegistryDaoImpl;
import com.fatwire.gst.foundation.vwebroot.VirtualWebrootApiBypassDao;
import com.fatwire.gst.foundation.vwebroot.VirtualWebrootDao;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.SimpleWRADao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.wra.navigation.WraNavigationService;
import com.fatwire.mda.DimensionFilterInstance;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

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
     * @param ics Content Server context object
     * @param roots Factory object
     */
    public IcsBackedObjectFactoryTemplate(ICS ics, Factory... roots) {
        super(ics, roots);
    }

    @ServiceProducer(cache = true)
    public WraCoreFieldDao createWraCoreFieldDao(final ICS ics) {
        return AssetApiWraCoreFieldDao.getInstance(ics);
    }

    @ServiceProducer(cache = true)
    public AliasCoreFieldDao createAliasCoreFieldDao(final ICS ics) {
        final WraCoreFieldDao wraCoreFieldDao = getObject("wraCoreFieldDao", WraCoreFieldDao.class);
        return new AssetApiAliasCoreFieldDao(ics, wraCoreFieldDao);
    }

    @ServiceProducer(cache = true)
    public PropertyDao createPropertyDao(final ICS ics) {
    	Session session = SessionFactory.getSession(ics);
    	AssetDataManager adm = (AssetDataManager) session.getManager(AssetDataManager.class.getName());
    	SiteManager sm = (SiteManager) session.getManager(SiteManager.class.getName());
    	String type = "GSTProperty";
    	String flexDefName = "GSTProperty";
    	String propNameAttr = "name";
    	String propDescAttr = "description";
    	String propValueAttr = "value";
    	return new AssetApiPropertyDao(adm, sm, type, flexDefName, propNameAttr, propDescAttr, propValueAttr, ics);
    }

    @ServiceProducer(cache = true)
    public WraPathTranslationService createWraPathTranslationService(final ICS ics) {
        final SimpleWRADao wraDao = new DbSimpleWRADao(ics);
        final VirtualWebrootDao vwDao = new VirtualWebrootApiBypassDao(ics);
        final UrlRegistryDao regDao = new UrlRegistryDaoImpl(ics);
        final UrlRegistry2 x = new UrlRegistry2(ics, wraDao, vwDao, regDao);
        return x;
    }

    @ServiceProducer(cache = true)
    public IncludeService createIncludeService(final ICS ics) {
        return new DefaultIncludeService(ics);
    }

    @ServiceProducer(cache = true)
    public ScatteredAssetAccessTemplate createScatteredAssetAccessTemplate(final ICS ics) {
        return new ScatteredAssetAccessTemplate(ics);
    }

    @ServiceProducer(cache = true)
    public AssetAccessTemplate createAssetAccessTemplate(final ICS ics) {
        return new AssetAccessTemplate(ics);
    }

    @ServiceProducer(cache = true)
    public MappingService createMappingService(final ICS ics) {
        return new IcsMappingService(ics);
    }

    @ServiceProducer(cache = true)
    public LocaleService createLocaleService(final ICS ics) {
        return new DefaultLocaleService(ics);
    }

    @ServiceProducer(cache = true)
    public TemplateAssetAccess createTemplateAssetAccess(final ICS ics) {
        return new TemplateAssetAccess(ics);
    }

    @ServiceProducer(cache = false)
    public Model createModel(final ICS ics) {
        return new Model();
    }

    @ServiceProducer(cache = true)
    public SimpleSearchEngine createSimpleSearchEngine(final ICS ics) {
        return new SimpleSearchEngine("lucene");
    }

    @ServiceProducer(cache = false)
    public NavigationService createNavigationService(final ICS ics) {
        // TODO: check if unnamed association is a valid association for a Page
        // assettype and create NavigationService based on that.

        //boolean wraNavigationSupport = true;
        TemplateAssetAccess taa = getObject("templateAssetAccess", TemplateAssetAccess.class);
        // TODO come up with a generalized Strategy for per-site dispatching
        //if (wraNavigationSupport) {
            // BE AWARE that the NavigationService is cached per request and
            // that the DimensionFilter is also reused per all the
            // NavigationService calls per request.
            // Depending on the outcome of the getDimensionFilter this may or
            // may not what you want.
            LocaleService ls = this.getObject("localeService", LocaleService.class);
            String site = ics.GetVar("site");
            DimensionFilterInstance filter = ls.getDimensionFilter(site);
            if (filter == null && LOG.isTraceEnabled()) {
                LOG.trace("No DimensionFilterInstance returned from getDimensionFilter(). Disabling Locale support for NavigationService.");
            }

            AliasCoreFieldDao aliasDao = getObject("aliasCoreFieldDao", AliasCoreFieldDao.class);
            Date date = PreviewContext.getPreviewDateFromCSVar(ics, "previewDate");
            return new WraNavigationService(ics, taa, aliasDao, filter, date);
        //} else {
            //return new SimpleNavigationHelper(ics, taa, "linktext", "path");
        //}
    }

}
