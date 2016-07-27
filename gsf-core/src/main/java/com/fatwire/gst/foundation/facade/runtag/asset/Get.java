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

package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the ASSET.GET xml tag
 * 
 * @author Mike Field
 * @since August 15, 2008
 */
public final class Get extends AbstractTagRunner {
    // Default Constructor
    public Get() {
        super("ASSET.GET");
    }

    /**
     * Sets name to the value of <code>s</code>
     * 
     * @param s The name of the asset to return
     */
    public void setName(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid name string: " + s);
        }
        this.set("NAME", s);
    }

    /**
     * Sets field to the value of <code>s</code>
     * 
     * @param s The field of the asset
     */
    public void setField(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid field string: " + s);
        }
        this.set("FIELD", s);
    }

    /**
     * Sets output to the value of <code>s</code>
     * 
     * @param s The name of the output variable
     */
    public void setOutput(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid output string: " + s);
        }
        this.set("OUTPUT", s);
    }

}
