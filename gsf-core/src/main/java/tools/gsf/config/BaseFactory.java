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

import COM.FutureTense.Interfaces.ICS;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory making use to reflection ({@link #reflectionStrategy(String, Class)}
 * and {@link #ctorStrategy(Class)}) to produce objects.
 * <p>
 * This class caches the produced objects for the lifetime of this object.
 * Effectively this means the lifetime of the ICS object.
 *
 * @author Dolf Dijkstra
 * @author Tony Field
 */
public abstract class BaseFactory implements Factory {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.BaseFactory");

    private final Map<String, Object> objectCache = new HashMap<>();
    private final ICS ics;
    private Factory[] roots = new Factory[0];

    public BaseFactory(ICS ics, Factory... roots) {
        this.ics = ics;
        if (roots != null) {
            this.roots = roots;
        }
    }

    @Override
    public final <T> T getObject(final String name, final Class<T> fieldType) {

        T o;
        try {
            o = locate(name, fieldType);
            if (o == null) {
                for (Factory root : roots) {
                    o = root.getObject(name, fieldType);
                    if (o != null) {
                        return o;
                    }
                }
                // only try ctor at the root level, otherwise it will be invoked on each BaseFactory
                if (roots.length == 0) {
                    o = ctorStrategy(fieldType);
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
     * @param c         current asset
     * @return the found service, null if no T can be created.
     * @throws InvocationTargetException exception from invocation
     */
    @SuppressWarnings("unchecked")
    private <T> T locate(final String askedName, final Class<T> c) throws InvocationTargetException {
        if (ICS.class.isAssignableFrom(c)) {
            return (T) ics;
        }
        if (c.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }
        final String name = StringUtils.isNotBlank(askedName) ? askedName : c.getSimpleName();
        if (StringUtils.isBlank(name)) {
            return null; // should not be possible - c would have to be anonymous.
        }

        Object o = locateInCache(c, name);
        if (o == null) {
            o = namedAnnotationStrategy(name, c);
        }
        if (o == null) {
            o = unnamedAnnotationStrategy(name, c);
        }
        if (o == null) {
            o = reflectionStrategy(name, c);
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

    /**
     * Method to find classes to use for the producer methods. This
     * implementation returns {@link #getClass()}.
     * <p>
     * Subclasses can return and are encouraged to return other classes.
     *
     * @param ics Content Server context object
     * @return array of classes to use for reflection
     */
    protected Class<?>[] factoryClasses(ICS ics) {
        return new Class[]{getClass()};
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

        for (Class<?> reflectionClass : factoryClasses(ics)) {
            for (Method m : reflectionClass.getMethods()) {
                if (m.isAnnotationPresent(ServiceProducer.class)) {
                    if (c.isAssignableFrom(m.getReturnType())) {
                        String n = m.getAnnotation(ServiceProducer.class).name();
                        if (name.equals(n)) {
                            return createFromMethod(name, c, m);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Tries to create the object based on the {@link ServiceProducer}
     * annotation without a name.
     *
     * @param <T>  object created based on service producer
     * @param name name
     * @param c    current asset
     * @return created object
     * @throws InvocationTargetException exception from invocation
     */
    private <T> T unnamedAnnotationStrategy(String name, Class<T> c) throws InvocationTargetException {

        for (Class<?> reflectionClass : factoryClasses(ics)) {
            for (Method m : reflectionClass.getMethods()) {
                if (m.isAnnotationPresent(ServiceProducer.class)) {
                    if (c.isAssignableFrom(m.getReturnType())) {
                        String n = m.getAnnotation(ServiceProducer.class).name();
                        if (StringUtils.isBlank(n)) {
                            return createFromMethod(name, c, m);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Reflection based producer method.
     * <p>
     * This method uses reflection to find producer methods to the following
     * rules:
     * <ul>
     * <li>public <b>static</b> Foo createFoo(ICS ics, Factory factory){}</li>
     * <li>public Foo createFoo(ICS ics){}</li>
     * </ul>
     * If the non-static version is used the implementing class needs to have a
     * public constructor that takes {@link ICS} and {@link Factory} as
     * arguments. To this class the current ICS and this object will be passed.
     *
     * @param <T>  returnable producer method or null
     * @param name the simple name of the object to produce
     * @param c    the class with the type information of the object to produce
     * @return the created object, null if no producer method was found or when
     * that method returned null.
     * @throws InvocationTargetException when the create&lt;Type&gt; method
     *                                   throws an exception.
     */
    private <T> T reflectionStrategy(String name, Class<T> c) throws InvocationTargetException {

        for (Class<?> reflectionClass : factoryClasses(ics)) {

            for (Method m : reflectionClass.getMethods()) {
                if (m.getName().equals("create" + c.getSimpleName())) {
                    if (c.isAssignableFrom(m.getReturnType())) {
                        return createFromMethod(name, c, m);
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param <T>  created method type
     * @param name name of the object
     * @param c    the type of the object to create
     * @param m    the method to use to create the object
     * @return created object
     * @throws InvocationTargetException exception from invoking specified method from class name
     */
    @SuppressWarnings("unchecked")
    private <T> T createFromMethod(String name, Class<T> c, Method m) throws InvocationTargetException {
        Object o = null;
        LOG.trace("trying to create a {} object with name {} from method {}", c.getName(), name, m.toGenericString());

        if (c.isAssignableFrom(m.getReturnType())) {
            Object from = null;

            if (!Modifier.isStatic(m.getModifiers())) {
                Class<?> reflectionClass = m.getDeclaringClass();
                if (reflectionClass.isAssignableFrom(getClass())) {
                    from = this; // TODO will this clash if this class and declared class have same parent class?
                } else {
                    Constructor<?> ctor;

                    try {
                        ctor = reflectionClass.getConstructor(ICS.class, Factory.class);
                        if (Modifier.isPublic(ctor.getModifiers())) {
                            from = ctor.newInstance(ics, this);
                        } else {
                            throw new IllegalStateException(reflectionClass.getName() + " does not have a public (ICS,Factory) constructor.");
                        }
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException(reflectionClass.getName() + " should have a public constructor accepting a ICS and Factory.");
                    } catch (InstantiationException e) {
                        LOG.error(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        LOG.error("Huh, Can't happen, the arguments are checked: " + m.toString() + ", " + e.getMessage());
                    } catch (IllegalAccessException e) {
                        LOG.error("Huh, Can't happen, the modifier is checked for public: " + m.toString() + ", " + e.getMessage());
                    }
                }
            }
            if (m.getParameterTypes().length == 2
                    && m.getParameterTypes()[0].isAssignableFrom(ICS.class)
                    && m.getParameterTypes()[1].isAssignableFrom(Factory.class)) {
                o = invokeCreateMethod(m, from, ics, this);
            } else if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0].isAssignableFrom(ICS.class)) {
                o = invokeCreateMethod(m, from, ics);
            } else if (m.getParameterTypes().length == 0) {
                o = invokeCreateMethod(m, from);
            }
            if (shouldCache(m)) {
                objectCache.put(name, o);
            }
        }
        return (T) o;
    }

    /**
     * @param m         method in invoke
     * @param from      object to invoke from
     * @param arguments the arguments to pass to the method
     * @return object from invoked method
     * @throws InvocationTargetException exception from invocation
     */
    private Object invokeCreateMethod(Method m, Object from, Object... arguments) throws InvocationTargetException {
        try {
            return m.invoke(from, arguments);
        } catch (IllegalArgumentException e) {
            LOG.error("Huh, can't happen, the arguments are checked: {}, {}", m.toString(), e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.error("Huh, can't happen, the modifier is checked for public: {}, {}", m.toString(), e.getMessage());
        }
        return null;
    }

    private boolean shouldCache(Method m) {
        boolean r = false;
        if (m.isAnnotationPresent(ServiceProducer.class)) {
            ServiceProducer ann = m.getAnnotation(ServiceProducer.class);
            r = ann.cache();
        }
        return r;
    }

    /**
     * @param <T> invoked object given class
     * @param c   current asset
     * @return newly created object
     * @throws InvocationTargetException exception from invoking constructor
     */
    private <T> T ctorStrategy(final Class<T> c) throws InvocationTargetException {
        T o = null;
        try {
            if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
                LOG.debug("Could not create a {} via a Template method. The class {} is an interface or abstract class. Giving up as a class cannot be constructed", c.getName(), c.getName());
                return null;
            }
            LOG.debug("Could not create a {} via a Template method. Trying via constructor.", c.getName());
            final Constructor<T> constr = c.getConstructor(ICS.class);
            o = constr.newInstance(ics);
        } catch (final NoSuchMethodException
                | IllegalArgumentException
                | InstantiationException
                | IllegalAccessException e1) {
            LOG.debug("Could not create a {} via a constructor method.", c.getName());
        }
        return o;
    }

    @Override
    public String toString() {
        return "BaseFactory [roots=" + Arrays.toString(roots) + "]";
    }
}