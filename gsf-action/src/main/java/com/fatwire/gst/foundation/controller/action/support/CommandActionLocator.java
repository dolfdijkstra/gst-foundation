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

package com.fatwire.gst.foundation.controller.action.support;

import java.util.HashMap;
import java.util.Map;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.RenderPage;

import COM.FutureTense.Interfaces.ICS;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Spring configuration-based action mapping. The ICS variable "cmd" is mapped
 * to a class name in the configuration to a class.
 * <p/>
 * Objects are created via a {@link Factory}, that can be configured via the
 * <tt>factoryClassname</tt>. That class needs to have a constructor accepting
 * ICS.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2011-03-15
 */
public final class CommandActionLocator extends BaseActionLocator {
    protected static final Log LOG = LogFactory.getLog(CommandActionLocator.class.getPackage().getName());
    private static final String CMD_VAR = "cmd";
    private Map<String, Action> commandActionMap = new HashMap<String, Action>();

    protected String getVarName() {
        return CMD_VAR;
    }

    public Action getAction(ICS ics) {
        return getAction(ics, ics.GetVar(getVarName()));
    }

    public Action getAction(ICS ics, String name) {

        final Action action;
        if (StringUtils.isBlank(name)) {
            action = newDefaultAction();
            LOG.trace("No command specified. Returning default action: " + action.getClass().getName());
        } else {
            action = commandActionMap.get(name);
            if (action == null) {
                throw new RuntimeException("No action configured for cmd: " + name);
            }
            if (LOG.isTraceEnabled())
                LOG.trace("Command '" + name + "' maps to action " + action.getClass().getName());
        }

        // inject the required data into the action
        this.injectDependencies(ics, action);

        return action;
    }

    protected Action newDefaultAction() {
        return new RenderPage();
    }

    public void setActionMap(Map<String, Action> map) {
        LOG.debug("Configured action mapping with " + (map == null ? 0 : map.size() + " entries."));
        this.commandActionMap = map;
    }

    public void setDefaultAction(Action action) {
        LOG.info("Setting default action mapping to " + action.getClass().getName());
        // defaultAction = action;
    }

}
