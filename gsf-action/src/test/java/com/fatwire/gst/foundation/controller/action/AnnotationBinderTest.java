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

import junit.framework.TestCase;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.controller.annotation.Bind;
import com.fatwire.gst.foundation.test.MockICS;

public class AnnotationBinderTest extends TestCase {

    class MyObject {
        @Bind
        String pagename;
        @Bind
        long cid;
        @Bind
        Long tid;
        @Bind
        String c;
        @Bind("foo")
        String bar;

    }

    public void testBind_csvar() {
        MyObject o = new MyObject();
        ICS ics = new MockICS() {

            @Override
            public String GetVar(String key) {
                if (ftMessage.PageName.equals(key)) {
                    return "hello";
                } else if ("cid".equals(key)) {
                    return "12345678901234";
                } else if ("tid".equals(key)) {
                    return "22345678901234";
                } else if ("foo".equals(key)) {
                    return "oof";

                }
                return null;
            }

        };
        AnnotationBinder.bind(o, ics);
        assertEquals("hello", o.pagename);
        assertEquals(12345678901234L, o.cid);
        assertEquals(new Long(22345678901234L), o.tid);
        assertNull(o.c);
        assertEquals("oof", o.bar);
    }

}
