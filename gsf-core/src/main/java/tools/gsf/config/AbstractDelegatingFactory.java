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
package tools.gsf.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class caches the produced objects for the lifetime of this object.
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2016-08-06
 */
public abstract class AbstractDelegatingFactory<SCOPE> implements Factory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDelegatingFactory.class);

    private final Map<String, Object> objectCache = new HashMap<>();

    private final SCOPE scope;
    private final Factory delegate;

    protected AbstractDelegatingFactory(SCOPE scope, Factory delegate) {
        this.scope = scope;
        this.delegate = delegate;
    }

    @Override
    public final <T> T getObject(final String name, final Class<T> fieldType) {

        T o;
        try {
            // try to locate it internally
            o = locate(name, fieldType);
            if (o != null) {
                LOG.debug("Located object {} of type {} in scope {}", name, fieldType.getName(), this.getClass().getName());
            } else {
                // delegate to another factory
            	LOG.debug("Did NOT locate object {} of type {} in scope {}", name, fieldType.getName(), this.getClass().getName());
                if (delegate != null) {
                	LOG.debug("Will attempt locating object {} of type {} in delegate {}", name, fieldType.getName(), delegate.getClass().getName());
                    o = delegate.getObject(name, fieldType);
                    if (o != null) {
                        LOG.debug("Located object {} of type {} in delegate {}", name, fieldType.getName(), delegate.getClass().getName());
                    }
                } else {
	                // we can't build it and we can't delegate it.
	                // try looking for a constructor
                	LOG.debug("Cannot delegate lookup onto any other scope.");
                	LOG.debug("Cannot locate object {} of type {} in delegate {}", name, fieldType.getName(), delegate.getClass().getName());
                }
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
        return o;
    }

    /**
     * Internal method to check for Services or create Services.
     *
     * @param <T>       ics or cached object
     * @param askedName name of asset to find
     * @param fieldType current asset
     * @return the found service, null if no T can be created.
     * @throws InvocationTargetException exception from invocation
     */
    @SuppressWarnings("unchecked")
    private <T> T locate(final String askedName, final Class<T> fieldType) throws InvocationTargetException {
        if (scope.getClass().isAssignableFrom(fieldType)) {
            return (T) scope;
        }
        if (fieldType.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }
        final String name = StringUtils.isNotBlank(askedName) ? askedName : fieldType.getSimpleName();
        if (StringUtils.isBlank(name)) {
            return null; // should not be possible - fieldType cannot be anonymous.
        }

        Object o = locateInCache(fieldType, name);
        if (o == null) {
            o = namedAnnotationStrategy(name, fieldType);
        }
        if (o == null) {
            o = unnamedAnnotationStrategy(name, fieldType);
        }
        return (T) o;
    }

    private <T> Object locateInCache(Class<T> c, String name) {
        Object o = objectCache.get(name);
        if (o != null && !c.isAssignableFrom(o.getClass())) {
            throw new IllegalStateException("Name conflict: '" + name + "' is in cache and is of type  '"
                    + o.getClass() + "' but a '" + c.getName()
                    + "' was asked for. Please check your factories for naming conflicts.");
        }
        return o;
    }

    private static boolean shouldCache(Method m) {
        boolean r = false;
        if (m.isAnnotationPresent(ServiceProducer.class)) {
            ServiceProducer ann = m.getAnnotation(ServiceProducer.class);
            r = ann.cache();
        }
        return r;
    }

    /**
     * Tries to create the object based on the {@link ServiceProducer}
     * annotation where the names match.
     *
     * @param <T>  object created by service producer
     * @param name name
     * @param c    current asset
     * @return created object
     * @throws InvocationTargetException exception from invocation
     */
    private <T> T namedAnnotationStrategy(String name, Class<T> c) throws InvocationTargetException {

        for (Method m : this.getClass().getMethods()) {
            if (m.isAnnotationPresent(ServiceProducer.class)) {
                if (c.isAssignableFrom(m.getReturnType())) {
                    String n = m.getAnnotation(ServiceProducer.class).name();
                    if (name.equals(n)) {
                        return constructAndCacheObject(name, c, m);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Tries to create the object based on the {@link tools.gsf.config.ServiceProducer}
     * annotation without a name.
     *
     * @param <T>  object created based on service producer
     * @param name name
     * @param c    current asset
     * @return created object
     * @throws InvocationTargetException exception from invocation
     */
    private <T> T unnamedAnnotationStrategy(String name, Class<T> c) throws InvocationTargetException {
        for (Method m : this.getClass().getMethods()) {
            if (m.isAnnotationPresent(ServiceProducer.class)) {
                if (c.isAssignableFrom(m.getReturnType())) {
                    String n = m.getAnnotation(ServiceProducer.class).name();
                    if (StringUtils.isBlank(n)) {
                        return constructAndCacheObject(name, c, m);
                    }
                }
            }
        }
        return null;
    }

    private <T> T constructAndCacheObject(String name, Class<T> c, Method m) throws InvocationTargetException {
        switch (m.getParameterCount()) {
            case 0: {
                T result = ReflectionUtils.createFromMethod(name, c, this, m);
                if (shouldCache(m)) {
                    objectCache.put(name, result);
                }
                return result;
            }
            case 1: {
                Class type = m.getParameterTypes()[0];
                if (type.isAssignableFrom(scope.getClass())) {
                    T result = ReflectionUtils.createFromMethod(name, c, this, m, scope);
                    if (shouldCache(m)) {
                        objectCache.put(name, result);
                    }
                    return result;
                } else {
                    throw new UnsupportedOperationException("Cannot create object with parameter type " + type.getName() + " using method " + m.getName() + " in class " + m.getDeclaringClass().getName());
                }
            }
            default: {
                throw new UnsupportedOperationException("Cannot create object using method " + m.getName() + " in class " + m.getDeclaringClass().getName() + " - invalid number of parameters");
            }
        }
    }

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName();
        return "{" + s + (delegate == null ? "}" : "::delegate:" + delegate.getClass().getName() + "}");
    }
}
