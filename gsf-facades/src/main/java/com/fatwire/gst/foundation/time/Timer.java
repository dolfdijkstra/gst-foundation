package com.fatwire.gst.foundation.time;

/**
 * Stateful timer object used for measuring elapsed time. Can be used to record cumulative time as well as split times.
 * Very lightweight. After creation, start() must be called to start the timer. split() records the time since
 * the last split() call (or start() call if split() has not yet been called).
 * elapsed() records the total time elapsed since the timer was started. Elapsed() does not reset the timer or
 * save the latest split.
 *
 * The timer can be restarted by calling restart().
 * @author Tony Field
 * @since 2016-07-17
 */
public interface Timer {

    /**
     * Starts the timer. Must be called before taking a split or elapsed time. Should not be called twice in a row.
     * @see #restart()
     */
    void start();

    /**
     * Clears and re-starts the timer.
     */
    void restart();

    /**
     * Record time elapsed since the last interval measurement or since started/restarted.
     * Resets the interval timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void split(String message);

    /**
     * Record the elapsed time since the timer was started/restarted.
     * Does not reset either the interval timer or the cumulative timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void elapsed(String message);
}
