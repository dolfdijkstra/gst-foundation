package com.fatwire.gst.foundation.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timer that records records into a logger. If the logger specified is not enabled at the debug level,
 * this timer does nothing. Designed for performance measurements.
 *
 * Very lightweight object. Upon instantiation, the current time is measured once.
 *
 * @author Tony Field
 * @since 2016-07-17
 */
public final class LoggerTimer implements Timer {

    /**
     * Default logger name used by this timer if no other logger is named.
     */
    public static final String DEFAULT_LOGGER_NAME = "tools.gsf.time";

    /**
     * Timer message format string. This format will be used for all measurements (interval and cumulative).
     * The first {} marker will be replaced by the message string provided, and the second one with the time
     * formatted for human readability (although the first measurement will be provided in microseconds).
     */
    public static final String MESSAGE_FORMAT = "Timer measurement: [{}] took [{}µs] ({})";

    private final Logger logger;
    private long startTimeNanos;
    private long lastIntervalTimeNanos;

    /**
     * Create a logger timer using the logger specified. If debug is not enabled, the NoopTimer is returned
     * @param logger logger instance
     * @return the timer or the NoopTimer if debug is not enabled
     * @see NoopTimer
     */
    public static Timer getInstance(Logger logger) {
        return logger.isDebugEnabled() ? new LoggerTimer(logger) : NoopTimer.INSTANCE;
    }

    /**
     * Create a logger timer using the logger specified. If debug is not enabled, the NoopTimer is returned
     * @param logger logger instance
     * @return the timer or the NoopTimer if debug is not enabled
     * @see NoopTimer
     */
    public static Timer getInstance(String logger) {
        return getInstance(LoggerFactory.getLogger(logger));
    }

    /**
     * Create a timer using the default logger, if debug is enabled. If debug is not enabled, the NoopTimer
     * is returned.
     * @return the timer or the NoopTimer if debug is not enabled
     * @see NoopTimer
     * @see #DEFAULT_LOGGER_NAME
     */
    public static Timer getInstance() {
        return getInstance(DEFAULT_LOGGER_NAME);
    }

    private LoggerTimer(Logger logger) {
        this.logger = logger;
        this.startTimeNanos = 0L;
        this.lastIntervalTimeNanos = 0L;
    }


    @Override
    public void start() {
        if (startTimeNanos == 0L) {
            startTimeNanos = System.nanoTime();
            lastIntervalTimeNanos = startTimeNanos;
        } else {
            throw new IllegalStateException("Already started. Try calling restart() instead.");
        }
    }

    @Override
    public void restart() {
        if (startTimeNanos == 0L) {
            throw new IllegalStateException("Cannot restart: timer has never been started.");
        } else {
            startTimeNanos = System.nanoTime();
            lastIntervalTimeNanos = startTimeNanos;
        }
    }

    @Override
    public void split(String msg) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        // no need for code guards - debug is definitely enabled
        long now = System.nanoTime();
        _log(lastIntervalTimeNanos, now, msg);
        lastIntervalTimeNanos = now;
    }

    @Override
    public void elapsed(String msg) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        // no need for code guards - debug is definitely enabled
        long now = System.nanoTime();
        _log(startTimeNanos, now, msg);
    }

    private void _log(long startNanos, long endNanos, String msg) {
        // no need for code guards - debug is definitely enabled
        long micros = (endNanos - startNanos) / 1000;
        String human = _humanFormat(micros);
        logger.debug(MESSAGE_FORMAT, msg, Long.toString(micros), human);
    }

    private String _humanFormat(long micros) {
        String human;
        if (micros > 1000000) {
            final long millis = micros / 1000;
            human = Long.toString(millis / 1000) + "." + String.format("%03d", (millis % 1000)) + "s";
        } else if (micros > 1000) {
            human = Long.toString(micros / 1000) + "." + String.format("%03d", (micros % 1000)) + "ms";
        } else {
            human = Long.toString(micros) + "µs";
        }
        return human;
    }
}
