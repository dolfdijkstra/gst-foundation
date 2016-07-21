/*
 * Copyright 2016 Function1, Inc. All Rights Reserved.
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
package tools.gsf.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stopwatch that records records into a logger. If the logger specified is not enabled at the debug level,
 * this timer does nothing. Designed for performance measurements.
 *
 * Very lightweight object. Upon instantiation, the current time is measured once.
 *
 * @author Tony Field
 * @since 2016-07-17
 */
public final class LoggerStopwatch implements Stopwatch {

    /**
     * Default logger name used by this timer if no other logger is named.
     */
    public static final String DEFAULT_LOGGER_NAME = "tools.gsf.time";

    /**
     * Stopwatch message format string. This format will be used for all split measurements.
     * The first {} marker will be replaced by the time in microseconds. The second marker will be replaced
     * by the human-readable version of the time. The provided message will be appended to the end.
     */
    public static final String MESSAGE_FORMAT_SPLIT = "Split timer measurement: {}us ({}) for: ";

    /**
     * Stopwatch message format string. This format will be used for all cumulative measurements.
     * The first {} marker will be replaced by the time in microseconds. The second marker will be replaced
     * by the human-readable version of the time. The provided message will be appended to the end.
     */
    public static final String MESSAGE_FORMAT_ELAPSED = "Elapsed timer measurement: {}us ({}) for: ";

    private final Logger logger;
    private long startTimeNanos;
    private long lastIntervalTimeNanos;

    /**
     * Create a logger stopwatch using the logger specified. If debug is not enabled, the NoopStopwatch is returned
     * @param logger logger instance
     * @return the stopwatch or the NoopStopwatch if debug is not enabled
     * @see NoopStopwatch
     */
    public static Stopwatch getInstance(Logger logger) {
        return logger.isDebugEnabled() ? new LoggerStopwatch(logger) : NoopStopwatch.INSTANCE;
    }

    /**
     * Create a logger stopwatch using the logger specified. If debug is not enabled, the NoopStopwatch is returned
     * @param logger logger instance
     * @return the stopwatch or the NoopStopwatch if debug is not enabled
     * @see NoopStopwatch
     */
    public static Stopwatch getInstance(String logger) {
        return getInstance(LoggerFactory.getLogger(logger));
    }

    /**
     * Create a stopwatch using the default logger, if debug is enabled. If debug is not enabled, the NoopStopwatch
     * is returned.
     * @return the stopwatch or the NoopStopwatch if debug is not enabled
     * @see NoopStopwatch
     * @see #DEFAULT_LOGGER_NAME
     */
    public static Stopwatch getInstance() {
        return getInstance(DEFAULT_LOGGER_NAME);
    }

    private LoggerStopwatch(Logger logger) {
        this.logger = logger;
        this.startTimeNanos = 0L;
        this.lastIntervalTimeNanos = 0L;
    }

    @Override
    public void start() {
        startTimeNanos = System.nanoTime();
        lastIntervalTimeNanos = startTimeNanos;
    }

    public void split(String msg) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        long now = System.nanoTime();
        _log(lastIntervalTimeNanos, now, true, msg);
        lastIntervalTimeNanos = now;
    }

    public void split(String msg, Object... args) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        long now = System.nanoTime();
        _log(lastIntervalTimeNanos, now, true, msg, args);
        lastIntervalTimeNanos = now;
    }

    public void elapsed(String msg) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        long now = System.nanoTime();
        _log(startTimeNanos, now, false, msg);
    }

    public void elapsed(String msg, Object... args) {
        if (startTimeNanos == 0L) throw new IllegalStateException("Not yet started. Call start() first.");
        long now = System.nanoTime();
        _log(startTimeNanos, now, false, msg, args);
    }

    private void _log(long startNanos, long endNanos, boolean split, String msg, Object... args) {

        long micros = (endNanos - startNanos) / 1000;
        String sMicros = Long.toString(micros);
        String human = _humanFormat(micros);

        String logmsg = split ? MESSAGE_FORMAT_SPLIT : MESSAGE_FORMAT_ELAPSED;
        logmsg += msg;

        if (args == null || args.length == 0) {
            logger.debug(logmsg, sMicros, human);
        } else {
            Object[] arguments = new Object[args.length + 2];
            arguments[0] = sMicros;
            arguments[1] = human;
            System.arraycopy(args, 0, arguments, 2, args.length);
            logger.debug(logmsg, arguments);
        }
    }

    private String _humanFormat(long micros) {
        String human;
        if (micros > 1000000) {
            final long millis = micros / 1000;
            human = Long.toString(millis / 1000) + "." + String.format("%03d", (millis % 1000)) + "s";
        } else if (micros > 1000) {
            human = Long.toString(micros / 1000) + "." + String.format("%03d", (micros % 1000)) + "ms";
        } else {
            human = Long.toString(micros) + "us";
        }
        return human;
    }
}
