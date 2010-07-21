/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.url;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.core.service.ICSLocatorSupport;

/**
 * Used to instantiate path translation services.  Probably should get replaced by DI at some point.
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public final class WraPathTranslationServiceFactory {

    /**
     * Return a new instance of the WraPathTranslationService.
     *
     * @param ics context, if available. Null is allowed
     * @return service
     */
    public static WraPathTranslationService getService(ICS ics) {
        return new UrlRegistry(ics == null ? new ICSLocatorSupport().getICS() : ics);
    }
}
