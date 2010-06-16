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
 * Wrapper around the ASSET.CREATE tag
 * @author Mike Field
 * @since Jun 11, 2008
 */
public final class AssetCreate extends AbstractTagRunner
{

    // Default Constructor
    public AssetCreate()
    { super ("ASSET.CREATE"); }

    /**
     * Sets name to the value of <code>s</code>
     * @param s The name of the asset instance
     */
    public void setName(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid string for Name: "+s);
        }
        this.set("NAME", s);
    }

    /**
     * Sets the type to the asset to <code>s</code>
     * @param s The name of the asset type
     */
    public void setType(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid string for Type: "+s);
        }
        this.set("TYPE", s);
    }



}
