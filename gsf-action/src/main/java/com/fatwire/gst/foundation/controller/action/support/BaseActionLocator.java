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

import java.lang.reflect.Constructor;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.RenderPage;
import com.fatwire.gst.foundation.mapping.MappingInjector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ActionLocator with support for Factory and a fallback ActionLocator.
 * <p/>
 * Objects are created via a {@link Factory}, that can be configured via the
 * <tt>factoryClassname</tt>. That class needs to have a constructor accepting
 * ICS.
 * 
 * @author Dolf Dijkstra
 * @since Apr 27, 2011
 */
public abstract class BaseActionLocator implements ActionLocator {

    protected static final Log LOG = LogFactory.getLog(BaseActionLocator.class.getPackage().getName());
    /**
     * The default fallbackActionLocator in case no action is found.
     */
    private ActionLocator fallbackActionLocator = new ActionLocator() {

        public Action getAction(final ICS ics, final String name) {
            Action action = new RenderPage();
            injectDependencies(ics, action);
            return action;
        }

    };
    private Constructor<Factory> constructor;

    public BaseActionLocator() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(
     * COM.FutureTense.Interfaces.ICS, java.lang.String)
     */
    public final Action getAction(final ICS ics, final String name) {

        Action action = null;
        action = doFindAction(ics, name);
        if (action == null) {
            action = getFallbackActionLocator().getAction(ics, name);
            LOG.trace("No command specified. Returning fallback action: " + action.getClass().getName());
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Command '" + name + "' maps to action " + action.getClass().getName());
            }
        }
        if (StringUtils.isNotBlank(name)) {
            CacheManager.RecordItem(ics, "gsf-action:" + name);
        }
        // inject the required data into the action
        // TODO: major, should be inject if the Action is retrieved from the
        // fallback?
        //injectDependencies(ics, action);

        return action;

    
    }

    /**
     * Template Method for finding the Action for the custom ActionLocator.
     * In case the Action is created throught this method, it is expected to be fully injected and ready to use.
     * 
     * @param ics the Content Server context
     * @param name the name of the action
     * @return the Action if found, null is valid.
     */
    protected abstract Action doFindAction(final ICS ics, final String name);

    /**
     * @param ics
     * @param action
     */
    protected final void injectDependencies(final ICS ics, final Action action) {
        final Factory factory = getFactory(ics);
        AnnotationInjector.inject(action, factory);
        final AssetIdWithSite id = figureOutTemplateOrCSElementId(ics);
        if (id != null) {
            MappingInjector.inject(action, factory, id);
        }

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
            factory = new IcsBackedObjectFactoryTemplate(ics);
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
