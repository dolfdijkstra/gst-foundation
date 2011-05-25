/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation;

import COM.FutureTense.Util.ftErrors;

/**
 * Generic Content Server exception that knows about errno.
 * 
 * @author Dolf Dijkstra
 * @author Tony Field
 * @since 10-Jun-2008
 */
public class CSRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 4188899178173205442L;
    private final int errno;
    private final ftErrors complexError;

    /**
     * @param msg the message
     * @param errno the Content Server errno
     */
    public CSRuntimeException(final String msg, final int errno) {
        super(msg + " (errno=" + errno + ")");
        this.errno = errno;
        this.complexError = null;
    }

    /**
     * @param msg the message
     * @param errno the Content Server errno
     * @param cause the Throwable as a cause
     */
    public CSRuntimeException(final String msg, final int errno, final Throwable cause) {
        super(msg + " (errno=" + errno + ")", cause);
        this.errno = errno;
        this.complexError = null;
    }

    /**
     * @param msg the message
     * @param complexError the complex error
     * @param errno the Content Server errno
     */
    public CSRuntimeException(final String msg, final ftErrors complexError, final int errno) {
        super(msg, complexError);
        this.errno = errno;
        this.complexError = complexError;
    }

    /**
     * @return the Content Server errno
     */
    public final int getErrno() {
        return errno;
    }

    /**
     * @return the complex error, or null if it was not set.
     */
    public final ftErrors getComplexError() {
        return complexError;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        // format:
        final StringBuilder builder = new StringBuilder();
        builder.append(super.getMessage());
        if (complexError != null) {
            if (errno != complexError.getReason()) {
                builder.append("|errno:").append(errno);
            }
            builder.append("|reason:").append(complexError.getReason());
            builder.append("|message:");
            builder.append(complexError.getMessage());

            final int details = complexError.details();
            if (details > 0) {
                builder.append("|details:");
            }
            for (int i = 0; i < details; i++) {
                builder.append(" ");
                builder.append(complexError.detail(i));
            }
        } else {
            builder.append("|errno:").append(errno);
        }
        return builder.toString();

    }
}
