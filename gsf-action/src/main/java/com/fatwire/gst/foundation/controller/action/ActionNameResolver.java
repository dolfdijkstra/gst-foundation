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
 * Resolve the name of the Action based on the current request, exposed via the
 * Content Server context.
 * 
 * @author Dolf.Dijkstra
 * @since May 27, 2011
 */
public interface ActionNameResolver {

    /**
     * Resolves the Action name based on the current Content Server context.
     * </p> A null or empty string value is an indication that a default Action
     * need to be used.
     * 
     * @param ics the Content Server context
     * @return the name of the action, blank or null is allowed.
     */
    String resolveActionName(ICS ics);

}
