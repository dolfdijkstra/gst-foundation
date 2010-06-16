/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.vdm;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the VDM.GETSCALAR xml tag
 * @author Tony Field and Mike Field
 * @since June 9, 2008
 */
public final class GetScalar extends AbstractTagRunner
{
    public GetScalar(String attribute, String varname)
    {
        this();
        setAttribute(attribute);
        setVarname(varname);
    }
    // Default Constructor
    public GetScalar()
    { super ("VDM.GETSCALAR"); }

    /**
     * Sets attribute to the value of <code>s</code>
     * @param s The name of the attribute
     */
    public void setAttribute(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid attribute string: "+s);
        }
        this.set("ATTRIBUTE", s);
    }

    /**
     * Sets the varname to the value of <code>s</code>
     * @param s The name of the varname
     */
    public void setVarname(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid varname string: "+s);
        }
        this.set("VARNAME", s);
    }



}
