/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.httpstatus;

import static com.fatwire.gst.foundation.httpstatus.HttpStatusStrings.X_FATWIRE_HEADER;
import static com.fatwire.gst.foundation.httpstatus.HttpStatusStrings.X_FATWIRE_STATUS;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A filter that transforms 2 special headers into a response status or response
 * header. In a ContentServer XML element you can call
 * ics.StreamHeader("X-Fatwire-Status","404") to set the response status for
 * Satellite Server to 404.
 * </p>
 * 
 * 
 * @version 15 June 2009
 * @author Dolf Dijkstra
 * 
 */
public class StatusFilterHttpResponseWrapper extends HttpServletResponseWrapper {

    private static Log log = LogFactory.getLog(StatusFilterHttpResponseWrapper.class.getPackage().getName());

    private int status = -1;

    private final boolean sendError;

    /**
     * Class constructor instantiating the response object
     */
    public StatusFilterHttpResponseWrapper(HttpServletResponse origResponse, boolean sendError) {
        super(origResponse);
        this.sendError = sendError;

    }

    /**
     * This method sets the response header value and names. It just proxies the
     * custom response header information if the environment is CS
     * (ContentServer). If the environment is SS (Satellite Server) then the
     * custom header information supplied as "X-FatWire-Header" and
     * "X-FatWire-Status" is parsed and set in the response header accordingly
     * 
     * @param hdrName Response header name
     * @param hdrValue Response header value
     */
    public void setHeader(String hdrName, String hdrValue) {
        if (log.isDebugEnabled()) {
            log.debug("original setHeader " + hdrName + ": " + hdrValue);
        }

        if (X_FATWIRE_STATUS.equalsIgnoreCase(hdrName)) {
            try {
                status = Integer.parseInt(hdrValue);
            } catch (Throwable t) {
                log.warn("Exception parsing  the " + hdrName + " header. " + t.getMessage());
            }
            if (status > 300) {
                // TODO low priority: is sendRedirectNeeded for 302 or 301?
                if (log.isDebugEnabled()) {
                    log.debug("setStatus to  " + status + " from " + hdrName);
                }
                if (this.isCommitted()) {
                    log.debug("wanted to setStatus to  " + status + " from " + hdrName
                            + " but the response is already committed");
                }
                if (sendError && status >= 400) {
                    try {
                        super.sendError(status);
                    } catch (IOException e) {
                        log.warn("Could not send error " + status + ".", e);
                    }
                } else {
                    super.setStatus(status);
                }
                // ignore the header all together after the setStatus, so
                // we are not leaking to the public

            } else if (status != -1) {
                log.debug("ignoring status header with value " + status + " from " + hdrName);
            }
        } else if (X_FATWIRE_HEADER.equalsIgnoreCase(hdrName)) {
            // splitting header name/value based on passed in header value,
            // pipe seperated;
            String[] headers = hdrValue.split("\\|");
            if (headers.length == 2 && headers[0] != null && headers[1] != null) {

                super.setHeader(headers[0], headers[1]);
            } else {
                log.debug(hdrName + " could not be split into something useful. " + hdrValue);
            }

        } else {
            super.setHeader(hdrName, hdrValue);
        }

    }

    @Override
    public void setStatus(int sc) {
        if (status == -1) {
            // only set it if we have not overridden it
            super.setStatus(sc);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("setStatus " + sc + " is being ignored because " + X_FATWIRE_STATUS + " header set it to "
                        + status);
            }

        }
    }

}// end of BufferedHttpResponseWrapper

