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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <VDM.GETALIAS KEY="keyvalue" VARNAME="varname"/>
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class GetAlias extends AbstractTagRunner {

    protected static final Log log = LogFactory.getLog(GetAlias.class);

    public GetAlias() {
        super("VDM.GETALIAS");
    }

    public GetAlias(String key, String varname) {
        this();
        setKey(key);
        setVarname(varname);
    }

    public void setKey(String key) {
        set("KEY", key);
    }

    public void setVarname(String varname) {
        set("VARNAME", varname);
    }
}
