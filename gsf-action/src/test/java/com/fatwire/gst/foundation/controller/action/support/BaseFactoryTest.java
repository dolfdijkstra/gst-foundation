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

package com.fatwire.gst.foundation.controller.action.support;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer;
import com.fatwire.gst.foundation.test.MockICS;

/**
 * @author dolf
 * 
 */
public class BaseFactoryTest {

    public static class Foo {
        public Foo(ICS ics) {

        }

    }

    public static class SBar {
        public SBar() {

        }

    }

    public static class Bar extends Foo {

        public Bar(ICS ics) {
            super(ics);
        }

    }

    public static class FooBar extends Foo {

        public FooBar(ICS ics) {
            super(ics);
        }

    }

    /**
     * Test method for
     * {@link com.fatwire.gst.foundation.controller.action.support.BaseFactory#getObject(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public void testGetObject() {
        MockICS ics = new MockICS();

        BaseFactory bf = new ListFactory(ics);
        List<?> foo = bf.getObject("foo", List.class);
        Assert.assertNotNull(foo);
    }

    @Test
    public void testGetObject_from_parent() {
        MockICS ics = new MockICS();

        BaseFactory bf = new CollectionFactory(ics, new ListFactory(ics));
        List<?> foo = bf.getObject("foo", List.class);
        Assert.assertNotNull(foo);
    }

    @Test
    public void testGetObject_not_exists() {
        MockICS ics = new MockICS();

        BaseFactory bf = new CollectionFactory(ics, new ListFactory(ics));
        String foo = bf.getObject("foo", String.class);
        Assert.assertNull(foo);
    }

    @Test
    public void testGetObject_foo() {
        MockICS ics = new MockICS();

        BaseFactory bf = new FooFactory(ics);
        Foo foo = bf.getObject("foo", Foo.class);
        Assert.assertNotNull(foo);
        Assert.assertTrue(foo instanceof Bar);
    }

    @Test
    public void testGetObject_bar_ctor() {
        MockICS ics = new MockICS();

        BaseFactory bf = new FooFactory(ics, new FooBarFactory(ics));
        Bar foo = bf.getObject("foo", Bar.class);
        Assert.assertNotNull(foo);
    }

    @Test
    public void testGetObject_foobar() {
        MockICS ics = new MockICS();

        BaseFactory bf = new FooFactory(ics, new FooBarFactory(ics));
        FooBar foo = bf.getObject("foo", FooBar.class);
        Assert.assertNotNull(foo);
    }

    @Test
    public void testGetObject_abar() {
        MockICS ics = new MockICS();

        BaseFactory bf = new CBarFactory(ics, new FooBarFactory(ics));
        FooBar foo = bf.getObject("foo", FooBar.class);
        Assert.assertNotNull(foo);
        List list = bf.getObject("list", List.class);
        Assert.assertNotNull(list);
    }

    @Test
    public void testGetObject_sbar() {
        MockICS ics = new MockICS();
        Factory root = new CBarFactory(ics);
        BaseFactory bf = new BaseFactory(ics, root) {

            @Override
            protected Class<?>[] factoryClasses(ICS ics) {
                return new Class[] { SBarFactory.class };
            }

        };
        SBar foo = bf.getObject("foobar2", SBar.class);
        Assert.assertNotNull(foo);
        List<?> list = bf.getObject("list", List.class);
        Assert.assertNotNull(list);
    }

    class ListFactory extends BaseFactory {

        public ListFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        public List<?> createList(ICS ics) {
            return Collections.emptyList();
        }

    }

    class CollectionFactory extends BaseFactory {

        public CollectionFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        public Collection<?> createCollection(ICS ics) {
            return Collections.emptyList();
        }

    }

    class FooFactory extends BaseFactory {

        public FooFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        public Foo createFoo(ICS ics) {
            return new Bar(ics);
        }
    }

    class FooBarFactory extends BaseFactory {

        public FooBarFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        public List<?> createList(ICS ics) {
            return Collections.emptyList();
        }

        public FooBar createFooBar(ICS ics) {
            return new FooBar(ics);
        }

    }

    abstract class ABarFactory extends BaseFactory {

        public ABarFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        public List<?> createList(ICS ics) {
            return Collections.emptyList();
        }

        public FooBar createFooBar(ICS ics) {
            return new FooBar(ics);
        }

    }

    class CBarFactory extends ABarFactory {

        public CBarFactory(MockICS ics, Factory... roots) {
            super(ics, roots);
        }

        @ServiceProducer(name = "foobar")
        public FooBar createFooBar(ICS ics) {
            return new FooBar(ics);
        }

    }

    static class SBarFactory {

        @ServiceProducer(name = "foobar2")
        public static SBar createFooBar(ICS ics) {
            return new SBar();
        }

    }

}
