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
 * Wrapper for commons loggging Log object to extend it with the string formatting and flexible argument methods. 
 * 
 * @author Dolf Dijkstra
 *
 * @deprecated as of release 12.x, replaced with SLF4J which is natively used by (and shipped along) WCS
 *
 */
public class LogEnhancer implements Log {

    private final org.apache.commons.logging.Log delegate;

    public LogEnhancer(org.apache.commons.logging.Log f) {
        this.delegate = f;
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return delegate.isFatalEnabled();
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    public void trace(Object message) {
        delegate.trace(message);
    }

    public void trace(Object message, Throwable t) {
        delegate.trace(message, t);
    }

    public void debug(Object message) {
        delegate.debug(message);
    }

    public void debug(Object message, Throwable t) {
        delegate.debug(message, t);
    }

    public void info(Object message) {
        delegate.info(message);
    }

    public void info(Object message, Throwable t) {
        delegate.info(message, t);
    }

    public void warn(Object message) {
        delegate.warn(message);
    }

    public void warn(Object message, Throwable t) {
        delegate.warn(message, t);
    }

    public void error(Object message) {
        delegate.error(message);
    }

    public void error(Object message, Throwable t) {
        delegate.error(message, t);
    }

    public void fatal(Object message) {
        delegate.fatal(message);
    }

    public void fatal(Object message, Throwable t) {
        delegate.fatal(message, t);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogEnhancer other = (LogEnhancer) obj;
        if (delegate == null) {
            if (other.delegate != null)
                return false;
        } else if (!delegate.equals(other.delegate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void trace(String message, Throwable t, Object... args) {
        if (delegate.isTraceEnabled())
            delegate.trace(String.format(message, args), t);

    }

    @Override
    public void trace(String message, Object... args) {
        if (delegate.isTraceEnabled())
            delegate.trace(String.format(message, args));

    }

    @Override
    public void debug(String message, Throwable t, Object... args) {
        if (delegate.isDebugEnabled())
            delegate.debug(String.format(message, args), t);

    }

    @Override
    public void debug(String message, Object... args) {
        if (delegate.isDebugEnabled())
            delegate.debug(String.format(message, args));

    }

    @Override
    public void info(String message, Throwable t, Object... args) {
        if (delegate.isInfoEnabled())
            delegate.info(String.format(message, args), t);

    }

    @Override
    public void info(String message, Object... args) {
        if (delegate.isInfoEnabled())
            delegate.info(String.format(message, args));

    }

    @Override
    public void warn(String message, Throwable t, Object... args) {
        if (delegate.isWarnEnabled())
            delegate.warn(String.format(message, args), t);

    }

    @Override
    public void warn(String message, Object... args) {
        if (delegate.isWarnEnabled())
            delegate.warn(String.format(message, args));

    }

    @Override
    public void error(String message, Throwable t, Object... args) {
        if (delegate.isErrorEnabled())
            delegate.error(String.format(message, args), t);

    }

    @Override
    public void error(String message, Object... args) {
        if (delegate.isErrorEnabled())
            delegate.error(String.format(message, args));

    }

    @Override
    public void fatal(String message, Throwable t, Object... args) {
        if (delegate.isFatalEnabled())
            delegate.fatal(String.format(message, args), t);

    }

    @Override
    public void fatal(String message, Object... args) {
        if (delegate.isFatalEnabled())
            delegate.fatal(String.format(message, args));

    }

}
