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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Builds a PageUrl.
 * 
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 */
public class GetPageUrl extends TagRunnerWithArguments {

    private static Log LOG = LogFactory.getLog(GetTemplateUrl.class);

    /**
     * 
     */
    public GetPageUrl() {
        super("RENDER.GETPAGEURL");
    }

    /**
     * @param b
     */
    public void setAddSession(boolean b) {
        set("ADDSESSION", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s
     */
    public void setAssembler(String s) {
        set("ASSEMBLER", s);
    }

    /**
     * @param s
     */
    public void setAuthority(String s) {
        set("AUTHORITY", s);
    }

    /**
     * @param s
     */
    public void setC(String s) {
        set("C", s);
    }

    /**
     * @param s
     */
    public void setCid(String s) {
        set("CID", s);
    }

    /**
     * @param s
     */
    public void setP(String s) {
        set("P", s);
    }

    /**
     * @param s
     */
    public void setContainer(String s) {
        set("CONTAINER", s);
    }

    /**
     * @param s
     */
    public void setDeptype(String s) {
        set("DEPTYPE", s);
    }

    /**
     * @param b
     */
    public void setDynamic(boolean b) {
        set("DYNAMIC", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s
     */
    public void setFragment(String s) {
        set("FRAGMENT", s);
    }

    /**
     * @param s
     */
    public void setOutstr(String s) {
        set("OUTSTR", s);
    }

    /**
     * @param s
     */
    public void setPackedargs(String s) {
        set("PACKEDARGS", s);
    }

    /**
     * @param b
     */
    public void setSatellite(boolean b) {
        set("SATELLITE", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s
     */
    public void setScheme(String s) {
        set("SCHEME", s);
    }

    /**
     * @param s
     */
    public void setPagename(String s) {
        set("PAGENAME", s);
    }

    /**
     * @param s
     */
    public void setWrapperpage(String s) {
        if (LOG.isTraceEnabled())
            LOG.trace("Setting wrapper to :" + s);
        set("wrapperpage", s);
        set("WRAPPERPAGE", s);
    }
}
