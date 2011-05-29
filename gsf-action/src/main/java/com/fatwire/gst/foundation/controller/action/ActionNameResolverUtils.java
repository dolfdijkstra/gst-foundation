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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Dolf Dijkstra
 * @since May 27, 2011
 */
public final class ActionNameResolverUtils {
    private static final Log LOG = LogFactory.getLog(ActionNameResolverUtils.class.getPackage().getName());
    public static final String ACTION_NAME_RESOLVER_BEAN = "gsfActionNameResolver";

    /**
     * 
     */
    private ActionNameResolverUtils() {
    }

    /**
     * Returns the ActionNameResolver as configured by spring framework on the
     * WebApplicationContext bean by the name of gsfActionLocator.
     * 
     * @param servletContext the servlet context.
     * @return the ActionNameResolver that is configured via the servletContext.
     */
    public static ActionNameResolver getActionNameResolver(final ServletContext servletContext) {
        return getActionNameResolver(servletContext, ACTION_NAME_RESOLVER_BEAN);
    }

    /**
     * Returns the ActionNameResolver as configured by spring framework on the
     * WebApplicationContext bean by the name 'beanName'.
     * 
     * @param servletContext the servlet context.
     * @return the ActionNameResolver that is configured via the servletContext.
     */

    public static ActionNameResolver getActionNameResolver(final ServletContext servletContext, String beanName) {

        // get the spring web application context
        final WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // get the bean.

        final ActionNameResolver resolver;
        if (wac.containsBean(beanName)) {
            resolver = (ActionNameResolver) wac.getBean(beanName);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using ActionNameResolver as configured: " + resolver.getClass().getName());
            }
        } else {
            resolver = null;
        }
        return resolver;
    }

}
