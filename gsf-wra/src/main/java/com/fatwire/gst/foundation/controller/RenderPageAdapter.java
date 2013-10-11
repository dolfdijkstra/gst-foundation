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
package com.fatwire.gst.foundation.controller;


import static COM.FutureTense.Interfaces.Utilities.goodString;

import javax.servlet.http.HttpServletResponse;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.facade.runtag.render.Unknowndeps;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.db.UrlRegistry2;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

/**
 * <p>
 * This an adapter so that Controllers and Actions can call the RenderPage logic. The RenderPage logic dispatches the request to the
 * correct asset/template combination based on the path field for a Web
 * Referenceable Asset.
 * <p/>
 * 
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class RenderPageAdapter extends BaseRenderPage {

    public static final String STATUS_HEADER = "X-Fatwire-Status";
    public RenderPageAdapter(ICS ics) {
        this.ics = ics;
        pathTranslationService = UrlRegistry2.lookup(ics);
        wraCoreFieldDao = new AssetApiWraCoreFieldDao(ics);
        aliasCoreFieldDao = new AssetApiAliasCoreFieldDao(ics, wraCoreFieldDao);
    }

    public RenderPageAdapter(ICS ics, WraPathTranslationService pathTranslationService,
            WraCoreFieldDao wraCoreFieldDao, AliasCoreFieldDao aliasCoreFieldDao) {
        super();
        this.ics = ics;
        this.pathTranslationService = pathTranslationService;
        this.wraCoreFieldDao = wraCoreFieldDao;
        this.aliasCoreFieldDao = aliasCoreFieldDao;
    }

    public void doExecute() {
        LOG.trace("RenderPageAdapter execution started");
        recordCompositionalDependencies();
        renderPage();
        LOG.trace("RenderPageAdapter execution complete");
    }

    protected void handleException(final Exception e) {
        if (e instanceof CSRuntimeException) {
            handleCSRuntimeException((CSRuntimeException) e);
        } else {
            sendError(500, e);
        }
    }

    protected final String sendError(final int code, final Exception e) {
        LOG.debug(code + " status code sent due to exception " + e.toString(), e);
        if (LOG.isTraceEnabled()) {
            DebugHelper.dumpVars(ics, LOG);
        }
        switch (code) { // all the http status codes, we may restrict the list
            // to error and redirect
            case HttpServletResponse.SC_ACCEPTED:
            case HttpServletResponse.SC_BAD_GATEWAY:
            case HttpServletResponse.SC_BAD_REQUEST:
            case HttpServletResponse.SC_CONFLICT:
            case HttpServletResponse.SC_CONTINUE:
            case HttpServletResponse.SC_CREATED:
            case HttpServletResponse.SC_EXPECTATION_FAILED:
            case HttpServletResponse.SC_FORBIDDEN:
            case HttpServletResponse.SC_FOUND:
            case HttpServletResponse.SC_GATEWAY_TIMEOUT:
            case HttpServletResponse.SC_GONE:
            case HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED:
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
            case HttpServletResponse.SC_LENGTH_REQUIRED:
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
            case HttpServletResponse.SC_MOVED_PERMANENTLY:
                // case HttpServletResponse.SC_MOVED_TEMPORARILY : //SC_FOUND is
                // preferred
            case HttpServletResponse.SC_MULTIPLE_CHOICES:
            case HttpServletResponse.SC_NO_CONTENT:
            case HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION:
            case HttpServletResponse.SC_NOT_ACCEPTABLE:
            case HttpServletResponse.SC_NOT_FOUND:
            case HttpServletResponse.SC_NOT_IMPLEMENTED:
            case HttpServletResponse.SC_NOT_MODIFIED:
            case HttpServletResponse.SC_OK:
            case HttpServletResponse.SC_PARTIAL_CONTENT:
            case HttpServletResponse.SC_PAYMENT_REQUIRED:
            case HttpServletResponse.SC_PRECONDITION_FAILED:
            case HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED:
            case HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE:
            case HttpServletResponse.SC_REQUEST_TIMEOUT:
            case HttpServletResponse.SC_REQUEST_URI_TOO_LONG:
            case HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
            case HttpServletResponse.SC_RESET_CONTENT:
            case HttpServletResponse.SC_SEE_OTHER:
            case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
            case HttpServletResponse.SC_SWITCHING_PROTOCOLS:
            case HttpServletResponse.SC_TEMPORARY_REDIRECT:
            case HttpServletResponse.SC_UNAUTHORIZED:
            case HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE:
            case HttpServletResponse.SC_USE_PROXY:
                ics.StreamHeader(STATUS_HEADER, Integer.toString(code));
                break;
            default:
                ics.StreamHeader(STATUS_HEADER, "500");
                break;
        }
        Unknowndeps.unknonwDeps(ics);// failure case might be corrected on next
        // publish or save
        renderErrorPage(code, e);

        return null;

    }

    /**
     * Renders the error page. This method can be overwritten is other elements
     * need to be invoked to render an error page.
     * 
     * @param code
     * @param e
     */
    protected void renderErrorPage(final int code, final Exception e) {
        String element = null;

        if (goodString(ics.GetVar("site")) && ics.IsElement(ics.GetVar("site") + "/ErrorHandler/" + code)) {
            element = ics.GetVar("site") + "/ErrorHandler/" + code;
        } else if (ics.IsElement("GST/ErrorHandler/" + code)) {
            element = "GST/ErrorHandler/" + code;
        } else if (ics.IsElement("GST/ErrorHandler")) {
            element = "GST/ErrorHandler";
        }
        if (element != null) {
            ics.SetObj("com.fatwire.gst.foundation.exception", e);
            ics.CallElement(element, null);
        }
        ics.SetErrno(ftErrors.exceptionerr);
    }

    /**
     * This method transforms errno values into http status codes and sets them
     * using the X-Fatwire-Status header.
     * <p/>
     * Only some errnos are handled by this base class.
     * <p/>
     * More info coming soon
     * 
     * @param e exception
     */
    protected void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
            case 400:
            case ftErrors.badparams:
                sendError(400, e);
                break;
            case 404:
            case ftErrors.pagenotfound:
                sendError(404, e);
                break;
            case 403:
            case ftErrors.noprivs:
                sendError(403, e);
                break;
            default:
                sendError(500, e);
                break;
        }
    }

}
