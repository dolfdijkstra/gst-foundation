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
 * Wrapper around the ASSET.LOAD xml tag
 * 
 * @author Mike Field
 * @since August 15, 2008
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class Load extends AbstractTagRunner {
    // Default Constructor
    public Load() {
        super("ASSET.LOAD");
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
     * Sets type to the value of <code>s</code>
     * 
     * @param s The type of the asset
     */
    public void setType(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid type string: " + s);
        }
        this.set("TYPE", s);
    }

    /**
     * Sets objectid to the value of <code>s</code>
     * 
     * @param s The object id of the asset
     */
    public void setObjectId(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid objectid string: " + s);
        }
        this.set("OBJECTID", s);
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
     * Sets "value" to the value of <code>s</code>
     * 
     * @param s The value of the asset
     */
    public void setValue(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid value string: " + s);
        }
        this.set("VALUE", s);
    }

    /**
     * Sets site to the value of <code>s</code>
     * 
     * @param s The site of the asset
     */
    public void setSite(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid site string: " + s);
        }
        this.set("SITE", s);
    }

    /**
     * Sets deptype to the value of <code>s</code>
     * 
     * @param s exact, exists, greater or none (case sensitive)
     */
    public void setDepType(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("exact") && !s.equals("exists") && !s.equals("greater")
                && !s.equals("none")) {
            throw new IllegalArgumentException("Invalid escape string: " + s);
        }
        this.set("DEPTYPE", s);
    }

    /**
     * Sets editable to the value of <code>s</code>
     * 
     * @param s true or false (case sensitive)
     */
    public void setEditable(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid editable string: " + s);
        }
        this.set("EDITABLE", s);
    }

    /**
     * Sets option to the value of <code>s</code>
     * 
     * @param s editable, readonly, readonly_complete (case sensitive)
     */
    public void setOption(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("editable") && !s.equals("readonly")
                && !s.equals("readonly_complete")) {
            throw new IllegalArgumentException("Invalid option string: " + s);
        }
        this.set("OPTION", s);
    }

    /**
     * Sets flushonvoid to the value of <code>s</code>
     * 
     * @param s true or false (case sensitive)
     */
    public void setFlushOnVoid(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid flushonvoid string: " + s);
        }
        this.set("FLUSHONVOID", s);
    }
}
