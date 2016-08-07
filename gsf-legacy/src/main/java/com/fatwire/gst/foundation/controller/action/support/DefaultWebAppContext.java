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
package com.fatwire.gst.foundation.controller.action.support;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.support.WebAppContext;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the WebAppContext with accessor to the injector but without accessors
 * to the ActionLocator and ActionNameResolver, with the companion FactoryProducer.
 *
 * Developers are expected to either subclass this class or use as a reference
 * for their own implementations.
 *
 * In most cases they would only like to override {@link #getFactory(ICS)} for
 * their own Service factory.
 *
 * @author Freddy Villalba
 * @deprecated see {@link tools.gsf.config.DefaultWebAppContext}
 */
public class DefaultWebAppContext extends WebAppContext implements FactoryProducer {

    private static final String FACTORY_CONFIG_FILE = "META-INF/gsf-factory";
    private static final Class[] FACTORY_CONSTRUCTOR_ARGS = {ICS.class};

    private final Constructor<Factory> factoryConstructor;

    public DefaultWebAppContext(final ServletContext context) {
        this(context, null);
    }

    public DefaultWebAppContext(final ServletContext context, final AppContext parent) {
        super(context, parent);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Constructor<Factory> f = _findFactoryClassConstructorFromServiceLocator(classLoader);
        if (f == null) {
            this.factoryConstructor = _createFactoryConstructor(classLoader, SimpleIcsBackedObjectFactoryTemplate.class.getName());
        } else {
            this.factoryConstructor = f;
        }
        LOG.debug("DefaultWebAppContext instance bound to factoryConstructor = " + this.factoryConstructor);
    }

    public Injector createInjector() {
        FactoryProducer fp = getBean("factoryProducer", FactoryProducer.class);
        return new DefaultAnnotationInjector(fp);
    }

    public FactoryProducer createFactoryProducer() {
        return this;
    }

    @Override
    public Factory getFactory(final ICS ics) {
        Factory result;
        try {
            result = factoryConstructor.newInstance(ics);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not instantiate factory (illegal access)", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate factory (invocation target)", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate factory (instantiation)", e);
        }
        return result;
    }

    private Constructor<Factory> _findFactoryClassConstructorFromServiceLocator(ClassLoader classLoader) {

        int c = 0;
        List<String> init = new LinkedList<String>();
        Enumeration<URL> configs;
        try {
            configs = classLoader.getResources(FACTORY_CONFIG_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Could not search classpath for configuration files", e);
        }
        while (configs.hasMoreElements()) {

            URL u = configs.nextElement();
            if (c++ > 0) {
                throw new IllegalStateException("Found second service locator in classpath at " + u
                        + ". Please make sure that only one " + FACTORY_CONFIG_FILE
                        + " file is found on the classpath or configure the Factory through a custom " + AppContext.class.getName());
            }
            InputStream in = null;
            BufferedReader r = null;
            try {
                in = u.openStream();
                r = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String s;
                while ((s = r.readLine()) != null) {
                    if (StringUtils.isNotBlank(s) && !StringUtils.startsWith(s, "#")) {
                        init.add(s);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading configuration file", e);
            } finally {
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error closing configuration file", e);
                }
            }
            return _createFactoryConstructor(classLoader, init.get(0));
        }
        return null;
    }

    private Constructor<Factory> _createFactoryConstructor(ClassLoader classLoader, String clazz) {
        try {
            final Class<Factory> cls = (Class<Factory>) classLoader.loadClass(clazz);
            return cls.getConstructor(FACTORY_CONSTRUCTOR_ARGS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not locate class " + clazz, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not find constructor for " + clazz + " that took ICS as an argument");
        }
    }
}
