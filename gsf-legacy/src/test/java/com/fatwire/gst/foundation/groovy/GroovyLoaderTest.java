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

package com.fatwire.gst.foundation.groovy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;

public class GroovyLoaderTest extends TestCase {

    public void testLoad() {
        DiskGroovyLoader loader = new DiskGroovyLoader();
        loader.bootEngine("./src/test/groovy");
        loader.precompile();
        Object a;
        try {
            a = loader.load(null,"test/MyAction");
            if (a instanceof Action) {
                Action action = (Action) a;
                action.handleRequest(null);
                for (Field field : action.getClass().getFields()) {
                    InjectForRequest anno = field.getAnnotation(InjectForRequest.class);
                    if ("foo".equals(field.getName()))
                        assertNotNull(anno);
                }
                for (Method method : action.getClass().getMethods()) {
                    if ("setSomething".equalsIgnoreCase(method.getName())) {
                        InjectForRequest anno = method.getAnnotation(InjectForRequest.class);
                        assertNotNull(anno);
                    }
                }
            } else {
                fail("not an Action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testJavaClass() {
        //
        DiskGroovyLoader loader = new DiskGroovyLoader();
        loader.bootEngine("./src/test/groovy");
        Object a;
        try {
            a = loader.load(null,"com.fatwire.gst.foundation.groovy.action.GroovyActionLocator");
            assertNotNull(a);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}