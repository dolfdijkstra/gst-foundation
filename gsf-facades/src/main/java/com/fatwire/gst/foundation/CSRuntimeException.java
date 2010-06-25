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
    /**
	 * 
	 */
    private static final long serialVersionUID = 4188899178173205442L;
    private final int errno;
    private final ftErrors complexError;

    public CSRuntimeException(String msg, int errno) {
        super(msg + " (errno=" + errno + ")");
        this.errno = errno;
        this.complexError = null;
    }

    public CSRuntimeException(String msg, int errno, Throwable t) {
        super(msg + " (errno=" + errno + ")", t);
        this.errno = errno;
        this.complexError = null;
    }

    public CSRuntimeException(String msg, ftErrors complexError, int errno) {
        super(msg, complexError);
        this.errno = errno;
        this.complexError = complexError;
    }

    public int getErrno() {
        return errno;
    }

    public ftErrors getComplexError() {
        return complexError;
    }

    public String getMessage() {
        // format:
        StringBuilder builder = new StringBuilder();
        builder.append(super.getMessage());
        if (complexError != null) {
            if (errno != complexError.getReason()) {
                builder.append("|errno:").append(errno);
            }
            builder.append("|reason:").append(complexError.getReason());
            builder.append("|message:");
            builder.append(complexError.getMessage());

            int details = complexError.details();
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
