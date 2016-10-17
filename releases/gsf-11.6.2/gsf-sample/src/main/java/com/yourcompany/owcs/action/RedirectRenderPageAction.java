package com.yourcompany.owcs.action;

import static COM.FutureTense.Interfaces.Utilities.goodString;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.RenderPageAdapter;
import com.fatwire.gst.foundation.controller.action.RenderPage;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.mobile.DeviceDetector;
import com.fatwire.gst.foundation.mobile.DeviceType;

/**
 * A sample on how one could implement redirection based on for device detection.
 * This sample probably does not work, use it as a prototype.
 * 
 * @author Dolf
 *
 */
public class RedirectRenderPageAction extends RenderPage {

    @InjectForRequest
    public DeviceDetector detector;

    @Override
    public void handleRequest(ICS ics) {
        DeviceType type = detector.detectDeviceType(ics);
        switch (type) {
            case MOBILE:

                if (goodString(ics.GetVar(VIRTUAL_WEBROOT)) && goodString(ics.GetVar(URL_PATH))) {
                    String webroot = ics.GetVar(VIRTUAL_WEBROOT);
                    String location = StringUtils.replace(webroot, "www.", "m.", 1) + ics.GetVar(URL_PATH);
                    ics.StreamHeader("Location", location);
                    ics.StreamHeader(RenderPageAdapter.STATUS_HEADER,
                            Integer.toString(HttpServletResponse.SC_MOVED_TEMPORARILY));

                } else {
                    super.handleRequest(ics);
                }
                break;
            case TABLET:
                if (goodString(ics.GetVar(VIRTUAL_WEBROOT)) && goodString(ics.GetVar(URL_PATH))) {
                    String webroot = ics.GetVar(VIRTUAL_WEBROOT);
                    String location = StringUtils.replace(webroot, "www.", "tablet.", 1) + ics.GetVar(URL_PATH);
                    ics.StreamHeader("Location", location);
                    ics.StreamHeader(RenderPageAdapter.STATUS_HEADER,
                            Integer.toString(HttpServletResponse.SC_MOVED_TEMPORARILY));

                } else {
                    super.handleRequest(ics);
                }

                break;
            case DESKTOP:
                super.handleRequest(ics);
        }

    }

}
