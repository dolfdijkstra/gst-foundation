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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;


public class AnnotationInjectorTest  {

    class Sample {
        @InjectForRequest("me")
        String foo;
        @InjectForRequest
        String foot;

    }

    @Test
    public void testInject() {
        final Factory f = new Factory() {

            @SuppressWarnings("unchecked")
            public <T> T getObject(final String name, final Class<T> type) {
                return (T) name;
            }

        };
        final Sample object = new Sample();
        AnnotationInjector.inject(object, f);
        assertEquals("me", object.foo);
        assertEquals("foot", object.foot);

    }

}
