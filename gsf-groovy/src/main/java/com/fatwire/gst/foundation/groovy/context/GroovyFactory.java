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

package com.fatwire.gst.foundation.groovy.context;

import groovy.lang.GroovyClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.support.BaseFactory;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

public class GroovyFactory extends BaseFactory {
    private static final Log log = LogUtil.getLog(GroovyFactory.class);

    private GroovyClassLoader gcl;

    public GroovyFactory(ICS ics, GroovyClassLoader gcl, Factory... roots) {
        super(ics, roots);
        this.gcl = gcl;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T reflectionStrategy(String name, Class<T> c) throws InvocationTargetException {

        T o = null;
        for (Class<?> reflectionClass : findClasses(ics)) {

            for (Method m : reflectionClass.getMethods()) {
                if (m.getName().equals("create" + name)) {
                    if (m.getReturnType().isAssignableFrom(c)) {
                        if (m.getParameterTypes().length == 2 && Modifier.isStatic(m.getModifiers())
                                && m.getParameterTypes()[0].isAssignableFrom(ICS.class)
                                && m.getParameterTypes()[1].isAssignableFrom(Factory.class)) {
                            try {
                                o = (T) m.invoke(null, ics, this);
                            } catch (IllegalArgumentException e) {
                                log.error("Huh, Can't happen, the arguments are checked: " + m.toString() + ", "
                                        + e.getMessage());
                            } catch (IllegalAccessException e) {
                                log.error("Huh, Can't happen, the modifier is checked for public: " + m.toString()
                                        + ", " + e.getMessage());
                            }
                            return o;
                        } else if (m.getParameterTypes().length == 1
                                && m.getParameterTypes()[0].isAssignableFrom(ICS.class)) {
                            Constructor<?> ctor;
                            try {
                                ctor = reflectionClass.getConstructor(ICS.class, Factory.class);
                                if (Modifier.isPublic(ctor.getModifiers())) {
                                    Object factory = ctor.newInstance(ics, this);

                                    if (LOG.isTraceEnabled())
                                        LOG.trace("creating " + name + " from " + m.getName());
                                    o = (T) m.invoke(factory, ics, this);
                                    return o;
                                } else {
                                    log.warn(reflectionClass.getName()
                                            + " does not have a public (ICS,Factory) constructor.");
                                }

                            } catch (SecurityException e) {
                                log.debug("Huh, : " + m.toString());

                            } catch (NoSuchMethodException e) {
                                throw new NoSuchMethodExceptionRuntimeException(reflectionClass.getName()
                                        + " should have a public constructor accepting a ICS and Factory.");
                            } catch (IllegalArgumentException e) {
                                log.error("Huh, Can't happen, the arguments are checked: " + m.toString() + ", "
                                        + e.getMessage());
                            } catch (InstantiationException e) {
                                log.error(e.getMessage());
                            } catch (IllegalAccessException e) {
                                log.error("Huh, Can't happen, the modifier is checked for public: " + m.toString()
                                        + ", " + e.getMessage());
                            }
                        }
                    }

                }
            }
        }
        return o;
    }

    /**
     * @param e
     */
    protected void toRuntimeException(InvocationTargetException e) {
        Throwable t = e.getTargetException();
        if (t == null) {
            throw new RuntimeException(e);
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new RuntimeException(e);
        }
    }

    protected Class<?>[] findClasses(ICS ics) {
        String site = ics.GetVar("site");
        Class<?> cs = null;
        Class<?> gc = null;

        if (StringUtils.isNotBlank(site)) {
            try {
                cs = gcl.loadClass("gsf." + site.toLowerCase() + ".ObjectFactory");
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        try {
            gc = gcl.loadClass("gsf.ObjectFactory");
        } catch (ClassNotFoundException e) {
            // ignore
        }
        if (cs == null) {
            if (gc == null) {
                return new Class[0];
            } else {
                return new Class[] { gc };
            }
        } else {
            if (gc == null) {
                return new Class[] { cs };
            } else {
                return new Class[] { cs, gc };
            }

        }
    }
}
