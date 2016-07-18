package com.fatwire.gst.foundation.time;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tony Field
 * @since 2016-07-17
 */
public class LoggerTimerTest extends TestCase {
    public void testLogger() {
        Logger log = LoggerFactory.getLogger(this.getClass());
        Timer timer = LoggerTimer.getInstance(log);
        timer.start();
        timer.split("first split");
        timer.split("second split");
        timer.elapsed("Total cumulative");
        timer.restart();
        timer.elapsed("Second cumulative");
        try {
            timer.start();
            if (log.isDebugEnabled()) {
                Assert.fail("Should not have been able to reset");
            } else {
                // allowed to get here if timer is disabled
            }
        } catch (IllegalStateException e) {
            // good - got here.
        }
    }
}
