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
 * A timer that does nothing. Only available as a singleton.
 *
 * @author Tony Field
 * @since 2016-07-17
 */
public class NoopStopwatch implements Stopwatch {
    public static final Stopwatch INSTANCE = new NoopStopwatch();
    private NoopStopwatch() {}
    public void start() {}
    public void split(String message) {}
    public void split(String message, Object... arguments) {}
    public void elapsed(String message) {}
    public void elapsed(String message, Object... arguments) {}
}
