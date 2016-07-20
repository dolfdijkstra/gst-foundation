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

import tools.gsf.time.LoggerStopwatch;
import tools.gsf.time.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;

/**
 * Helper to inject dependencies into Object based on annotated fields and
 * methods.
 * 
 * @author Dolf Dijkstra
 * @since Mar 26, 2011
 * @deprecated - class due for rewriting
 */
public final class AnnotationInjector {
	private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.controller.action.AnnotationInjector");

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
    public static void inject(final Object object, final Factory factory) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory cannot be null.");
        }
        Stopwatch stopwatch = LoggerStopwatch.getInstance(); // TODO: dependency injection breakdown in static method
        try {
            Class<?> c = object.getClass();
            // first to all annotated public setter methods.
            for (final Method method : c.getMethods()) {
                if (method.isAnnotationPresent(InjectForRequest.class)) {
                    injectIntoMethod(object, factory, method);
                }
            }
            // and then all annotated fields.
            while (c != Object.class && c != null) {
                for (final Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(InjectForRequest.class)) {
                        injectIntoField(object, factory, field);
                    }

                }

                c = c.getSuperclass();
            }
        } finally {
            stopwatch.elapsed("inject model for {}", object.getClass().getName());
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
    public static Field[] findFieldsWithAnnotation(final Object object,
            final Class<? extends Annotation> annnotationClass) {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null.");
        }
        if (annnotationClass == null) {
            throw new IllegalArgumentException("clazz must not be null.");
        }
        final List<Field> x = new ArrayList<>();
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
     * @param object the object to inject into
     * @param factory the factory that created the objects that need to be
     *            injected.
     * @param field field to inject into
     * @throws SecurityException security exception injecting values into field
     */
    private static void injectIntoField(final Object object, final Factory factory, final Field field)
            throws SecurityException {

        final InjectForRequest ifr = field.getAnnotation(InjectForRequest.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }
        final Object injectionValue = factory.getObject(name, field.getType());
        if (injectionValue == null) {
            throw new InjectionException(factory.getClass().getName() + " does not know how to inject '"
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
            throw new InjectionException("IllegalArgumentException injecting " + injectionValue + " into field "
                    + field.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new InjectionException("IllegalAccessException injecting " + injectionValue + " into field "
                    + field.getName(), e);
        }
    }

    /**
     * @param object the object to inject into
     * @param factory the factory that created the objects that need to be
     *            injected.
     * @param method the method to inject into
     * @throws SecurityException security exception when injecting value into field
     */
    private static void injectIntoMethod(final Object object, final Factory factory, final Method method)
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
            throw new InjectionException(factory.getClass().getName() + " does not know how to inject '" + type.getName()
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
            throw new InjectionException("IllegalArgumentException injecting " + injectionValue + " into method "
                    + method.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new InjectionException("IllegalAccessException injecting " + injectionValue + " into method "
                    + method.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new InjectionException("InvocationTargetException injecting " + injectionValue
                    + " into method " + method.getName(), e);
        }
    }

}
