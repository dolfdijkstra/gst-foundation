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

import COM.FutureTense.Interfaces.ICS;

/**
 * Interface to be implemented by objects that define a mapping between requests
 * and handler objects.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2011-03-15
 * 
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public interface ActionLocator {

    /**
     * Gets the action for the name specified.
     * 
     * @param ics Content Server context object
     * @param name the name of the action
     * @return the located Action, never null
     */
    Action getAction(ICS ics, String name);

}
