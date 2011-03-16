/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller;

import javax.servlet.ServletContext;

import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.impl.SpringControllerMapping;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Dispatching controller. Relies on handlerMapping to dispatch control to
 * action classes.
 *
 * @author Tony Field
 * @since Mar 15, 2011
 */
public class Dispatcher extends AbstractController {

    private static final String CONTROLLER_MAPPING_BEAN = "gsfControllerMapping";

    protected void doExecute() {

        // record seid and eid and any other deps required
        recordCompositionalDependencies();

        // find the controller mapping
        LOG.trace("Dispatcher looking for controller mapping");
        ControllerMapping cm = getControllerMapping();
        if (LOG.isTraceEnabled()) LOG.trace("Using controller mapping: " + cm.getClass().getName());

        // get the controller
        Controller controller = cm.getController(ics);
        if (LOG.isTraceEnabled()) LOG.trace("Using controller: " + controller.getClass().getName());

        // execute the command
        controller.handleRequest(ics);
        LOG.trace("Request handling complete");
    }

    protected ControllerMapping getControllerMapping() {

        // get the spring web application context
        ServletContext servletContext = ics.getIServlet().getServlet().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // get the bean.  Note for lazy administrators, a default mapping is provided
        final ControllerMapping cm;
        if (wac.containsBean(CONTROLLER_MAPPING_BEAN)) {
            cm = (ControllerMapping) wac.getBean(CONTROLLER_MAPPING_BEAN);
            if (LOG.isTraceEnabled())
                LOG.trace("Using controllerMappingBean as configured: " + cm.getClass().getName());
        } else {
            cm = new SpringControllerMapping();
            if (LOG.isTraceEnabled()) LOG.trace("Using default controllerMappingBean " + cm.getClass().getName());
        }
        return cm;
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

    /**
     * Record compositional dependencies that are required for the controller
     */
    protected void recordCompositionalDependencies() {
        if (ics.isCacheable(ics.GetVar(ftMessage.PageName))) {
            if (goodString(ics.GetVar("seid"))) {
                LogDep.logDep(ics, "SiteEntry", ics.GetVar("seid"));
            }
            if (goodString(ics.GetVar("eid"))) {
                LogDep.logDep(ics, "CSElement", ics.GetVar("eid"));
            }
        }
    }
}
