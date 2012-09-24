/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

/**
 * @author Dolf Dijkstra
 * 
 */
public abstract class BaseFactory implements Factory {

    protected static final Log LOG = LogUtil.getLog(IcsBackedObjectFactoryTemplate.class);

    protected final ICS ics;
    private final Map<String, Object> objectCache = new HashMap<String, Object>();
    private Factory[] roots = new Factory[0];;

    public BaseFactory(ICS ics) {
        super();
        this.ics = ics;

    }

    public BaseFactory(ICS ics, Factory... roots) {
        super();
        this.ics = ics;
        if (roots != null)
            this.roots = roots;
    }

    public final <T> T getObject(final String name, final Class<T> fieldType) {

        T o;
        try {
            o = locate(fieldType);
            if (o == null) {
                for (Factory root : roots) {
                    o = root.getObject(name, fieldType);
                    if (o != null)
                        return o;

                }
            }
            if (roots.length == 0) // only try ctor at the root level, otherwise
                                   // it will be invoked on each BaseFactory
                o = ctorStrategy(name, fieldType);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
        return o;
    }

    /**
     * Internal method to check for Services or create Services.
     * 
     * @param c
     * @param ics
     * @return the found service, null if no T can be created.
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    protected <T> T locate(final Class<T> c) throws InvocationTargetException {
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

            o = reflectionStrategy(name, c);
            if (shouldCache(c)) {
                objectCache.put(c.getName(), o);
            }
        }
        return (T) o;
    }

    /**
     * @param name
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> T reflectionStrategy(final String name, final Class<T> c) throws InvocationTargetException {
        T o = null;
        try {
            // TODO: medium: check for other method signatures
            Method m;
            m = getClass().getMethod("create" + name, ICS.class);
            if (m != null) {
                if (LOG.isTraceEnabled())
                    LOG.trace("creating " + name + " from " + m.getName());
                o = (T) m.invoke(this, ics);
            }
        } catch (IllegalArgumentException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a reflection method: " + e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a reflection method: " + e.getMessage());
        } catch (SecurityException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a reflection method: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a reflection method: " + e.getMessage());
        }
        return o;
    }

    /**
     * @param name
     * @param c
     * @return
     * @throws InvocationTargetException
     */
    protected <T> T ctorStrategy(final String name, final Class<T> c) throws InvocationTargetException {
        T o = null;
        try {
            // System.out.println("factory: " + getClass().getName());
            // System.out.println("class: " + c.getName());
            // System.out.println("class: " + c.getSimpleName());
            // System.out.println(" interface: " + c.isInterface());
            // System.out.println(" abstract: " +
            // Modifier.isAbstract(c.getModifiers()));
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
                LOG.debug("Could not create  a " + c.getName() + " via a Template method. The class '" + c.getName()
                        + "' is an interface or abstract class, giving up as a class cannot be constructed.");
                return null;
            }

            LOG.debug("Could not create  a " + c.getName() + " via a Template method, trying via constructor.");
            final Constructor<T> constr = c.getConstructor(ICS.class);
            o = constr.newInstance(ics);
        } catch (final NoSuchMethodException e1) {
            LOG.debug("Could not create  a " + c.getName() + " via a constructor method.");
        } catch (IllegalArgumentException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a constructor method.");
        } catch (InstantiationException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a constructor method.");
        } catch (IllegalAccessException e) {
            LOG.debug("Could not create  a " + c.getName() + " via a constructor method.");
        }
        return o;
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
        // brought into the factory, that might be a bad thing.
        if (Model.class.isAssignableFrom(c)) {
            return false;
        }

        return true;
    }

    public ICS createICS(final ICS ics) {
        return ics;
    }

}
