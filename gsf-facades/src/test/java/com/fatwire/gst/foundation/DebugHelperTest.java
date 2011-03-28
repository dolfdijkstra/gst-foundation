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
