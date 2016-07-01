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
package com.fatwire.gst.foundation.controller.support;

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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.support.DefaultWebAppContext;

/**
 * ServletContextListener that loads and configures the AppContext for this
 * application.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class WebAppContextLoader implements ServletContextListener {
    public static final String CONTEXTS = "gsf-contexts";

    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.controller.support.WebAppContextLoader");
    boolean booted = false;
    private static final Class<?>[] ARGS = new Class[] { ServletContext.class, AppContext.class };

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext context = sce.getServletContext();
        if (context.getMajorVersion() == 2 && context.getMinorVersion() < 4) {
            throw new IllegalStateException(
                    "Servlet Container is configured for version 2.3 or less. This ServletContextListener does not support 2.3 and earlier as the load order of Listeners is not guaranteed.");
        }

        configureWebAppContext(context);

    }

    public AppContext configureWebAppContext(final ServletContext context) {
        AppContext parent = null;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

        parent = configureFromInitParam(context, cl);
        if (parent == null) {
            try {
                parent = configureFromServiceLocator(context, cl);
            } catch (IOException e) {
                LOG.debug("Exception when loadding the service descriptor for the AppContext from the classpath.", e);
            }
        }
        if (parent == null) {
            parent = new DefaultWebAppContext(context);
            parent.init();
        }

        if (parent != null) {
            context.setAttribute(WebAppContext.WEB_CONTEXT_NAME, parent);
        }
        booted = true;
        return parent;

    }

    private static final String PREFIX = "META-INF/";

    private AppContext configureFromServiceLocator(ServletContext context, ClassLoader cl) throws IOException {
        String fullName = PREFIX + CONTEXTS;

        int c = 0;
        List<String> init = new LinkedList<String>();
        Enumeration<URL> configs = cl.getResources(fullName);
        while (configs.hasMoreElements()) {

            URL u = configs.nextElement();
            if (c++ > 0)
                throw new IllegalStateException("Found second service locator in classpath at " + u
                        + ". Please make sure that only one " + fullName
                        + " file is found on the classpath or configure the AppContext through web.xml");
            InputStream in = null;
            BufferedReader r = null;

            try {
                in = u.openStream();
                r = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String s = null;
                while ((s = r.readLine()) != null) {
                    if (StringUtils.isNotBlank(s) && !StringUtils.startsWith(s, "#")) {
                        init.add(s);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading configuration file", e);
            } finally {
                try {
                    if (r != null)
                        r.close();
                    if (in != null)
                        in.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error closing configuration file", e);
                }
            }
            return this.createFromString(context, cl, init.toArray(new String[init.size()]));
        }

        return null;
    }

    /**
     * Creates a AppContext based on the init parameter {@link #CONTEXTS}.
     * 
     * @param context servlet context
     * @param cl class loader
     * @return the AppContext as configured from the web app init parameter.
     */
    protected AppContext configureFromInitParam(final ServletContext context, final ClassLoader cl) {
        final String init = context.getInitParameter(CONTEXTS);
        AppContext parent = null;

        if (init != null) {
            parent = createFromString(context, cl, init.split(","));
        }
        return parent;
    }

    private AppContext createFromString(final ServletContext context, final ClassLoader cl, final String[] c) {
        AppContext parent = null;

        if (c != null) {

            for (int i = c.length - 1; i >= 0; i--) {

                try {
                    final AppContext n = createAppContext(cl, c[i], context, parent);
                    if (n != null) {
                        parent = n;
                    }
                } catch (final IllegalArgumentException e) {
                    LOG.warn("Couldn't create the app context", e);
                } catch (final InstantiationException e) {
                	LOG.warn("Couldn't create the app context", e);
                } catch (final IllegalAccessException e) {
                	LOG.warn("Couldn't create the app context", e);
                } catch (final InvocationTargetException e) {
                	LOG.warn("Couldn't create the app context", e);
                } catch (final SecurityException e) {
                	LOG.warn("Couldn't create the app context", e);
                } catch (final NoSuchMethodException e) {
                	LOG.warn("Couldn't create the app context", e);
                } catch (final ClassNotFoundException e) {
                	LOG.warn("Couldn't create the app context", e);
                }

            }

        }
        return parent;
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(WebAppContext.WEB_CONTEXT_NAME);
    }

    /**
     * @param cl the classloader to load the class.
     * @param c the class name, class needs to implement a constructor with a
     *            <tt>context</tt> and a <tt>parent</tt> {@link #ARGS}.
     * @param context the web context
     * @param parent parent AppContext, null is none
     * @return the created AppContext
     * @throws ClassNotFoundException class not found
     * @throws SecurityException security exception
     * @throws NoSuchMethodException no method exists
     * @throws InstantiationException could not instantiate
     * @throws IllegalAccessException no access to perform this operation
     * @throws InvocationTargetException exception from invocation
     */
    AppContext createAppContext(final ClassLoader cl, final String c, final ServletContext context,
            final AppContext parent) throws ClassNotFoundException, SecurityException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        @SuppressWarnings("unchecked")
        final Class<AppContext> cls = (Class<AppContext>) cl.loadClass(c);
        final Constructor<AppContext> ctr = cls.getConstructor(ARGS);
        AppContext n;
        n = ctr.newInstance(context, parent);
        if (n != null) {
            LOG.info("Creating AppContext from class " + c);
            n.init();
        }
        return n;

    }

}
