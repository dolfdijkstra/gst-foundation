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

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.gst.foundation.controller.AppContext;

/**
 * @author Dolf.Dijkstra
 * @since 4 April 2012
 */
public class WebContextUtil {

    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.foundation.controller.support.WebContextUtil");
    private WebContextUtil() {
        super();
    }

    public static AppContext getWebAppContext(ServletContext ctx) {
        Object o = ctx.getAttribute(WebAppContext.WEB_CONTEXT_NAME);
        if (o == null) {
            LOG.trace("Configuring WebAppContext from WebContextUtil; it is not explicitly configured in web.xml. Using default setup!");
            return new WebAppContextLoader().configureWebAppContext(ctx);
        }
        if (o instanceof AppContext) {
            return (AppContext) o;
        }

        throw new IncompleteConfigurationException("There was no " + WebAppContext.WEB_CONTEXT_NAME
                + " object in the ServletContext found. Is the WebAppContextLoader listener configured in web.xml?");
    }

}
