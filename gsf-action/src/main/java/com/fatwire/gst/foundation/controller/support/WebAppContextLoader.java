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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.support.DefaultWebAppContext;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

import org.apache.commons.logging.Log;

/**
 * ServletContextListener that loads and configures the AppContext for this
 * application.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class WebAppContextLoader implements ServletContextListener {
    private static final String GROOVY_WEB_CONTEXT = "com.fatwire.gst.foundation.groovy.context.GroovyWebContext";
    private static final String GROOVY_CLASSNAME = "groovy.util.GroovyScriptEngine";
    public static final String CONTEXTS = "gsf-contexts";

    protected static final Log LOG = LogUtil.getLog(WebAppContextLoader.class);
    boolean booted = false;
    private static final Class<?>[] ARGS = new Class[] { ServletContext.class, AppContext.class };

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext context = sce.getServletContext();
        if (context.getMajorVersion() == 2 && context.getMinorVersion() < 4) {
            throw new IllegalStateException(
                    "Servlet Container is configured for version 2.3 or less. This ServletContextListener does not support 2.3 and earlier as the load order of Listeners is not guaranteed.");
        }
        AppContext parent = null;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

        parent = configureFromInitParam(context, cl);
        if (parent == null) {
            // if gsf-groovy is found and groovy classes around found, boot with
            // groovy
            final String groovyPath = context.getRealPath("/WEB-INF/gsf-groovy");

            if (new File(groovyPath).isDirectory() && isGroovyOnClassPath(cl)) {
                try {
                    parent = createAppContext(cl, GROOVY_WEB_CONTEXT, context, null);
                } catch (final Exception e) {
                    LOG.warn("Exception when creating the GroovyWebContext as a default option", e);
                }
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

    }

    /**
     * Creates a AppContext based on the init parameter {@link #CONTEXTS}.
     * 
     * @param context
     * @param cl
     * @return the AppContext as configured from the web app init parameter.
     */
    protected AppContext configureFromInitParam(final ServletContext context, final ClassLoader cl) {
        AppContext parent = null;
        final String init = context.getInitParameter(CONTEXTS);
        if (init != null) {
            final String[] c = init.split(",");

            for (int i = c.length - 1; i >= 0; i--) {

                try {
                    final AppContext n = createAppContext(cl, c[i], context, parent);
                    if (n != null) {
                        parent = n;
                    }
                } catch (final IllegalArgumentException e) {
                    LOG.warn(e);
                } catch (final InstantiationException e) {
                    LOG.warn(e);
                } catch (final IllegalAccessException e) {
                    LOG.warn(e);
                } catch (final InvocationTargetException e) {
                    LOG.warn(e);
                } catch (final SecurityException e) {
                    LOG.warn(e);
                } catch (final NoSuchMethodException e) {
                    LOG.warn(e);
                } catch (final ClassNotFoundException e) {
                    LOG.warn(e);
                }

            }

        }
        return parent;
    }

    private boolean isGroovyOnClassPath(final ClassLoader cl) {
        try {
            cl.loadClass(GROOVY_CLASSNAME);
        } catch (final ClassNotFoundException e) {
            return false;
        }
        return true;
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
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
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
            LOG.info("Creating application context by " + c);
            n.init();
        }
        return n;

    }

}
