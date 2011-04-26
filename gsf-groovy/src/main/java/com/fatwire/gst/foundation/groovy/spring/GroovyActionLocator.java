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

package com.fatwire.gst.foundation.groovy.spring;

import java.lang.reflect.Constructor;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector.Factory;
import com.fatwire.gst.foundation.controller.action.IcsBackedObjectFactory;
import com.fatwire.gst.foundation.controller.action.RenderPage;
import com.fatwire.gst.foundation.mapping.MappingInjector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
public class GroovyActionLocator implements ActionLocator {
    protected static final Log LOG = LogFactory.getLog(GroovyActionLocator.class.getPackage().getName());

    private GroovyLoader groovyLoader;

    private ActionLocator fallbackActionLocator = new ActionLocator() {

        public Action getAction(final ICS ics) {

            return new RenderPage();
        }

        public Action getAction(final ICS ics, final String name) {
            return null;
        }

    };
    private Constructor<Factory> constructor;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(
     * COM.FutureTense.Interfaces.ICS)
     */
    public Action getAction(final ICS ics) {
        Action action = null;
        final String script = getScriptName(ics);
        action = groovyAction(script);
        if (action == null) {
            action = getFallbackActionLocator().getAction(ics);
        }
        final Factory factory = getFactory(ics);
        AnnotationInjector.inject(action, factory);

        return action;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(
     * COM.FutureTense.Interfaces.ICS, java.lang.String)
     */
    public Action getAction(final ICS ics, final String name) {
        Action action = null;
        action = groovyAction(name);
        if (action == null) {
            return null;
        }
        CacheManager.RecordItem(ics, "gsf-action:" + name);
        final Factory factory = getFactory(ics);
        AnnotationInjector.inject(action, factory);
        final AssetIdWithSite id = figureOutTemplateOrCSElementId(ics);
        if (id != null) {
            MappingInjector.inject(action, factory, id);
        }

        return action;
    }

    private AssetIdWithSite figureOutTemplateOrCSElementId(final ICS ics) {
        String eid = ics.GetVar("eid");
        if (eid != null) {
            return new AssetIdWithSite("CSElement", Long.parseLong(eid), ics.GetVar("site"));
        }
        eid = ics.GetVar("tid");
        if (eid != null) {
            return new AssetIdWithSite("Template", Long.parseLong(eid), ics.GetVar("site"));
        }
        return null;
    }

    /**
     * 
     * 
     * @param name
     * @param action
     * @return
     * @throws RuntimeException
     */
    private Action groovyAction(final String name) {
        Action action = null;
        final String script = name.endsWith(".groovy") ? name : name + ".groovy";
        try {

            if (groovyLoader.isValidScript(script)) {
                final Object o = groovyLoader.load(groovyLoader.toClassName(script));

                if (o instanceof Action) {
                    action = (Action) o;
                }
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return action;
    }

    protected String getScriptName(final ICS ics) {
        final String name = ics.ResolveVariables("CS.elementname") + ".groovy";
        return name.startsWith("/") ? name.substring(1) : name;
    }

    protected Factory getFactory(final ICS ics) {
        final Object o = ics.GetObj(Factory.class.getName());
        if (o instanceof Factory) {
            return (Factory) o;
        }
        Factory factory = null;
        try {
            factory = getInjectionFactory(ics);
        } catch (final Exception e) {
            LOG.warn(e);
        }
        if (factory == null) {
            factory = new IcsBackedObjectFactory(ics);
        }
        ics.SetObj(Factory.class.getName(), factory);
        return factory;
    }

    public final Factory getInjectionFactory(final ICS ics) throws Exception {
        Factory factory = null;
        if (constructor != null) {
            factory = constructor.newInstance(new Object[] { ics });
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private void findConstructor(final String factoryClassname) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException {
        if (factoryClassname != null) {
            final Class<Factory> c = (Class<Factory>) Class.forName(factoryClassname);
            constructor = c.getConstructor(ICS.class);

        }
    }

    /**
     * @param factoryClassname the factoryClassname to set
     */
    public void setFactoryClassname(final String factoryClassname) {
        try {
            findConstructor(factoryClassname);
        } catch (final Exception e) {
            throw new IllegalArgumentException("factoryClassname: " + factoryClassname + " is illegal. "
                    + e.getMessage(), e);
        }
    }

    /**
     * @return the groovyLoader
     */
    public GroovyLoader getGroovyLoader() {
        return groovyLoader;
    }

    /**
     * @param groovyLoader the groovyLoader to set
     */
    public void setGroovyLoader(final GroovyLoader groovyLoader) {
        this.groovyLoader = groovyLoader;
    }

    /**
     * @return the fallbackActionLocator
     */
    public ActionLocator getFallbackActionLocator() {
        return fallbackActionLocator;
    }

    /**
     * @param fallbackActionLocator the fallbackActionLocator to set
     */
    public void setFallbackActionLocator(final ActionLocator fallbackActionLocator) {
        this.fallbackActionLocator = fallbackActionLocator;
    }

}
