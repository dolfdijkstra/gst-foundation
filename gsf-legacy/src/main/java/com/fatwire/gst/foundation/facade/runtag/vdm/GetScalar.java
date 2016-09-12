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

package com.fatwire.gst.foundation.facade.runtag.vdm;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the VDM.GETSCALAR xml tag
 * 
 * @author Tony Field and Mike Field
 * @since June 9, 2008
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class GetScalar extends AbstractTagRunner {
    public GetScalar(String attribute, String varname) {
        this();
        setAttribute(attribute);
        setVarname(varname);
    }

    // Default Constructor
    public GetScalar() {
        super("VDM.GETSCALAR");
    }

    /**
     * Sets attribute to the value of <code>s</code>
     * 
     * @param s The name of the attribute
     */
    public void setAttribute(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid attribute string: " + s);
        }
        this.set("ATTRIBUTE", s);
    }

    /**
     * Sets the varname to the value of <code>s</code>
     * 
     * @param s The name of the varname
     */
    public void setVarname(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid varname string: " + s);
        }
        this.set("VARNAME", s);
    }

}
