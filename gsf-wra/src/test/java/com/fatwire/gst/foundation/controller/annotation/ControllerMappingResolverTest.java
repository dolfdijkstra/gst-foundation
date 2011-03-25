package com.fatwire.gst.foundation.controller.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.test.MockICS;

public class ControllerMappingResolverTest extends TestCase {

    static class Foo {
        int i = 0;

        @IcsVariable(var = { "cmd=login", "cmd=logout" })
        public void doSomething(ICS ics) {
            i++;

        }
    }

    public void testFindControllerMethod() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        ControllerMappingResolver resolver = new ControllerMappingResolver();
        Foo foo = new Foo();
        ICS ics = new MockICS() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.fatwire.gst.foundation.facade.uri.MockICS#GetVar(java.lang
             * .String)
             */
            @Override
            public String GetVar(String name) {
                if ("cmd".equals(name))
                    return "login";
                return null;
            }

        };
        Method m = resolver.findControllerMethod(ics, foo);
        m.invoke(foo, new Object[] { ics });
        assertEquals(1, foo.i);
    }

}
