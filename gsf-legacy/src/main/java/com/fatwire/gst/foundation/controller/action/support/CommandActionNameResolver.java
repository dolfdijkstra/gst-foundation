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

import org.apache.commons.lang3.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.ActionNameResolver;

/**
 * ActionNameResolver that looks up variable name value from ICS scope. Default
 * variable name is 'cmd'.
 * 
 * @author Dolf.Dijkstra
 * @since May 26, 2011
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public class CommandActionNameResolver implements ActionNameResolver {
    private static final String CMD_VAR = "cmd";

    private String varName;

    public CommandActionNameResolver() {
        varName = "cmd";
    }

    public CommandActionNameResolver(String name) {
        this.varName = name;
    }

    public final String getVarName() {
        return StringUtils.isNotBlank(varName) ? varName : CMD_VAR;
    }

    public String resolveActionName(ICS ics) {
        return ics.GetVar(getVarName());
    }

    /**
     * @param varName the varName to set
     */
    public final void setVarName(String varName) {
        this.varName = varName;
    }
}
