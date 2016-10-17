/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.groovy;

import COM.FutureTense.Interfaces.ICS;

/**
 * Interface to load Groovy scripts and classes
 * 
 * @author Dolf Dijkstra
 * 
 * @deprecated as of release 12.x, replace with WCS 12c's native Groovy support
 * 
 */
public interface GroovyLoader {

    /**
     * @param name the name of the class
     * @param ics content server context
     * @return the Object loaded by Groovy
     * @throws Exception exception from load attempt
     */
    Object load(ICS ics,String name) throws Exception;

}
