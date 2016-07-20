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

/**
 * Stateful stopwatch object used for measuring elapsed time. Can be used to record cumulative time as well as split times.
 * Very lightweight. After creation, start() must be called to start the stopwatch. split() records the time since
 * the last split() call (or start() call if split() has not yet been called).
 * elapsed() records the total time elapsed since the timer was started. Elapsed() does not reset the stopwatch or
 * save the latest split.
 *
 * The stopwatch can be restarted by calling start().
 * @author Tony Field
 * @since 2016-07-17
 */
public interface Stopwatch {

    /**
     * Starts the stopwatch. Must be called before taking a split or elapsed time.
     */
    void start();

    /**
     * Record time elapsed since the last interval measurement or since started/restarted.
     * Resets the interval timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void split(String message);

    /**
     * Record time elapsed since the last interval measurement or since started/restarted.
     * Resets the interval timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     * @param arguments arguments to be substituted into the message. Parameterization operates as in slf4j:
     *                http://www.slf4j.org/faq.html#logging_performance
     */
    void split(String message, Object... arguments);

    /**
     * Record the elapsed time since the stopwatch was started/restarted.
     * Does not reset either the interval timer or the cumulative timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     */
    void elapsed(String message);

    /**
     * Record the elapsed time since the stopwatch was started/restarted.
     * Does not reset either the interval timer or the cumulative timer.
     * @param message message to record (typically a description of the functionality measured, like a component name.)
     * @param arguments arguments to be substituted into the message. Parameterization operates as in slf4j:
     *                http://www.slf4j.org/faq.html#logging_performance
     */
    void elapsed(String message, Object... arguments);
}
