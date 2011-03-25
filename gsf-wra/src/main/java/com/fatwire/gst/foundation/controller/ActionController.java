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

import java.lang.reflect.Field;
import javax.servlet.ServletContext;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.controller.impl.CommandActionLocator;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Dispatching controller. Relies on actionLocator to dispatch control to
 * action classes.
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Mar 15, 2011
 */
public class ActionController extends AbstractController {

    private static final String ACTION_LOCATOR_BEAN = "gsfActionLocator";

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

        // inject the required data into the action
        injectIntoAction(ics, action);

        // execute the command
        action.handleRequest(ics);
        LOG.trace("Request handling complete");
    }

    protected ActionLocator getActionLocator() {

        // get the spring web application context
        ServletContext servletContext = ics.getIServlet().getServlet().getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // get the bean.  Note for lazy administrators, a default locator is provided
        final ActionLocator locator;
        if (wac.containsBean(ACTION_LOCATOR_BEAN)) {
            locator = (ActionLocator) wac.getBean(ACTION_LOCATOR_BEAN);
            if (LOG.isTraceEnabled())
                LOG.trace("Using actionLocatorBean as configured: " + locator.getClass().getName());
        } else {
            locator = new CommandActionLocator();
            if (LOG.isTraceEnabled()) LOG.trace("Using default actionLocatorBean " + locator.getClass().getName());
        }
        return locator;
    }

    /**
     * Inject ICS runtime objects into the action.  Objects flagged with the {@Inject} annotation
     * will be populated by this method by retrieving the value from the {#getValueForInjection} method.
     *
     * @param ics    ICS context
     * @param action action
     * @see #getValueForInjection(ICS, String, Class)
     */
    protected final void injectIntoAction(ICS ics, Action action) {
        long start = System.nanoTime();
        try {
            Class<?> c = action.getClass();
            while (c != Object.class) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(InjectForRequest.class)) {
                        Object injectionValue = getValueForInjection(ics, field.getName(), field.getType());
                        if (injectionValue == null) {
                            throw new CSRuntimeException(this.getClass().getName() + " does not know how to inject '" + field.getType().getName() + "' into the field '" + field.getName() + "' for an action.", ftErrors.badparams);
                        }
                        field.setAccessible(true); //make private fields accessible
                        if (LOG.isDebugEnabled())
                            LOG.debug("Injecting " + injectionValue.getClass().getName() + " into field " + field.getName() + " for " + action.getClass().getName());
                        try {
                            field.set(action, injectionValue);
                        } catch (IllegalArgumentException e) {
                            throw new CSRuntimeException("IllegalArgumentException injecting " + injectionValue + " into field " + field.getName(), ftErrors.exceptionerr, e);
                        } catch (IllegalAccessException e) {
                            throw new CSRuntimeException("IllegalAccessException injecting " + injectionValue + " into field " + field.getName(), ftErrors.exceptionerr, e);
                        }
                    }
                    c = c.getSuperclass();
                }
            }
        } finally {
            DebugHelper.printTime("inject model for " + action.getClass().getName(), start);
        }
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
     * Provides the data to be injected into the action
     *
     * @param ics       ICS context
     * @param fieldName the name of the field to be set
     * @param fieldType the type of the field used by the action
     * @return object to inject or null if it is not known how to do it
     */
    protected Object getValueForInjection(ICS ics, String fieldName, Class fieldType) {
        if (ICS.class.isAssignableFrom(fieldType)) {
            return ics;
        }
        if (WraCoreFieldDao.class.isAssignableFrom(fieldType)) {
            return WraCoreFieldDao.getInstance(ics);
        }
        if (AliasCoreFieldDao.class.isAssignableFrom(fieldType)) {
            WraCoreFieldDao wraCoreFieldDao = WraCoreFieldDao.getInstance(ics);
            return new AliasCoreFieldDao(ics, wraCoreFieldDao);
        }
        if (WraPathTranslationService.class.isAssignableFrom(fieldType)) {
            return WraPathTranslationServiceFactory.getService(ics);
        }
        return null;
    }

}
