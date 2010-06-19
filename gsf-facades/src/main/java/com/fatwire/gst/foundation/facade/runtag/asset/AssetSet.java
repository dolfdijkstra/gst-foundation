/*
 * Copyright (c) 2008 FieldCo, Inc. All Rights Reserved.
 */
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
 * Wrapper around ASSET.SET tag
 * 
 * @author Mike Field
 * @since Jun 11, 2008
 */
public final class AssetSet extends AbstractTagRunner {
    // Default Constructor
    public AssetSet() {
        super("ASSET.SET");
    }

    /**
     * Sets name to the value of <code>s</code>
     * 
     * @param s The name of the asset instance
     */
    public void setName(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid string for Name: " + s);
        }
        this.set("NAME", s);
    }

    /**
     * Sets name to the field to be set, to <code>s</code>
     * 
     * @param s The name of the field to be set
     */
    public void setField(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid string for Field: " + s);
        }
        this.set("FIELD", s);
    }

    /**
     * Sets the value of the field to be set, to <code>s</code>
     * 
     * @param s The name of the asset subtype
     */
    public void setValue(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid string for Value: " + s);
        }
        this.set("VALUE", s);
    }

}
