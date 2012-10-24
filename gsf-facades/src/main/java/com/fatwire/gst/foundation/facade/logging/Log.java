package com.fatwire.gst.foundation.facade.logging;

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
