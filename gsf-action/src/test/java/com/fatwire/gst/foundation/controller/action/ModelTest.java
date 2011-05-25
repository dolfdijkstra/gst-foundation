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

import java.util.Collection;

import junit.framework.TestCase;

public class ModelTest extends TestCase {

    public void testAddStringObject() {
        final Model m = new Model();
        m.add("foo", "bar");
        assertEquals(1, m.entries().size());
        assertEquals("bar", m.entries().iterator().next().getValue());
    }

    public void testAddStringObjectArray() {
        final Model m = new Model();
        m.add("foo", "bar", "bad");
        assertEquals(1, m.entries().size());
        final Object o = m.entries().iterator().next().getValue();
        assertTrue(o instanceof Collection);

    }

    @SuppressWarnings("rawtypes")
    public void testList() {
        final Model m = new Model();
        m.list("foo", "bad");
        m.list("foo", "bad");
        assertEquals(1, m.entries().size());
        final Object o = m.entries().iterator().next().getValue();
        assertTrue(o instanceof Collection);
        assertEquals(2, ((Collection) o).size());

    }

}
