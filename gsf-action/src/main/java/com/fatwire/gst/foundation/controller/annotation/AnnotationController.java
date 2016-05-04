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

package com.fatwire.gst.foundation.controller.annotation;

import static COM.FutureTense.Interfaces.Utilities.goodString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AbstractController;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;

/**
 * The Controller delegates the business logic to an annotated method. In this
 * method the special logic for the request is executed. Method selection is
 * done via the {@literal @}IcsVariable annotation. This works of the premise that a special
 * name/value pair on the ics scope set before this controller is executed, is
 * enough to determine the selection for the business logic method.
 * 
 * @author Dolf Dijkstra
 * @since Apr 15, 2011
 * @see IcsVariable
 */
public abstract class AnnotationController extends AbstractController {

    public AnnotationController(ICS ics) {
        super(ics);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.controller.AbstractController#doExecute()
     */
    @Override
    protected void doExecute() {

        // record seid and eid and any other deps required
        RenderUtils.recordBaseCompositionalDependencies(ics);
        final ControllerMappingResolver resolver = new ControllerMappingResolver();
        final Object target = getTarget();
        final Method m = resolver.findControllerMethod(ics, target);
        if (m == null) {
            executeDefault();
        } else {
            invokeControllerMethod(m, target);
        }
    }

    /**
     * @param m method to invoke
     * @param target target object
     */
    protected void invokeControllerMethod(final Method m, final Object target) {
        try {
            m.invoke(target, new Object[] { ics });
        } catch (final IllegalArgumentException e) {
            handleException(e);
        } catch (final IllegalAccessException e) {
            handleException(e);
        } catch (final InvocationTargetException e) {
            handleException(e);
        }

    }

    /**
     * @return this
     */
    protected Object getTarget() {
        return this;
    }

    /**
     * Default operation to execute if no matching annotated method can be
     * found.
     */
    abstract void executeDefault();

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
     * <p>
     * Only some errnos are handled by this base class.
     * <p>
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
