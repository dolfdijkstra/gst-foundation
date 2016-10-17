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

import com.fatwire.gst.foundation.controller.support.WebContextUtil;

/**
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 * 
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public final class ActionLocatorUtils {
    public static final String ACTION_LOCATOR_BEAN = "gsfActionLocator";

    /**
     * 
     */
    private ActionLocatorUtils() {
    }

    /**
     * Returns the ActionLocator as configured on the WebAppContext bean by the
     * name of gsfActionLocator.
     * 
     * @param servletContext the servlet context.
     * @return the ActionLocator that is configured via the servletContext.
     */
    public static ActionLocator getActionLocator(final ServletContext servletContext) {
        return getActionLocator(servletContext, ACTION_LOCATOR_BEAN);
    }

    /**
     * Returns the ActionLocator as configured on the WebAppContext bean by the
     * name 'beanName'.
     * @param beanName bean name
     * @param servletContext the servlet context.
     * @return the ActionLocator that is configured via the servletContext.
     */

    public static ActionLocator getActionLocator(final ServletContext servletContext, String beanName) {
        return WebContextUtil.getWebAppContext(servletContext).getBean(beanName, ActionLocator.class);
    }

}
