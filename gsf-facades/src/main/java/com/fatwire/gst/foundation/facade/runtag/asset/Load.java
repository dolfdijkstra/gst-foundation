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
 * Wrapper around the ASSET.LOAD xml tag
 * @author Mike Field
 * @since August 15, 2008
 */
public final class Load extends AbstractTagRunner
{
    // Default Constructor
    public Load()
    { super ("ASSET.LOAD"); }

    /**
     * Sets name to the value of <code>s</code>
     * @param s The name of the asset to return
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
     * @param s The type of the asset
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
     * Sets objectid to the value of <code>s</code>
     * @param s The object id of the asset
     */
    public void setObjectId(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid objectid string: "+s);
        }
        this.set("OBJECTID", s);
    }

    /**
     * Sets field to the value of <code>s</code>
     * @param s The field of the asset
     */
    public void setField(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid field string: "+s);
        }
        this.set("FIELD", s);
    }

    /**
     * Sets "value" to the value of <code>s</code>
     * @param s The value of the asset
     */
    public void setValue(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid value string: "+s);
        }
        this.set("VALUE", s);
    }

    /**
     * Sets site to the value of <code>s</code>
     * @param s The site of the asset
     */
    public void setSite(String s)
    {
        // validate first
        if (s == null || s.length() == 0)
        {
            throw new IllegalArgumentException("Invalid site string: "+s);
        }
        this.set("SITE", s);
    }

    /**
     * Sets deptype to the value of <code>s</code>
     * @param s exact, exists, greater or none (case sensitive)
     */
    public void setDepType(String s)
    {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("exact") && !s.equals("exists") && !s.equals("greater") && !s.equals("none"))
        {
            throw new IllegalArgumentException("Invalid escape string: "+s);
        }
        this.set("DEPTYPE", s);
    }


    /**
     * Sets editable to the value of <code>s</code>
     * @param s true or false (case sensitive)
     */
    public void setEditable(String s)
    {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false"))
        {
            throw new IllegalArgumentException("Invalid editable string: "+s);
        }
        this.set("EDITABLE", s);
    }

    /**
     * Sets option to the value of <code>s</code>
     * @param s editable, readonly, readonly_complete (case sensitive)
     */
    public void setOption(String s)
    {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("editable") && !s.equals("readonly") && !s.equals("readonly_complete"))
        {
            throw new IllegalArgumentException("Invalid option string: "+s);
        }
        this.set("OPTION", s);
    }

    /**
     * Sets flushonvoid to the value of <code>s</code>
     * @param s true or false (case sensitive)
     */
    public void setFlushOnVoid(String s)
    {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false"))
        {
            throw new IllegalArgumentException("Invalid flushonvoid string: "+s);
        }
        this.set("FLUSHONVOID", s);
    }
}
