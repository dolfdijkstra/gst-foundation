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

import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AbstractController;
import com.fatwire.gst.foundation.facade.RenderUtils;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Dispatching controller. Relies on actionLocator to dispatch control to
 * Action classes.
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Mar 15, 2011
 */
public class ActionController extends AbstractController {

    

    protected void doExecute() {

        // record seid and eid
        RenderUtils.recordBaseCompositionalDependencies(ics);

        // find the action locator
        LOG.trace("Dispatcher looking for action locator");
        ActionLocator locator = getActionLocator();
        if (LOG.isTraceEnabled()) LOG.trace("Using action locator: " + locator.getClass().getName());

        // get the action
        Action action = locator.getAction(ics);
        if (LOG.isTraceEnabled()) LOG.trace("Using action: " + action.getClass().getName());

        // execute the command
        action.handleRequest(ics);
        LOG.trace("Request handling complete");
    }

    protected ActionLocator getActionLocator() {

        // get the servlet context
        ServletContext servletContext = getServletContext();
        return ActionLocatorUtils.getActionLocator(servletContext);
    }

    
    @SuppressWarnings("deprecation")
    private ServletContext getServletContext() {
        return ics.getIServlet().getServlet().getServletContext();
    }

    @Override
    protected void handleException(final Exception e) {
        if (e instanceof CSRuntimeException) {
            handleCSRuntimeException((CSRuntimeException) e);
        } else {
            sendError(500, e);
        }
    }

    /**
     * This method transforms errno values into http status codes and sets them
     * using the X-Fatwire-Status header.
     * <p/>
     * Only some errnos are handled by this base class.
     * <p/>
     * More info coming soon
     *
     * @param e exception
     */
    protected void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
            case 400:
            case ftErrors.badparams:
                sendError(400, e);
                break;
            case 404:
            case ftErrors.pagenotfound:
                sendError(404, e);
                break;
            case 403:
            case ftErrors.noprivs:
                sendError(403, e);
                break;
            default:
                sendError(500, e);
                break;
        }
    }
}
