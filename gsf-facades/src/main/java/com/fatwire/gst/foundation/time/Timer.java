package com.fatwire.gst.foundation.time;

/**
 * Stateful timer object used for measuring elapsed time.
 * Contains both cumulative timers and interval timers.
 * @author Tony Field
 * @since 2016-07-17
 */
public interface Timer {

    /**
     * Reset the cumulative and interval timers.
     */
    void reset();

    /**
     * Record time elapsed since the last interval measurement (or the last reset, or timer creation).
     * Resets the interval timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void interval(String message);

    /**
     * Record the elapsed time since the timer creation (or the last timer reset).
     * Does not reset either the interval timer or the cumulative timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void elapsed(String message);
}
