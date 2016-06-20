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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.mda.DefaultLocaleService;
import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.facade.search.SimpleSearchEngine;
import com.fatwire.gst.foundation.mapping.IcsMappingService;
import com.fatwire.gst.foundation.mapping.MappingService;
import com.fatwire.gst.foundation.properties.AssetApiPropertyDao;
import com.fatwire.gst.foundation.properties.PropertyDao;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

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
 * <p>
 * This class was created as a subset of the methods of the legacy class IcsBackedObjectFactory.
 * More especifically, it was removed the createWraPathTranslationService method, the
 * createIncludeService method and the createNavigationService method, therefore breaking the
 * dependency between this class, the vanity url-related classes, the navigation service-related
 * classes and the include service-related classes.
 * </p>
 * 
 * @author Dolf.Dijkstra
 * @since Apr 20, 2011
 * 
 */
public class SimpleIcsBackedObjectFactoryTemplate extends BaseFactory {
    /**
     * Constructor.
     * 
     * @param ics the Content Server context
     */
    public SimpleIcsBackedObjectFactoryTemplate(final ICS ics) {
        super(ics);
    }

    /**
     * @param ics Content Server context object
     * @param roots Factory object
     */
    public SimpleIcsBackedObjectFactoryTemplate(ICS ics, Factory... roots) {
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
        return AssetApiPropertyDao.newInstance(ics);
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

}
