/*
 * Copyright 2012 oracle Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.logging;

/**
 * Extension of commons logging to allow for log messages with arbitrary number of arguments, elleviating the developer from string concatenation and level checking to prevent expensive string contatenation.    
 * 
 * @author Dolf Dijkstra
 *
 */
public interface Log extends org.apache.commons.logging.Log {

    public void trace(String message, Throwable t, Object... args);

    public void trace(String message, Object... args);

    public void debug(String message, Throwable t, Object... args);

    public void debug(String message, Object... args);

    public void info(String message, Throwable t, Object... args);

    public void info(String message, Object... args);

    public void warn(String message, Throwable t, Object... args);

    public void warn(String message, Object... args);

    public void error(String message, Throwable t, Object... args);

    public void error(String message, Object... args);

    public void fatal(String message, Throwable t, Object... args);

    public void fatal(String message, Object... args);

}
