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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Default factory producer class that creates factory classes for the scopes specified.
 * <p>
 * Factory classes will be looked up in a configuration file called {@link #CONFIG_FILE}.
 * The format of the file is as follows: blank lines are allowed (and ignored); comments are
 * allowed (and ignored). The comment indicator is (#).
 * <p>
 * Each functional line of the configuration file has two parts, the factory class name, and the
 * scope class name. For example:
 * <code>scope.class.name : factory.class.name</code>
 * <p>
 * Each factory class name must have a two-argument constructor. The first argument is the scope class,
 * the very same class that is passed into the {@link #getFactory(Object)} method. The second
 * argument is the optional delegate factory that the specified factory will defer to if it is not
 * able to locate the specified object. (If no parent is specified, no delegation will occur).
 * <p>
 * Only one factory class per scope may be defined. If more than one is found, a configuration
 * error will be thrown.
 * <p>
 * By default, DefaultFactoryProducer knows how to get access to the ServletContext from within the
 * ICS object, and so a factory corresponding to the ServletContext scope will be used
 * as the delegate for the ICS scoped factory. Users may override either of these two and the
 * relationship will be preserved.
 * <p>
 * Conversely, this factory producer does not know the relationship between any custom scope
 * and the two it knows about, and so it is unable to automatically wire delegation between
 * factories involving custom scopes. To do this, it is necessary to extend this class (or
 * replace it).
 * <p>
 * Also, this factory producer does not know how to cache factories with custom scopes, so
 * new versions will be created whenever one is requested. For the known scopes, caching
 * is built-in.
 *
 * @author Tony Field
 * @since 2016-08-05
 */
public class DefaultFactoryProducer implements FactoryProducer {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFactoryProducer.class);

    /**
     * The configuration file(s) that this class will read while looking for
     * information about which factories to use
     */
    protected static final String CONFIG_FILE = "META-INF/gsf-factory";

    private final Map<Class, Constructor<Factory>> factoryConstructors;

    /**
     * Creates a new factory producer. This constructor pre-reads the configuration
     * files and validates that constructors exist for the scope specified.
     * <p>
     * If no factory is configured for ICS or ServletContext, a default is provided.
     */
    public DefaultFactoryProducer() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // read the configuration file
        Map<Class, Class> conf = lookupConfiguration(classLoader);

        // add defaults for known scopes
        if (!conf.containsKey(ICS.class)) {
            conf.put(ICS.class, IcsBackedFactory.class);
        }
        if (!conf.containsKey(ServletContext.class)) {
            conf.put(ServletContext.class, ServletContextBackedFactory.class);
        }
        
        // get the constructors
        factoryConstructors = getConstructorMap(conf);
        
        if (LOG.isDebugEnabled()) {
        	LOG.debug("DefaultFactoryProducer constructor is done... factoryConstructors = " + factoryConstructors);
        }

    }

    @Override
    public Factory getFactory(Object scope) {
        if (scope instanceof ICS) {
            return getFactory((ICS) scope);
        } else if (scope instanceof ServletContext) {
            return getFactory((ServletContext) scope);
        } else if (scope == null) {
            throw new IllegalArgumentException("Null scope not allowed");
        } else if (factoryConstructors.containsKey(scope.getClass())) {
            return createFactory(scope);
        } else {
            throw new IllegalArgumentException("Unsupported scope: " + scope.getClass().getName());
        }
    }

    /**
     * Create a custom-scoped factory. Note that there is no caching support for a custom scope (because
     * this class does not know enough about the scope to know how to cache against it).
     *
     * Subclasses can override this method and create their own internal cache.
     * @param customScope custom scope
     * @return the factory
     */
    protected Factory createFactory(Object customScope) {
        Constructor<Factory> con = factoryConstructors.get(customScope.getClass());
        try {
            return con.newInstance(customScope, null /* delegate is not knowable */);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate factory: " + e, e);
        }
    }

    /**
     * The ics object pool key for the ics-backed factory.
     */
    protected static final String ICS_CONTEXT_BACKED_FACTORY = "gsf-ics-backed-factory";

    /**
     * Get the ics-backed factory. Once created, the factory is cached on the ICS object
     * pool using {@link #ICS_CONTEXT_BACKED_FACTORY} as the key.
     *
     * @param ics ics context
     * @return the factory, never null
     */
    protected Factory getFactory(ICS ics) {
        Object o = ics.GetObj(ICS_CONTEXT_BACKED_FACTORY);
        if (o == null) {
            o = createFactory(ics);
            ics.SetObj(ICS_CONTEXT_BACKED_FACTORY, o);
        }
        return (Factory) o;
    }

    /**
     * Create a new instance of the ics-backed factory.
     * <p>
     * This implementation uses the constructor configured in the {@link #CONFIG_FILE}. It also
     * uses ICS to locate the ServletContext, and then gets the servletContext-backed factory
     * which it will use as a delegate for the ics-backed factory, if possible.
     *
     * @param ics the ics context
     * @return the factory
     */
    protected Factory createFactory(ICS ics) {

        Factory delegate;
        try {
            delegate = getFactory(ics.getIServlet().getServlet().getServletContext());
            LOG.debug("Creating ICS-backed factory that delegates to a servletContext-backed factory");
        } catch (RuntimeException e) {
            delegate = null;
            LOG.debug("Creating ICS-backed factory that has no delegate because no servletContext could be located");
        }

        Constructor<Factory> con = factoryConstructors.get(ICS.class);
        try {
            return con.newInstance(ics, delegate);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate factory: " + e, e);
        }
    }

    /**
     * The servlet context attribute key for the servletContext-backed factory.
     */
    protected static final String SERVLET_CONTEXT_BACKED_FACTORY = "gsf-servlet-context-backed-factory";

    /**
     * Get the servletContext-backed factory. Once created, the factory is cached on the servlet context
     * pool using {@link #SERVLET_CONTEXT_BACKED_FACTORY} as the key.
     *
     * @param servletContext the servlet context
     * @return the factory, never null
     */
    protected Factory getFactory(ServletContext servletContext) {
        Object o = servletContext.getAttribute(SERVLET_CONTEXT_BACKED_FACTORY);
        if (o == null) {
            o = createFactory(servletContext);
            servletContext.setAttribute(SERVLET_CONTEXT_BACKED_FACTORY, o);
        }
        return (Factory) o;
    }

    /**
     * Create a new instance of the servletContext-backed factory.
     * <p>
     * This implementation uses the constructor configured in the {@link #CONFIG_FILE}.
     *
     * @param servletContext the servlet context
     * @return the factory, never null
     */
    protected Factory createFactory(ServletContext servletContext) {
        Constructor<Factory> con = factoryConstructors.get(ServletContext.class);
        try {
            return con.newInstance(servletContext, null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate factory: " + e, e);
        }
    }

    /**
     * Look up the configuration for the FactoryProducer by reading the {@link #CONFIG_FILE}s found
     * in the specified ClassLoader's classpath. The configuration file format is described above.
     *
     * @param classLoader the classloader to do the looking
     * @return map containing the classes from the config file
     * @throws IllegalStateException if the config file format is invalid, if more than one
     *                               configuration setting is found for a given scope, or if the config
     *                               file points to classes that are not found.
     */
    protected static Map<Class, Class> lookupConfiguration(ClassLoader classLoader) {
        Map<Class, Class> config = new HashMap<>();
        Set<String> configLines = ReflectionUtils.readConfigurationResource(classLoader, CONFIG_FILE);
        for (String line : configLines) {
            String[] lineInfo = line.split(":");
            if (lineInfo.length != 2) {
                throw new IllegalStateException("Invalid configuration file format in " + CONFIG_FILE + ": " + line);
            }
            String scopeClassName = lineInfo[0];
            String factoryClassName = lineInfo[1];
            try {
                Class scopeClass = classLoader.loadClass(scopeClassName);
                if (config.containsKey(scopeClass)) {
                    throw new IllegalStateException("More than one matching configuration setting found for " +
                            scopeClass.getName() + " in " + CONFIG_FILE);
                }
                Class factoryClass = classLoader.loadClass(factoryClassName);
                config.put(scopeClass, factoryClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not find class listed in configuration file: " +
                        CONFIG_FILE + ": " + e, e);
            }
        }
        return config;
    }

    /**
     * Converts the map of classes to a map containing factory constructors as the value.
     * <p>
     * This class will look for a constructor with the signature described above, and will
     * fail if one is not found.
     *
     * @param config the mapping between scope and factory constructors to be used to create
     *               the factories
     * @return a map containing factory constructors for each scope. Never null
     * @throws IllegalStateException if no constructor can be found with the appropriate signature,
     *                               or if the class specified does not implement the Factory interface.
     */
    protected static Map<Class, Constructor<Factory>> getConstructorMap(Map<Class, Class> config) {
        Map<Class, Constructor<Factory>> constructorMap = new HashMap<>(config.size());
        for (Class scopeClass : config.keySet()) {
            Class configuredFactoryClass = config.get(scopeClass);
            if (Factory.class.isAssignableFrom(configuredFactoryClass)) {
                @SuppressWarnings("unchecked")
                Class<Factory> factoryClass = (Class<Factory>) configuredFactoryClass;
                try {
                    Constructor<Factory> constructor = factoryClass.getConstructor(scopeClass, Factory.class);
                    constructorMap.put(scopeClass, constructor);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Could not locate constructor with argument " + scopeClass.getName() + " for class " + factoryClass.getName());
                }
            } else {
                throw new IllegalStateException("Invalid configuration - class " + configuredFactoryClass.getName() + " does not implement " + Factory.class);
            }
        }
        return constructorMap;
    }
}
