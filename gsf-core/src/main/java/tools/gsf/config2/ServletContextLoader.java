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
package tools.gsf.config2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static tools.gsf.config2.ReflectionUtils.readConfigurationResource;

/**
 * ServletContextListener that loads and configures the FactoryProducer for this
 * application.
 * <p>
 * It is configured to auto-load into the servlet context by way of the
 * {@code @WebListener} annotation.  Overriding of this class is allowed, and overridden
 * classes that are annotated with {@code @WebListener} will take precedence over this one.
 * <p>
 * By default, this class looks for the FactoryProducer class in a servletContext init
 * parameter called {@link #GSF_FACTORY_PRODUCER}. The class should have a public zero-arg
 * constructor.
 * <p>
 * If no servlet context init parameter is found, this loader will search the classPath for
 * a resource called {@link #CONFIG_FILE}. This file can contain blank lines and comments
 * (denoted by #) but must contain only one active line - a class name of a class implementing
 * the FactoryProducer interface. If more than one class name is found, an exception
 * will be thrown.
 * <p>
 * If neither of the above are found, {@link DefaultFactoryProducer} will be instantiated
 * as the factory producer.
 * <p>
 * When the servlet context is initialized, the factory producer that is created is registered
 * the servlet context using the parameter {@link #GSF_FACTORY_PRODUCER}, and removed when the
 * servlet context is destroyed.
 *
 * @author Tony Field
 * @since 2016-08-05
 */
@WebListener
public class ServletContextLoader implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServletContextLoader.class);

    /**
     * Name of the servlet context init parameter containing the factory producer to be booted
     */
    public static final String GSF_FACTORY_PRODUCER = "gsf-factory-producer";

    /**
     * Name of config file where the factory producer class is configured.
     */
    public static final String CONFIG_FILE = "META-INF/gsf-factory-producer";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final ServletContext context = servletContextEvent.getServletContext();
        if (context.getMajorVersion() == 3 && context.getMinorVersion() < 0) {
            throw new IllegalStateException("Servlet Container is configured for version less than 3.0. " +
                    "This ServletContextListener does not support 2.x and earlier as the load order of " +
                    "Listeners is not guaranteed.");
        }

        FactoryProducer factoryProducer = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (factoryProducer == null) {
            factoryProducer = configureFromInitParam(context, classLoader);
            if (factoryProducer != null) {
                LOG.info("FactoryProducer configured from init param: " + factoryProducer.getClass().getName());
            }
        }

        if (factoryProducer == null) {
            factoryProducer = configureFromServiceLocator(classLoader);
            if (factoryProducer != null) {
                LOG.info("FactoryProducer configured from service locator: " + factoryProducer.getClass().getName());
            }
        }

        if (factoryProducer == null) {
            factoryProducer = new DefaultFactoryProducer();
            LOG.info("FactoryProducer defaulting to: " + factoryProducer.getClass().getName());
        }

        context.setAttribute(GSF_FACTORY_PRODUCER, factoryProducer);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().removeAttribute(GSF_FACTORY_PRODUCER);
        LOG.info("FactoryProducer un-registered from servlet context.");
    }

    private FactoryProducer configureFromInitParam(ServletContext servletContext, ClassLoader classLoader) {
        String factoryProducerClassName = servletContext.getInitParameter(GSF_FACTORY_PRODUCER);
        return instantiateFactoryProducer(factoryProducerClassName, classLoader);
    }

    private FactoryProducer configureFromServiceLocator(ClassLoader classLoader) {
        Set<String> classes = readConfigurationResource(classLoader, CONFIG_FILE);
        switch (classes.size()) {
            case 0: {
                // not found
                return null;
            }
            case 1: {
                // found it!
                String fpClass = classes.iterator().next();
                return instantiateFactoryProducer(fpClass, classLoader);
            }
            default: {
                // too many
                throw new IllegalStateException("Too many lines in the " + CONFIG_FILE +
                        " configuration file. Only one FactoryProducer class name is allowed.");
            }
        }
    }

    private static FactoryProducer instantiateFactoryProducer(String className, ClassLoader classLoader) {
        if (className == null) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Class<FactoryProducer> cls = (Class<FactoryProducer>) classLoader.loadClass(className);
            Constructor<FactoryProducer> constructor = cls.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find FactoryProducer class: " + className, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not locate zero-arg constructor for FactoryProducer in class: " + className, e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not invoke constructor for FactoryProducer class: " + className, e);
        }
    }
}
