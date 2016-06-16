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

import COM.FutureTense.Interfaces.ICS;

/**
 * AbstractActionController that is using the AppContext to configure the
 * ActionLocator and ActionNameResolver.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Mar 15, 2011
 */
public class ActionFrameworkController extends AbstractActionController {
    public ActionFrameworkController(ICS ics) {
        super(ics);

    }

    protected final ActionLocator getActionLocator() {

        // get the servlet context
        final ServletContext servletContext = getServletContext();
        ActionLocator l = ActionLocatorUtils.getActionLocator(servletContext);
        return l;
    }

    @Override
    protected ActionNameResolver getActionNameResolver() {
        final ServletContext servletContext = getServletContext();
        ActionNameResolver l = ActionNameResolverUtils.getActionNameResolver(servletContext);
        return l;
    }

}
