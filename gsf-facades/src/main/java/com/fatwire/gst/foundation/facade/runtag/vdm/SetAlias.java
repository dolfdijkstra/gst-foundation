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
 * <VDM.SETALIAS
      KEY="keyvalue"
      VALUE="aliasvalue"/>
 *
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetAlias extends AbstractTagRunner
{
    public SetAlias() { super("VDM.SETALIAS"); }
    public SetAlias(String key, String value) {
        this();
        setKey(key);
        setValue(value);
    }
    public void setKey(String key) { set("KEY", key); }
    public void setValue(String value) { set("VALUE", value);}
}
