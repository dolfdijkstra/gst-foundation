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
package com.fatwire.gst.foundation.facade.runtag.vdm;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunner;

/**
 * Sets a scalar attribute value without resetting it if it has already been set
 * to the same value.
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetScalarWithoutReset implements TagRunner {

    private final String attribute;

    private final String value;

    public SetScalarWithoutReset(final String attribute, final String value) {
        super();
        this.attribute = attribute;
        this.value = value;
    }

    public String execute(final ICS ics) {
        final String varname = "get_scalar_output_value" + ics.genID(true);
        try {
            ics.RemoveVar(varname);
            GetScalar getScalar = new GetScalar(attribute, varname);
            String getResult = getScalar.execute(ics);
            String attrVal = ics.GetVar(varname);
            if (attrVal != null && attrVal.equals(value) || attrVal == null && value == null) {
                // nothing to do. this saves a lot of processing
                return getResult;
            }

            SetScalar setScalar = new SetScalar(attribute, value);
            return setScalar.execute(ics);
        } finally {
            // cleaning up
            ics.RemoveVar(varname);
        }
    }

    public String getValue() {
        return value;
    }

    public String getAttribute() {
        return attribute;
    }
}
