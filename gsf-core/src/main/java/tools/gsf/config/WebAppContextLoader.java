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
package tools.gsf.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
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
 * ServletContextListener that loads and configures the AppContext for this
 * application.
 *
 * It is configured to auto-load into the servlet context by way of the
 * {@code @WebListener} annotation.  Overriding of this class is allowed.
 * 
 * @author Dolf Dijkstra
 * @author Tony Field
 */
@WebListener
public class WebAppContextLoader implements ServletContextListener {

    /**
     * Name of the servlet context init parameter containing the app context to be booted
     */
    public static final String CONTEXTS = "gsf-contexts";

    /**
     * Name of config file where context classes can be configured.
     */
    public static final String CONFIG_FILE = "META-INF/gsf-contexts";

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.config.WebAppContextLoader");
    private static final Class<?>[] ARGS = new Class[] { ServletContext.class, AppContext.class };

    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext context = sce.getServletContext();
        if (context.getMajorVersion() == 3 && context.getMinorVersion() < 0) {
            throw new IllegalStateException("Servlet Container is configured for version less than 3.0. This ServletContextListener does not support 2.x and earlier as the load order of Listeners is not guaranteed.");
        }
        configureWebAppContext(context);
    }

    private AppContext configureWebAppContext(final ServletContext context) {

        AppContext parent;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

        // start with init param
        parent = configureFromInitParam(context, cl);
        if (parent != null) {
            LOG.info("AppContext initialized from init param. Class: {}", parent.getClass().getName());
        }

        // try locator
        if (parent == null) {
            parent = configureFromServiceLocator(context, cl);
            if (parent != null) {
                LOG.info("AppContext initialized from service locator. Class: {}", parent.getClass().getName());
            }
        }

        // use default
        if (parent == null) {
            parent = new DefaultWebAppContext(context);
            parent.init();
            LOG.info("AppContext initialized using default class: {}", parent.getClass().getName());
        }

        // register it
        context.setAttribute(WebAppContext.WEB_CONTEXT_NAME, parent);

        return parent;
    }

    private AppContext configureFromServiceLocator(ServletContext context, ClassLoader cl) {

        List<String> init = new LinkedList<>();
        Enumeration<URL> configs;
        try {
            configs = cl.getResources(CONFIG_FILE);
        } catch (IOException e) {
            LOG.warn("Exception when loading the service descriptor for the AppContext from the classpath.", e);
            return null;
        }

        if (configs.hasMoreElements()) {
            URL u = configs.nextElement();
            try (InputStream in = u.openStream();
                 BufferedReader r = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
                String s;
                while ((s = r.readLine()) != null) {
                    if (StringUtils.isNotBlank(s) && !StringUtils.startsWith(s, "#")) {
                        init.add(s);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading configuration file", e);
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
    private AppContext configureFromInitParam(final ServletContext context, final ClassLoader cl) {
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
                } catch (IllegalArgumentException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | SecurityException
                        | NoSuchMethodException
                        | ClassNotFoundException e) {
                    LOG.warn("Couldn't create the app context", e);
                }
            }
        }
        return parent;
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
    private AppContext createAppContext(final ClassLoader cl, final String c,
                                        final ServletContext context, final AppContext parent)
            throws ClassNotFoundException, SecurityException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        @SuppressWarnings("unchecked")
        final Class<AppContext> cls = (Class<AppContext>) cl.loadClass(c);
        final Constructor<AppContext> ctr = cls.getConstructor(ARGS);
        AppContext n;
        n = ctr.newInstance(context, parent);
        n.init();
        return n;
    }

    public void contextDestroyed(final ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(WebAppContext.WEB_CONTEXT_NAME);
        LOG.info("AppContext un-registered from servlet context.");
    }
}
