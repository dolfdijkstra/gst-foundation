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
import javax.servlet.http.HttpServletResponse;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AbstractController;
import com.fatwire.gst.foundation.facade.RenderUtils;

/**
 * Dispatching Controller. Relies on ActionLocator to dispatch control to Action
 * classes. An ActionNameResolver is used to resolve the action name on the
 * current request.
 * 
 * @author Dolf Dijkstra
 * @since May 26, 2011
 */
public abstract class AbstractActionController extends AbstractController {

    public AbstractActionController() {
        super();
    }

    @Override
    protected final void doExecute() {

        // record seid and eid
        RenderUtils.recordBaseCompositionalDependencies(ics);

        // find the action locator
        LOG.trace("Dispatcher looking for action locator");
        final ActionLocator locator = getActionLocator();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Using action locator: " + locator.getClass().getName());
        }
        String actionName = getActionNameResolver().resolveActionName(ics);
        // get the action
        final Action action = locator.getAction(ics, actionName);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Using action: " + action.getClass().getName());
        }

        // execute the command
        action.handleRequest(ics);
        LOG.trace("Request handling complete");
    }

    /**
     * @return the ActionNameResolver
     */
    protected abstract ActionNameResolver getActionNameResolver();

    /**
     * @return the ActionLocator.
     */
    protected abstract ActionLocator getActionLocator();

    @SuppressWarnings("deprecation")
    protected ServletContext getServletContext() {
        return ics.getIServlet().getServlet().getServletContext();
    }

    @Override
    protected final void handleException(final Exception e) {
        if (LOG.isTraceEnabled()) {
            // Give developer a clue in case error pages aren't configured properly. 
            LOG.trace("Action threw an exception and an error code will be returned.  Exception: "+e, e);
        }
        if (e instanceof CSRuntimeException) {
            handleCSRuntimeException((CSRuntimeException) e);
        } else {
            sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
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
    protected final void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
            case HttpServletResponse.SC_BAD_REQUEST:
            case ftErrors.badparams:
                sendError(HttpServletResponse.SC_BAD_REQUEST, e);
                break;
            case HttpServletResponse.SC_NOT_FOUND:
            case ftErrors.pagenotfound:
                sendError(HttpServletResponse.SC_NOT_FOUND, e);
                break;
            case HttpServletResponse.SC_FORBIDDEN:
            case ftErrors.noprivs:
                sendError(HttpServletResponse.SC_FORBIDDEN, e);
                break;
            default:
                sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
                break;
        }
    }

}
