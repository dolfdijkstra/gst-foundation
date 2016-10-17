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
 * Base action interface, representing a component that receives ICS like a
 * HttpServlet but is able to participate in an MVC workflow. Comparable to the
 * notion of a Struts Action or a Spring MVC Controller.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2011-03-15
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 *  
 */
public interface Action {
    /**
     * Process and handle the request. This method might be responsible for invoking
     * the view as well.
     * 
     * @param ics Content Server context.
     */
    void handleRequest(ICS ics);
}
