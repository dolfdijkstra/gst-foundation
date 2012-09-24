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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

/**
 * @author Dolf Dijkstra
 * 
 */
public abstract class BaseFactory implements Factory {

    protected static final Log LOG = LogUtil.getLog(IcsBackedObjectFactoryTemplate.class);

    public abstract boolean shouldCache(final Class<?> c);

    protected final ICS ics;
    private final Map<String, Object> objectCache = new HashMap<String, Object>();
    private Factory[] roots = new Factory[0];;

    public BaseFactory(ICS ics) {
        super();
        this.ics = ics;

    }

    public BaseFactory(ICS ics, Factory[] roots) {
        super();
        this.ics = ics;
        if (roots != null)
            this.roots = roots;
    }

    public final <T> T getObject(final String name, final Class<T> fieldType) {

        T o = locate(fieldType, ics);
        if (o == null) {
            for (Factory root : roots) {
                o = root.getObject(name, fieldType);
                if (o != null)
                    return o;

            }
        }
        return o;
    }

    /**
     * Internal method to check for Services or create Services.
     * 
     * @param c
     * @param ics
     * @return the found service, null if no T can be created.
     */
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

            try {
                // TODO: medium: check for other method signatures
                Method m;
                m = getClass().getMethod("create" + name, ICS.class);
                if (m != null) {
                    if (LOG.isTraceEnabled())
                        LOG.trace("creating " + name + " from " + m.getName());
                    o = m.invoke(this, ics);
                }
            } catch (final NoSuchMethodException e) {
                try {
                    LOG.debug("Could not create  a " + c.getName() + " via a Template method, trying via constructor.");
                    final Constructor<T> constr = c.getConstructor(ICS.class);
                    o = constr.newInstance(ics);
                } catch (final RuntimeException e1) {
                    throw e1;
                } catch (final Exception e1) {
                    throw new RuntimeException(e1);
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

}
