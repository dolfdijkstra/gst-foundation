package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.XML.Template.Seed2;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.openmarket.xcelerate.publish.Render;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * @author Tony Field
 * @author Dolf.Dijkstra
 * @since Jun 16, 2010
 */

public abstract class AbstractController implements Seed2 {
    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.controller");

    public static String STATUS_HEADER = "X-Fatwire-Status";

    protected ICS ics;

    /*
     * (non-Javadoc)
     * 
     * @see
     * COM.FutureTense.XML.Template.Seed2#SetAppLogic(COM.FutureTense.Interfaces
     * .IPS)
     */

    public void SetAppLogic(final IPS ips) {
        ics = ips.GetICSObject();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * COM.FutureTense.XML.Template.Seed#Execute(COM.FutureTense.Interfaces.
     * FTValList, COM.FutureTense.Interfaces.FTValList)
     */

    public final String Execute(final FTValList vIn, final FTValList vOut) {
        try {
            doExecute();
        } catch (final Exception e) {
            handleException(e);
        }

        return "";
    }

    /**
     * Sends the http status code to the user-agent
     *
     * @param code the http response code
     * @return String to stream
     */
    protected String sendError(final int code, final Exception e) {
        LOG.debug(code + " status code sent due to exception " + e.toString(), e);
        switch (code) { // all the http status codes, we may restrict the list
            // to error and redirect
            case 100:
            case 101:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 300:
            case 301:
            case 302:
            case 303:
            case 304:
            case 306:
            case 307:
            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            case 405:
            case 406:
            case 407:
            case 408:
            case 409:
            case 410:
            case 411:
            case 412:
            case 413:
            case 414:
            case 415:
            case 416:
            case 417:
            case 450:
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 505:
                ics.StreamHeader(STATUS_HEADER, Integer.toString(code));
                break;
            default:
                ics.StreamHeader(STATUS_HEADER, "500");
                break;
        }
        Render.UnknownDeps(ics);// failure case might be corrected on next
        // publish or save
        String element = null;

        if (goodString(ics.GetVar("site")) && ics.IsElement(ics.GetVar("site") + "/ErrorHandler/" + code)) {
            element = ics.GetVar("site") + "/ErrorHandler/" + code;
        } else if (ics.IsElement("GST/ErrorHandler/" + code)) {
            element = "GST/ErrorHandler/" + code;
        }
        if (element != null) {
            ics.SetObj("com.fatwire.gst.foundation.exception", e);
            ics.CallElement(element, null);
        }
        ics.SetErrno(ftErrors.exceptionerr);

        return null;

    }

    /**
     * Executes the core business logic of the controller.
     *
     * @throws CSRuntimeException may throw a CSRuntimeException which is handled by
     *                            handleCSRuntimeException
     */
    abstract protected void doExecute();

    /**
     * Handles the exception, doing what is required
     *
     * @param e exception
     */
    abstract protected void handleException(Exception e);
}
