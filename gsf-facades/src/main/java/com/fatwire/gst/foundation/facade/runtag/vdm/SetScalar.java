/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.vdm;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftStatusCode;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <VDM.SETSCALAR
 * ATTRIBUTE="attribute"
 * VALUE="value"/>
 *
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetScalar extends AbstractTagRunner
{

    public SetScalar(String attribute, String value)
    {
        this();
        setAttribute(attribute);
        setValue(value);
    }

    public SetScalar() { super("VDM.SETSCALAR"); }

    public void setAttribute(String attr) { set("ATTRIBUTE", attr); }

    public void setValue(String val) { set("VALUE", val); }

    public String execute(ICS ics)
    {
        String s = super.execute(ics);

        // Update from Tony Field September 19, 2009
        // for some oddball reason, setscalar can fail with a database error (unique constraint violation)
        // and the tag does not report errno.  However it does return a status code in a variable called
        // cshttp.  We can look into this variable to get the code, parse it, and extract errno from it.
        // Wow this is nasty...

        String statusCode = ics.GetVar("cshttp");
        ftStatusCode sc = new ftStatusCode();
        if(sc.setFromData(statusCode))
        {
            int errno = sc.getErrorID();
            switch(errno)
            {
                case ftErrors.success:
                    return s;
                case ftErrors.dberror:
                {
                    throw new CSRuntimeException("SetScalar failed with a database error.  It was returned in a ftStatusCode.  StatusCode:" + statusCode, -13704);
                }
                default:
                {
                    throw new CSRuntimeException("SetScalar failed with an unexpected error.  It was returned in a ftStatusCode.  StatusCode:" + statusCode, -13704);
                }
            }
        }
        return s;
    }
}
