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

package com.fatwire.gst.foundation.controller.action;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.AnnotationInjector.Factory;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.mda.DefaultLocaleService;
import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.include.IncludeService;
import com.fatwire.gst.foundation.include.JspIncludeService;
import com.fatwire.gst.foundation.mapping.IcsMappingService;
import com.fatwire.gst.foundation.mapping.MappingService;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.lang.StringUtils;

/**
 * Factory implementation that works with a method naming convention to create
 * objects. Objects are created in a delegated factory method. The delegated
 * method is found by looking for a method that is prefixed by 'create' and then
 * the the simple name of the class (classname without package prefix). </p> For
 * instance to create a object of class 'com.bar.Foo' if will look for a method
 * <tt>public Foo createFoo(ICS ics);</tt>. The method has to be public and has
 * to accept one argument of type ICS. </p>
 * 
 * 
 * @author Dolf.Dijkstra
 * @since Apr 20, 2011
 */
public class IcsBackedObjectFactoryTemplate implements Factory {
    private final ICS ics;
    private final Map<String, Object> objectCache = new HashMap<String, Object>();

    /**
     * @param ics
     */
    public IcsBackedObjectFactoryTemplate(final ICS ics) {
        super();
        this.ics = ics;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.AnnotationInjector.Factory
     * #getObject(java.lang.String, java.lang.Class)
     */
    public final <T> T getObject(final String name, final Class<T> fieldType) {

        return locate(fieldType, ics);
    }

    /**
     * Should the created object be cached on the ICS scope.
     * 
     * @param c
     * @return true is object should be cached locally
     */
    public boolean shouldCache(final Class<?> c) {
        // don't cache the model as this is bound to the jsp page context and
        // not
        // to ICS. It would leak variables into other elements if we allowed it
        // to cache.
        // TODO:medium, figure out if this should be done more elegantly. It
        // seems that scoping logic is
        // brough into the factory, that might be a bad thing.
        if (Model.class.isAssignableFrom(c)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    protected <T> T locate(final Class<T> c, final ICS ics) {
        if (ICS.class.isAssignableFrom(c)) {
            return (T) ics;
        }
        if (c.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }
        final String name = c.getSimpleName();

        if (StringUtils.isBlank(name)) {
            return null;
        }
        Object o = objectCache.get(name);
        if (o == null) {
            Method m;
            try {
                // TODO medium: check for other method signatures
                m = getClass().getMethod("create" + name, ICS.class);
                if (m != null) {
                    o = m.invoke(this, ics);
                }
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
            if (shouldCache(c)) {
                objectCache.put(c.getName(), o);
            }
        }
        return (T) o;
    }

    public ICS createICS(final ICS ics) {
        return ics;
    }

    // @Factory
    public WraCoreFieldDao createWraCoreFieldDao(final ICS ics) {
        return WraCoreFieldDao.getInstance(ics);
    }

    public AliasCoreFieldDao createAliasCoreFieldDao(final ICS ics) {
        final WraCoreFieldDao wraCoreFieldDao = locate(WraCoreFieldDao.class, ics);
        return new AliasCoreFieldDao(ics, wraCoreFieldDao);
    }

    public WraPathTranslationService createWraPathTranslationService(final ICS ics) {
        return WraPathTranslationServiceFactory.getService(ics);
    }

    public IncludeService createIncludeService(final ICS ics) {
        return new JspIncludeService(ics);
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

    public Model createModel(final ICS ics) {
        return new Model();
    }

}
