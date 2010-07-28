/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.tagging;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.tagging.db.TableTaggingServiceImpl;

/**
 * TODO: Add class/interface details
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class AssetTaggingServiceFactory {
    public static AssetTaggingService getService(ICS ics) {
        return new TableTaggingServiceImpl(ics);
    }
}
