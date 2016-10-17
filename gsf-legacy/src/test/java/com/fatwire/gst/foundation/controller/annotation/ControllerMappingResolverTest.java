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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.test.MockICS;

/**
 * @deprecated - WCS 12c supports its own native controller infrastructure
 */
public class ControllerMappingResolverTest extends TestCase {

    static class Foo {
        int i = 0;

        @IcsVariable(var = { "cmd=login", "cmd=logout" })
        public void doSomething(final ICS ics) {
            i++;

        }
    }

    public void testFindControllerMethod() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        final ControllerMappingResolver resolver = new ControllerMappingResolver();
        final Foo foo = new Foo();
        final ICS ics = new MockICS() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.fatwire.gst.foundation.facade.uri.MockICS#GetVar(java.lang
             * .String)
             */
            @Override
            public String GetVar(final String name) {
                if ("cmd".equals(name)) {
                    return "login";
                }
                return null;
            }

        };
        final Method m = resolver.findControllerMethod(ics, foo);
        m.invoke(foo, new Object[] { ics });
        assertEquals(1, foo.i);
    }

}
