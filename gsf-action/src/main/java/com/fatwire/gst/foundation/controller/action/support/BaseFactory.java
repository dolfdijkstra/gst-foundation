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
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

/**
 * Factory making use to reflection ({@link #reflectionStrategy(String, Class)}
 * and {@link #ctorStrategy(String, Class)}) to produce objects.
 * <p/>
 * This class caches the produced objects for the lifetime of this object.
 * Effectively this means the lifetime of the ICS object.
 * 
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
                // only try ctor at the root level, otherwise
                // it will be invoked on each BaseFactory
                if (roots.length == 0)
                    o = ctorStrategy(name, fieldType);
            }
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
        }
        return (T) o;
    }

    /**
     * Method to find classes to use for the producer methods. This
     * implementation returns {@link #getClass()}.</p> Subclasses can return and
     * are encouraged to return other classes.
     * 
     * @param ics
     * @return array of classes to use for reflection
     */
    protected Class<?>[] findClasses(ICS ics) {
        return new Class[] { getClass() };
    }

    /**
     * Reflection based producer method.
     * <p/>
     * This method uses reflection to find producer methods to the following
     * rules:
     * <ul>
     * <li>public <b>static</b> Foo createFoo(ICS ics, Factory factory){}</li>
     * <li>public Foo createFoo(ICS ics){}</li>
     * </ul>
     * If the non-static version is used the implementing class needs to have a
     * public constructor that takes {@see ICS} and {@see Factory} as arguments.
     * To this class the current ICS and this object will be passed.
     * 
     * 
     * @param name the simple name of the object to produce
     * @param c the class with the type information of the object to produce
     * @return the created object, null if no producer method was found or when
     *         that method returned null.
     * @throws InvocationTargetException when the create&lt;Type&gt; method
     *             throws an exception.
     */
    @SuppressWarnings("unchecked")
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
                                if (shouldCache(m))
                                    objectCache.put(c.getName(), o);

                            } catch (IllegalArgumentException e) {
                                LOG.error("Huh, Can't happen, the arguments are checked: " + m.toString() + ", "
                                        + e.getMessage());
                            } catch (IllegalAccessException e) {
                                LOG.error("Huh, Can't happen, the modifier is checked for public: " + m.toString()
                                        + ", " + e.getMessage());
                            }
                            return o;
                        } else if (m.getParameterTypes().length == 1
                                && m.getParameterTypes()[0].isAssignableFrom(ICS.class)) {
                            try {
                                Object factory = null;
                                if (reflectionClass.equals(getClass())) {
                                    factory = this;
                                } else {
                                    Constructor<?> ctor;

                                    ctor = reflectionClass.getConstructor(ICS.class, Factory.class);
                                    if (Modifier.isPublic(ctor.getModifiers())) {
                                        factory = ctor.newInstance(ics, this);

                                    } else {
                                        LOG.warn(reflectionClass.getName()
                                                + " does not have a public (ICS,Factory) constructor.");
                                    }
                                }
                                if (factory != null) {
                                    if (LOG.isTraceEnabled())
                                        LOG.trace("creating " + name + " from " + m.getName());
                                    o = (T) m.invoke(factory, ics);
                                    if (shouldCache(m))
                                        objectCache.put(c.getName(), o);
                                }
                                return o;

                            } catch (SecurityException e) {
                                LOG.debug("Huh, : " + m.toString());

                            } catch (NoSuchMethodException e) {
                                throw new NoSuchMethodExceptionRuntimeException(reflectionClass.getName()
                                        + " should have a public constructor accepting a ICS and Factory.");
                            } catch (IllegalArgumentException e) {
                                LOG.error("Huh, Can't happen, the arguments are checked: " + m.toString() + ", "
                                        + e.getMessage());
                            } catch (InstantiationException e) {
                                LOG.error(e.getMessage());
                            } catch (IllegalAccessException e) {
                                LOG.error("Huh, Can't happen, the modifier is checked for public: " + m.toString()
                                        + ", " + e.getMessage());
                            }
                        }
                    }

                }
            }
        }
        return o;
    }

    protected boolean shouldCache(Method m) {
        boolean r = false;
        if (m.isAnnotationPresent(ServiceProducer.class)) {
            ServiceProducer annon = m.getAnnotation(ServiceProducer.class);
            r = annon.cache();
        }
        return r;
    }

    /**
     * @param e
     */
    protected void throwRuntimeException(InvocationTargetException e) {
        Throwable t = e.getTargetException();
        if (t == null) {
            throw new RuntimeException(e);
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new RuntimeException(t);
        }
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

    @ServiceProducer(cache = false)
    public ICS createICS(final ICS ics) {
        return ics;
    }

}
