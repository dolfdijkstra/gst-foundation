/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.uri;

import com.fatwire.gst.foundation.test.MockICS;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import junit.framework.Assert;
import junit.framework.TestCase;

public class PageUriBuilderTest extends TestCase {

    public void testPageUriBuilderString() {
        ICS ics = new MockICS() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.fatwire.gst.foundation.facade.uri.MockICS#runTag(java.lang
             * .String, COM.FutureTense.Interfaces.FTValList)
             */
            @Override
            public String runTag(String tag, FTValList list) {
                Assert.assertEquals("RENDER.GETPAGEURL", tag);
                Assert.assertEquals(3, list.count());
                return null;
            }

        };
        String uri = new PageUriBuilder("GST/Wrapper").argument("foo", "bar").toURI(ics);

    }

}
