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

import com.fatwire.gst.foundation.facade.runtag.TagRunner;

/**
 * Sets an alias ONLY if it has not been set before. Avoids unnecessary DB
 * thrash.
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetAliasWithoutReset implements TagRunner {

    private final String key;

    private final String value;

    public SetAliasWithoutReset(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String execute(final ICS ics) {
        final String varname = "alias" + ics.genID(true);
        try {
            GetAlias getAlias = new GetAlias(key, varname);
            String getResult = getAlias.execute(ics);
            String alias = ics.GetVar(varname);
            if (alias != null && alias.equals(value) || alias == null && value == null) {
                // nothing to do. this saves a lot of processing
                return getResult;
            }

            SetAlias setAlias = new SetAlias(key, value);
            return setAlias.execute(ics);

        } finally {
            // cleaning up
            ics.RemoveVar(varname);
        }
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

}
