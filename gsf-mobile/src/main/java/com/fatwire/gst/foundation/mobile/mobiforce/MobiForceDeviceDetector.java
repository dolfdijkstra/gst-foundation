/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.mobile.mobiforce;

import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import COM.FutureTense.Interfaces.ICS;

import org.apache.commons.lang.StringUtils;

import com.fatwire.gst.foundation.mobile.DeviceDetector;
import com.fatwire.gst.foundation.mobile.DeviceType;

/**
 * DeviceDecector making use of the MobiForce algorithm. This is a simple
 * algorithm and takes care of most of the devices based on User-Agent header.
 * </p> To override the User-Agent algorithm, you can add a parameter <tt>d</tt>
 * with a value of <tt>mobile, tablet or desktop</tt>.
 * 
 * @author Dolf Dijkstra
 * @since 25 jun. 2012
 */
public class MobiForceDeviceDetector implements DeviceDetector {
    public static String DEVICE_VAR = "d";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.oracle.ateam.webcenter.sites.DeviceDetector#detectDeviceType(COM.
     * FutureTense.Interfaces.ICS)
     */
    @Override
    public DeviceType detectDeviceType(ICS ics) {
        if ("desktop".equals(ics.GetVar(DEVICE_VAR)))
            return DeviceType.DESKTOP;
        if ("mobile".equals(ics.GetVar(DEVICE_VAR)))
            return DeviceType.MOBILE;
        if ("tablet".equals(ics.GetVar(DEVICE_VAR)))
            return DeviceType.TABLET;

        @SuppressWarnings("deprecation")
        HttpServletRequest request = ics.getIServlet().getServletRequest();
        return detectDeviceType(request);
    }

    protected DeviceType detectDeviceType(HttpServletRequest request) {
        // UAProf detection
        if (request.getHeader("x-wap-profile") != null || request.getHeader("Profile") != null) {
            return DeviceType.MOBILE;
        }

        if (StringUtils.isNotBlank(request.getHeader("Accept"))
                && request.getHeader("Accept").toLowerCase(Locale.US).indexOf("application/vnd.wap.xhtml+xml") > 0) {
            return DeviceType.MOBILE;
        }

        String userAgent = request.getHeader("User-Agent");
        DeviceType type = detectDeviceType(userAgent);
        // OperaMini special case
        if (type == DeviceType.DESKTOP) {
            @SuppressWarnings("rawtypes")
            Enumeration headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = (String) headers.nextElement();
                if (header.contains("OperaMini")) {
                    return DeviceType.MOBILE;
                }

            }
        }
        return type;

    }

    protected DeviceType detectDeviceType(String ua) {
        if (StringUtils.isBlank(ua) || ua.length() < 5)
            return DeviceType.DESKTOP;
        String userAgent = ua.toLowerCase(Locale.US);

        String mobile_ua = userAgent.substring(0, 4);
        for (String prefix : KNOWN_MOBILE_USER_AGENT_PREFIXES) {
            if (prefix.equals(mobile_ua))
                return DeviceType.MOBILE;
        }

        // UserAgent keyword detection for Mobile and Tablet devices

        for (String keyword : KNOWN_MOBILE_USER_AGENT_KEYWORDS) {
            if (userAgent.contains(keyword)) {
                return DeviceType.MOBILE;
            }
        }
        for (String keyword : KNOWN_TABLET_USER_AGENT_KEYWORDS) {
            if (userAgent.contains(keyword)) {
                return DeviceType.TABLET;
            }
        }
        // Android special case
        if (userAgent.contains("android") && userAgent.contains("mobile")) {
            return DeviceType.MOBILE;
        } else if (userAgent.contains("android") && !userAgent.contains("mobile")) {
            return DeviceType.TABLET;
        }
        return DeviceType.DESKTOP;// default to desktop
    }

    private static final String[] KNOWN_MOBILE_USER_AGENT_PREFIXES = new String[] { "w3c ", "w3c-", "acs-", "alav",
            "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
            "doco", "eric", "hipt", "htc_", "inno", "ipaq", "ipod", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d",
            "lg-g", "lge-", "lg/u", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-",
            "newt", "noki", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany",
            "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb",
            "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp",
            "wapr", "webc", "winw", "winw", "xda ", "xda-" };
    private static final String[] KNOWN_MOBILE_USER_AGENT_KEYWORDS = new String[] { "blackberry", "webos", "ipod",
            "lge vx", "midp", "maemo", "mmp", "netfront", "hiptop", "nintendo DS", "novarra", "openweb", "opera mobi",
            "opera mini", "palm", "psp", "phone", "smartphone", "symbian", "up.browser", "up.link", "wap", "windows ce" };

    private static final String[] KNOWN_TABLET_USER_AGENT_KEYWORDS = new String[] { "ipad", "playbook", "hp-tablet" };

}
