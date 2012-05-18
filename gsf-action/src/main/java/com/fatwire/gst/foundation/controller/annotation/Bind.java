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
package com.fatwire.gst.foundation.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Annotation to bind variables to the Action.
 * <p>
 * 
 * <pre>
 * {@code
 * class MyAction implements Action {
 * 
 *     {@literal @}Bind String rendermode;
 *     {@literal @}Bind("myVar") String theVariable;
 *     {@literal @}Bind(scope="session") ShoppingCart cart;
 * 
 *     public void handleRequest(ICS ics){
 *         if("live".equals(rendermode){
 *           // do something when rendermode=live
 *         }
 *     }
 * 
 * }
 * }
 * </pre>
 * @author Dolf.Dijkstra
 * @since 12 mei 2012
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Bind {
    public enum Scope {
        ics, request, session
    }

    /**
     * @return the key name of the variable to bind
     */
    String value() default "";

    /**
     * @return the match argument from render:lookup tag
     */
    Scope scope() default Scope.ics;
}
