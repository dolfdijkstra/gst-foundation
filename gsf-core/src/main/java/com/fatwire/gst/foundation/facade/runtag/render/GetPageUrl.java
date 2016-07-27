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

package com.fatwire.gst.foundation.facade.runtag.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a PageUrl.
 * 
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 */
public class GetPageUrl extends TagRunnerWithArguments {

    private static Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.runtag.render.GetTemplateUrl");

    /**
     * 
     */
    public GetPageUrl() {
        super("RENDER.GETPAGEURL");
    }

    /**
     * @param b flag for ADDSESSION
     */
    public void setAddSession(boolean b) {
        set("ADDSESSION", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s string value for assembler
     */
    public void setAssembler(String s) {
        set("ASSEMBLER", s);
        set("assembler", s);
    }

    /**
     * @param s string value for AUTHORITY
     */
    public void setAuthority(String s) {
        set("AUTHORITY", s);
    }

    /**
     * @param s string value for C , current asset
     */
    public void setC(String s) {
        set("C", s);
    }

    /**
     * @param s string value for CID, current asset id
     */
    public void setCid(String s) {
        set("CID", s);
    }

    /**
     * @param s string value for P
     */
    public void setP(String s) {
        set("P", s);
    }

    /**
     * @param s string value for CONTAINER
     */
    public void setContainer(String s) {
        set("CONTAINER", s);
    }

    /**
     * @param s string value for DEPTYPE
     */
    public void setDeptype(String s) {
        set("DEPTYPE", s);
    }

    /**
     * @param b flag for DYNAMIC
     */
    public void setDynamic(boolean b) {
        set("DYNAMIC", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s string value for FRAGMENT
     */
    public void setFragment(String s) {
        set("FRAGMENT", s);
    }

    /**
     * @param s string value for OUTSTR
     */
    public void setOutstr(String s) {
        set("OUTSTR", s);
    }

    /**
     * @param s string value for packed arguments
     */
    public void setPackedargs(String s) {
        set("PACKEDARGS", s);
    }

    /**
     * @param b flag for SATELLITE
     */
    public void setSatellite(boolean b) {
        set("SATELLITE", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s string value for SCHEME
     */
    public void setScheme(String s) {
        set("SCHEME", s);
    }

    /**
     * @param s string value for page name
     */
    public void setPagename(String s) {
        set("PAGENAME", s);
    }

    /**
     * @param s string value for wrapper page
     */
    public void setWrapperpage(String s) {
        if (LOG.isTraceEnabled())
            LOG.trace("Setting wrapper to :" + s);
        set("wrapperpage", s);
        set("WRAPPERPAGE", s);
    }
}
