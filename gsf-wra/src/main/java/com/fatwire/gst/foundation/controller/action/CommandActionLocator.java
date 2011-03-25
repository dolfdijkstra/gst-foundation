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
package com.fatwire.gst.foundation.controller.action;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.Action;
import com.fatwire.gst.foundation.controller.ActionLocator;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Spring configuration-based action mapping.
 * The ICS variable "cmd" is mapped to a class name in the configuration
 * to a class.
 *
 * @author Tony Field
 * @since 2011-03-15
 */
public final class CommandActionLocator implements ActionLocator {
    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.controller.mapping");
    private static final String CMD_VAR = "cmd";
    private Map<String, Action> commandActionMap = new HashMap<String, Action>();
    private Action defaultAction = new RenderPage();

    public Action getAction(ICS ics) {
        final String cmd = ics.GetVar(CMD_VAR);
        final Action action;
        if (StringUtils.isBlank(cmd)) {
            LOG.trace("No command specified. Returning default action: " + defaultAction.getClass().getName());
            action = defaultAction;
        } else {
            action = commandActionMap.get(cmd);
            if (action == null) {
                throw new CSRuntimeException("No action configured for cmd: " + cmd, ftErrors.badparams);
            }
            if (LOG.isTraceEnabled()) LOG.trace("Command '" + cmd + "' maps to action " + action.getClass().getName());
        }

        // inject the required data into the action
        injectIntoAction(ics, action);

        return action;
    }

    public void setActionMap(Map<String, Action> map) {
        LOG.debug("Configured action mapping with " + (map == null ? 0 : map.size() + " entries."));
        this.commandActionMap = map;
    }

    public void setDefaultAction(Action action) {
        LOG.info("Setting default action mapping to " + action.getClass().getName());
        defaultAction = action;
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

    /**
     * Provides the data to be injected into the action
     *
     * @param ics       ICS context
     * @param fieldName the name of the field to be set
     * @param fieldType the type of the field used by the action
     * @return object to inject or null if it is not known how to do it
     */
    protected Object getValueForInjection(ICS ics, String fieldName, Class<?> fieldType) {
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
