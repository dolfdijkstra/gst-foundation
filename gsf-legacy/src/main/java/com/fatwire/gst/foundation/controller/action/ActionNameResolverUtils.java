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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fatwire.gst.foundation.controller.action.support.NullActionNameResolver;
import com.fatwire.gst.foundation.controller.support.WebContextUtil;

/**
 * @author Dolf Dijkstra
 * @since May 27, 2011
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public final class ActionNameResolverUtils {
	protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.controller.action.ActionNameResolverUtils");

    public static final String ACTION_NAME_RESOLVER_BEAN = "gsfActionNameResolver";

    private static final ActionNameResolver nullActionNameResolver = new NullActionNameResolver();

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

    private static ActionNameResolver getActionNameResolver(ServletContext servletContext, String actionNameResolverBean) {
        return WebContextUtil.getWebAppContext(servletContext)
                .getBean(actionNameResolverBean, ActionNameResolver.class);
    }

    /**
     * Returns the ActionNameResolver as configured by spring framework on the
     * WebApplicationContext bean by the name 'beanName'.
     * @param beanName bean name
     * @param servletContext the servlet context.
     * @return the ActionNameResolver that is configured via the servletContext.
     */

    public static ActionNameResolver getActionNameResolverX(final ServletContext servletContext, String beanName) {

        // get the spring web application context
        final WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // get the bean.

        ActionNameResolver resolver;
        try {
            resolver = (ActionNameResolver) wac.getBean(beanName, ActionNameResolver.class);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using ActionNameResolver as configured: " + resolver.getClass().getName());
            }
        } catch (NoSuchBeanDefinitionException e) {
            return nullActionNameResolver;
        }

        return resolver;
    }

}
