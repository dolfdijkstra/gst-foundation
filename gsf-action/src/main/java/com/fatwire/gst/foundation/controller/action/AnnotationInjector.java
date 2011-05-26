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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

/**
 * Helper to inject dependencies into Object based on annotated fields and
 * methods.
 * 
 * @author Dolf Dijkstra
 * @since Mar 26, 2011
 */
public class AnnotationInjector {
    protected static final Log LOG = LogFactory.getLog(AnnotationInjector.class.getPackage().getName());
    protected static final Log LOG_TIME = LogFactory.getLog(AnnotationInjector.class.getPackage().getName() + ".time");

    /**
     * Inject ICS runtime objects into the object. Objects flagged with the
     * {@link InjectForRequest} annotation will be populated by this method by
     * retrieving the value from the {@link Factory#getObject(String,Class)}
     * method.
     * 
     * @param object the object to inject into
     * @param factory the factory that created the objects that need to be
     *            injected.
     */
    public static final void inject(final Object object, final Factory factory) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory cannot be null.");
        }
        final long start = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0L;
        try {
            Class<?> c = object.getClass();
            while (c != Object.class && c != null) {
                for (final Field field : c.getDeclaredFields()) {
                    // LOG.trace("Found field: "+field.getName());
                    if (field.isAnnotationPresent(InjectForRequest.class)) {
                        injectIntoField(object, factory, field);
                    }

                }
                for (final Method method : c.getMethods()) {
                    // LOG.trace("Found field: "+field.getName());
                    if (method.isAnnotationPresent(InjectForRequest.class)) {
                        injectIntoMethod(object, factory, method);
                    }

                }

                c = c.getSuperclass();
            }
        } finally {
            DebugHelper.printTime(LOG_TIME, "inject model for " + object.getClass().getName(), start);
        }
    }

    /**
     * Finds the fields in the class or super class that are annotated with the
     * <tt>annnotationClass</tt> annotation.
     * 
     * @param object the object to inspect.
     * @param annnotationClass the annotation to find.
     * @return the array of fields with the annotation, never null.
     */
    public static final Field[] findFieldsWithAnnotation(final Object object,
            final Class<? extends Annotation> annnotationClass) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null.");
        }
        if (annnotationClass == null) {
            throw new IllegalArgumentException("clazz must not be null.");
        }
        final List<Field> x = new ArrayList<Field>();
        Class<?> c = object.getClass();
        while (c != Object.class && c != null) {
            for (final Field field : c.getDeclaredFields()) {
                // LOG.trace("Found field: "+field.getName());
                if (field.isAnnotationPresent(annnotationClass)) {
                    x.add(field);
                }

            }

            c = c.getSuperclass();
        }
        return x.toArray(new Field[x.size()]);
    }

    /**
     * @param object
     * @param factory
     * @param field
     * @throws SecurityException
     */
    public static void injectIntoField(final Object object, final Factory factory, final Field field)
            throws SecurityException {

        final InjectForRequest ifr = field.getAnnotation(InjectForRequest.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }
        final Object injectionValue = factory.getObject(name, field.getType());
        if (injectionValue == null) {
            throw new RuntimeException(factory.getClass().getName() + " does not know how to inject '"
                    + field.getType().getName() + "' into the field '" + field.getName() + "' for an action.");
        }
        field.setAccessible(true); // make private fields accessible
        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + injectionValue.getClass().getName() + " into field " + field.getName() + " for "
                    + object.getClass().getName());
        }
        try {
            field.set(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("IllegalArgumentException injecting " + injectionValue + " into field "
                    + field.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("IllegalAccessException injecting " + injectionValue + " into field "
                    + field.getName(), e);
        }
    }

    /**
     * @param object
     * @param factory
     * @param method
     * @throws SecurityException
     */
    public static void injectIntoMethod(final Object object, final Factory factory, final Method method)
            throws SecurityException {
        // LOG.trace("Found annotated field: "+field.getName());
        final InjectForRequest ifr = method.getAnnotation(InjectForRequest.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = BeanUtils.findPropertyForMethod(method).getName();
        }

        final Class<?> type = method.getParameterTypes()[0];
        final Object injectionValue = factory.getObject(name, type);
        if (injectionValue == null) {
            throw new RuntimeException(factory.getClass().getName() + " does not know how to inject '" + type.getName()
                    + "' into the field '" + method.getName() + "' for an action.");
        }

        // accessible
        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + injectionValue.getClass().getName() + " into field " + method.getName() + " for "
                    + object.getClass().getName());
        }
        try {
            method.invoke(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("IllegalArgumentException injecting " + injectionValue + " into method "
                    + method.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException("IllegalAccessException injecting " + injectionValue + " into method "
                    + method.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new IllegalArgumentException("InvocationTargetException injecting " + injectionValue
                    + " into method " + method.getName(), e);
        }
    }

}
