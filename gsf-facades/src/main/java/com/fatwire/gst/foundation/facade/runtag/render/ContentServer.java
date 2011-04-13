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

import java.util.Date;

/**
 * &lt;RENDER.CONTENTSERVER PAGENAME="nameOfPageEntry" [ARGS_var1="value"]/&gt;
 * 
 * 
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 */
public class ContentServer extends TagRunnerWithArguments {

    private static final String ARGS = "ARGS_";

    public ContentServer() {
        super("RENDER.CONTENTSERVER");
    }

    public ContentServer(String pagename) {
        super("RENDER.CONTENTSERVER");
        set("PAGENAME", pagename);
    }

    public void setPagename(String s) {
        set("PAGENAME", s);
    }

    /**
     * Call template args are prefixed with ARGS_ in order to be available in
     * the called template
     * 
     * @param name parameter name
     * @param value parameter value
     */
    public void setArgument(final String name, final String value) {
        super.set(ARGS + name, value);
    }

    /**
     * Args are prefixed with ARGS_ in order to be available in the called
     * template
     * 
     * @param name parameter name
     * @param value parameter value
     */
    public void setArgument(final String name, final long value) {
        super.set(ARGS + name, value);
    }

    /**
     * Args are prefixed with ARGS_ in order to be available in the called
     * template
     * 
     * @param name parameter name
     * @param value parameter value
     */
    public void setArgument(final String name, final Date value) {
        super.set(ARGS + name, value);
    }

    /**
     * Args are prefixed with ARGS_ in order to be available in the called
     * template
     * 
     * @param name parameter name
     * @param value parameter value
     */
    public void setArgument(final String name, final int value) {
        super.set(ARGS + name, value);
    }

    /**
     * Args are prefixed with ARGS_ in order to be available in the called
     * template
     * 
     * @param name parameter name
     * @param value parameter value
     */
    public void setArgument(final String name, final boolean value) {
        super.set(ARGS + name, value);
    }

}
