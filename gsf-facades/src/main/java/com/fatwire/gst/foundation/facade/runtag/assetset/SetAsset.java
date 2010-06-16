/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.assetset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the ASSETSET.SETASSET xml tag
 * @author Mike Field
 * @since July 15, 2008
 */
public final class SetAsset extends AbstractTagRunner
{
    // Default Constructor
    public SetAsset()
    { super ("ASSETSET.SETASSET"); }

    /**
     * Sets name to the value of <code>s</code>
     * @param s The name of the assetset to return
     */
    public void setName(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid name string: "+s);
        }
        this.set("NAME", s);
    }

    /**
     * Sets type to the value of <code>s</code>
     * @param s The asset's type
     */
    public void setType(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid type string: "+s);
        }
        this.set("TYPE", s);
    }

    /**
     * Sets id to the value of <code>s</code>
     * @param s The id of the asset
     */
    public void setId(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid Id string: "+s);
        }
        this.set("ID", s);
    }

    /**
     * Sets locale to the value of <code>s</code>
     * @param s The locale of the asset
     */
    public void setLocale(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid locale string: "+s);
        }
        this.set("LOCALE", s);
    }

    /**
     * Sets deptype to the value of <code>s</code>
     * @param s The id of the deptype
     */
    public void setDeptype(String s)
    {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("exact") && !s.equals("exists") && !s.equals("none"))
        {
            throw new IllegalArgumentException("Invalid deptype string: "+s);
        }
        this.set("DEPTYPE", s);
    }

}
