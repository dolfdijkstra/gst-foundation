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
package com.fatwire.gst.foundation.groovy.context;

import groovy.lang.GroovyClassLoader;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.junit.Test;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.test.MockICS;

/**
 * @author dolf
 * 
 */
public class GroovyFactoryTest {

    /**
     * Test method for
     * {@link com.fatwire.gst.foundation.controller.action.support.BaseFactory#getObject(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public void testGetObject() {

        MockICS ics = new MockICS();
        GroovyClassLoader gcl = new GroovyClassLoader();

        gcl.addClasspath("./src/test/groovy");

        GroovyFactory factory = new GroovyFactory(ics, gcl);
        List<?> list = factory.getObject("foo", List.class);
        org.junit.Assert.assertNotNull(list);

    }

    @Test
    public void testGetObject_bad_source() {

        MockICS ics = new MockICS();
        GroovyClassLoader gcl = new GroovyClassLoader();

        gcl.addClasspath("./src/test/bad-groovy");
        try {

            GroovyFactory factory = new GroovyFactory(ics, gcl);
            factory.getObject("foo", List.class);
        } catch (MultipleCompilationErrorsException e) {

            return;
        }
        org.junit.Assert.fail("should not have reached beyond the exception");

    }

    @Test
    public void testGetObject_no_method() {

        MockICS ics = new MockICS();
        GroovyClassLoader gcl = new GroovyClassLoader();

        gcl.addClasspath("./src/test/groovy");

        GroovyFactory factory = new GroovyFactory(ics, gcl);
        Collection<?> list = factory.getObject("foo", Collection.class);
        org.junit.Assert.assertNull(list);

    }

    @Test
    public void testGetObject_from_root() {

        MockICS ics = new MockICS();
        Factory root = new Factory() {

            @SuppressWarnings("unchecked")
            @Override
            public <T> T getObject(String name, Class<T> type) {
                return (T) ((Collection.class.isAssignableFrom(type)) ? Arrays.asList("tomato", "salad") : null);

            }

        };

        GroovyClassLoader gcl = new GroovyClassLoader();

        gcl.addClasspath("./src/test/groovy");

        GroovyFactory factory = new GroovyFactory(ics, gcl, root);
        Collection<?> list = factory.getObject("foobar", Collection.class);
        org.junit.Assert.assertNotNull(list);
        org.junit.Assert.assertEquals(2, list.size());

    }
}
