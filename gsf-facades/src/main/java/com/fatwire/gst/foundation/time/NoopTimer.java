package com.fatwire.gst.foundation.time;

/**
 * A timer that does nothing. Only available as a singleton.
 *
 * @author Tony Field
 * @since 2016-07-17
 */
public class NoopTimer implements Timer {
    public static final Timer INSTANCE = new NoopTimer();
    private NoopTimer() {}
    @Override
    public void reset() {}
    @Override
    public void interval(String message) {}
    @Override
    public void elapsed(String message) {}
}
