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

package com.fatwire.gst.foundation;

import junit.framework.TestCase;

public class DebugHelperTest extends TestCase {

    public void testMicroToHuman() {
        assertEquals("(60004us) 60.4ms",DebugHelper.microToHuman(60004));
    }

    public void testMilliToHuman() {
        assertEquals("(60004ms) 1m 0s",DebugHelper.milliToHuman(60004));
    }

    public void testNanoToHuman() {
        assertEquals("(60us) 60us",DebugHelper.nanoToHuman(60004));
    }

}
