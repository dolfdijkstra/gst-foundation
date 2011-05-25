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

import java.lang.reflect.Method;

import COM.FutureTense.Interfaces.ICS;

/**
 * This class resolves a specific method that handles a specific request as a
 * controller. The method needs to be annotated with the IcsVariable annotation
 * and need to accept one argument of type ICS.
 * <p/>
 * The name of the method can be freely chosen. The method needs to have public
 * visibility.
 * 
 * @author Dolf Dijkstra
 * @since Mar 21, 2011
 */
public class ControllerMappingResolver {

    /**
     * @param ics
     * @param o object with method annations of type IcsVariable
     * @return the method with the IcsVariable annotation that has a match for
     *         the name value pair as set in the annotation.
     */
    public Method findControllerMethod(final ICS ics, final Object o) {
        for (final Method m : o.getClass().getMethods()) {
            final IcsVariable p = m.getAnnotation(IcsVariable.class);
            if (p != null) {
                for (final String param : p.var()) {
                    final String[] split = param.split("=");
                    if (split[1].equals(ics.GetVar(split[0]))) {

                        if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(ICS.class)) {
                            return m;
                        }
                        throw new UnsupportedOperationException("Method " + m.getName()
                                + " does not have a single argument of type ICS though the method is annotated "
                                + "with a IcsVariable annation.");

                    }
                }
            }

        }
        return null;
    }
}
