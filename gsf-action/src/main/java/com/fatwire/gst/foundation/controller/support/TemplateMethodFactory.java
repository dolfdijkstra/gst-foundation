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
package com.fatwire.gst.foundation.controller.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Dolf Dijkstra
 * 
 */
public class TemplateMethodFactory {
    private static final Class<?>[] NO_PARAMS = new Class[0];
    private static final Object[] NO_ARGS = new Object[0];

    @SuppressWarnings("unchecked")
    static <T> T createByMethod(Object template, Class<T> c) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method m;
        m = template.getClass().getMethod("create" + c.getSimpleName(), NO_PARAMS);
        if (m != null) {
            Object o = m.invoke(template, NO_ARGS);
            if (o != null && c.isAssignableFrom(o.getClass())) {
                return (T) o;

            }
        }
        return null;
    }

    static <T> T createByConstructor(Class<T> c) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException   {
        if(c.isInterface()) return null; //TODO medium test for abstract class
        final Constructor<T> constr = c.getConstructor(NO_PARAMS);
        return constr.newInstance();
    }

}