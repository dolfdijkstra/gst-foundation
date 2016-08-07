/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.config.inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import tools.gsf.config.Factory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Tony Field
 * @since 2016-07-21
 */
public final class InjectForRequestInjector {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.inject.InjectForRequestInjector");

    private final Factory factory;

    public InjectForRequestInjector(Factory factory) {
        this.factory = factory;
    }

    /**
     * Inject ICS runtime objects into the object. Objects flagged with the
     * {@link InjectForRequest} annotation will be populated by this method by
     * retrieving the value from the {@link Factory#getObject(String, Class)}
     * method.
     *
     * @param target the object to inject into
     */
    public void inject(final Object target) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null.");
        }

        Class<?> c = target.getClass();
        // first to all annotated public setter methods.
        for (final Method method : c.getMethods()) {
            if (method.isAnnotationPresent(InjectForRequest.class)) {
                injectIntoMethod(target, factory, method);
            }
        }
        // and then all annotated fields.
        while (c != Object.class && c != null) {
            for (final Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectForRequest.class)) {
                    injectIntoField(target, factory, field);
                }
            }
            c = c.getSuperclass();
        }
    }

    /**
     * @param object  the object to inject into
     * @param factory the factory that created the objects that need to be
     *                injected.
     * @param field   field to inject into
     * @throws SecurityException security exception injecting values into field
     */
    private static void injectIntoField(final Object object, final Factory factory, final Field field) throws SecurityException {

        final InjectForRequest ifr = field.getAnnotation(InjectForRequest.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }
        final Object injectionValue = factory.getObject(name, field.getType());
        if (injectionValue == null) {
            throw new InjectionException(factory.getClass().getName() + " does not know how to inject '" + field.getType().getName() + "' into the field '" + field.getName() + "' for an action.");
        }
        field.setAccessible(true); // make private fields accessible
        LOG.debug("Injecting {} into field {} for {}", injectionValue.getClass().getName(), field.getName(), object.getClass().getName());
        try {
            field.set(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new InjectionException("IllegalArgumentException injecting " + injectionValue + " into field " + field.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new InjectionException("IllegalAccessException injecting " + injectionValue + " into field " + field.getName(), e);
        }
    }

    /**
     * @param object  the object to inject into
     * @param factory the factory that created the objects that need to be
     *                injected.
     * @param method  the method to inject into
     * @throws SecurityException security exception when injecting value into field
     */
    private static void injectIntoMethod(final Object object, final Factory factory, final Method method) throws SecurityException {
        final InjectForRequest ifr = method.getAnnotation(InjectForRequest.class);

        String name = ifr.value();
        if (StringUtils.isBlank(name)) {
            name = BeanUtils.findPropertyForMethod(method).getName();
        }

        final Class<?> type = method.getParameterTypes()[0];
        final Object injectionValue = factory.getObject(name, type);
        if (injectionValue == null) {
            throw new InjectionException(factory.getClass().getName() + " does not know how to inject '" + type.getName() + "' into the field '" + method.getName() + "' for an action.");
        }

        // accessible
        LOG.debug("Injecting {} into field {} for {}", injectionValue.getClass().getName(), method.getName(), object.getClass().getName());
        try {
            method.invoke(object, injectionValue);
        } catch (final IllegalArgumentException e) {
            throw new InjectionException("IllegalArgumentException injecting " + injectionValue + " into method " + method.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new InjectionException("IllegalAccessException injecting " + injectionValue + " into method " + method.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new InjectionException("InvocationTargetException injecting " + injectionValue + " into method " + method.getName(), e);
        }
    }
}
