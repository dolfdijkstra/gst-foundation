package com.fatwire.gst.foundation.time;

import junit.framework.TestCase;

/**
 * @author Tony Field
 * @since 2016-07-17
 */
public class LoggerTimerTest extends TestCase {
    public void testLogger() {
        Timer timer = LoggerTimer.getInstance();
        timer.reset();
        timer.interval("first interval");
        timer.interval("second interval");
        timer.elapsed("Total cumulative");
    }
}
