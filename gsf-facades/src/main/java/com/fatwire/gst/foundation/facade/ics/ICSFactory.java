/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.ics;

import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.ICS;

/**
 * Factory class for creating new ICS instances.  This process is very expensive so use it sparingly.
 *
 * @author Tony Field
 * @since Jul 29, 2010
 */
public final class ICSFactory {

    public static final ICS newInstance() {
        try {
            return Factory.newCS();
        } catch (Exception e) {
            throw new RuntimeException("Could not create new ICS instance: " + e, e);
        }
    }
}
