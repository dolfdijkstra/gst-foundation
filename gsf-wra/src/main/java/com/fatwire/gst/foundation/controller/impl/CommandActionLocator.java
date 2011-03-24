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
package com.fatwire.gst.foundation.controller.impl;

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.Action;
import com.fatwire.gst.foundation.controller.ActionLocator;

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

    public Action getAction(ICS ics) { // todo: high: support icsaware?
        final String cmd = ics.GetVar(CMD_VAR);
        if (StringUtils.isBlank(cmd)) {
            LOG.trace("No command specified. Returning default action: " + defaultAction.getClass().getName());
            return defaultAction;
        }

        Action c = commandActionMap.get(cmd);
        if (c == null) {
            throw new CSRuntimeException("No action configured for cmd: " + cmd, ftErrors.badparams);
        }
        if (LOG.isTraceEnabled()) LOG.trace("Command '" + cmd + "' maps to action " + c.getClass().getName());
        return c;
    }

    public void setActionMap(Map<String, Action> map) {
        LOG.debug("Configured action mapping with " + (map == null ? 0 : map.size() + " entries."));
        this.commandActionMap = map;
    }

    public void setDefaultAction(Action action) {
        LOG.info("Setting default action mapping to " + action.getClass().getName());
        defaultAction = action;
    }
}
