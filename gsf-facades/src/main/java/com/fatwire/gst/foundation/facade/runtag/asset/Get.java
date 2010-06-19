/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
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
