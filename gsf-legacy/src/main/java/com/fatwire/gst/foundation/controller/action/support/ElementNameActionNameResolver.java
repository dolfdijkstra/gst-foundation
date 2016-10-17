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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.ActionNameResolver;

/**
 * ActionNameResolver that looks up name of action based on current elementname.
 * 
 * @author Dolf.Dijkstra
 * @since May 26, 2011
 * 
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public class ElementNameActionNameResolver implements ActionNameResolver {

    /*
     * (non-Javadoc)
     * 
     * @see "com.fatwire.gst.foundation.controller.action.ActionNameResolver#"
     * resolveActionName(COM.FutureTense.Interfaces.ICS)
     */
    public String resolveActionName(ICS ics) {

        final String name = ics.ResolveVariables("CS.elementname");
        return name.startsWith("/") ? name.substring(1) : name;
    }

}
