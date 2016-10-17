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
package com.fatwire.gst.foundation.include;

import COM.FutureTense.Interfaces.ICS;

/**
 * Interface method for including a text block into an Content Server element.
 * This facilitates hiding of the complexities of including a piece of text from
 * the View layer and move this into the business logic layer.
 * <p>
 * Contract is that Include implementation is created in business logic layer
 * and that it is included in the View layer. The View layer is expected to call
 * the include(ICS) method.
 * <p>
 * This interface is designed after the Command pattern.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * 
 * @deprecated as of release 12.x, replace with OOTB features in WCS 12c (e.g. Controllers, callelement tag, calltemplate tag, ics.RunTag and the like).
 * 
 */
public interface Include {

    /**
     * @param ics the Content Server context.
     */
    void include(ICS ics);

}
