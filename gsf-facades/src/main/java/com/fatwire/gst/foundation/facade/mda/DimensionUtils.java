/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.mda;

import com.fatwire.mda.*;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.system.SessionFactory;
import com.fatwire.system.Session;

import java.util.Collection;

import COM.FutureTense.Interfaces.ICS;

/**
 * Miscellaneous utilities for working with dimensions
 *
 * @author Tony Field
 * @since Jun 8, 2009
 */
public final class DimensionUtils
{
    /**
     * Shorthand function for returning the DimensionableAssetManager
     * given an ICS context.
     *
     * @param ics context
     * @return dimensionable asset manager
     */
    public static DimensionableAssetManager getDAM(ICS ics)
    {
        Session session = SessionFactory.getSession(ics);
        return (DimensionableAssetManager)session.getManager(DimensionableAssetManager.class.getName());
    }

    /**
     * Shorthand function for returning the DimensionManager given
     * an ICS context
     * @param ics context
     * @return Dimension Manager
     */
    public static DimensionManager getDM(ICS ics)
    {
        Session session = SessionFactory.getSession(ics);
        return (DimensionManager) session.getManager(DimensionManager.class.getName());
    }

    /**
     * Return the dimension of the input asset that corresponds to its locale.
     * If the asset does not have a locale set, returns null
     *
     * @param ics context
     * @param id asset
     * @return locale dimension or null
     */
    public static Dimension getLocaleAsDimension(ICS ics, AssetId id)
    {
        Collection<Dimension> dims = getDAM(ics).getDimensionsForAsset(id);
        for(Dimension dim : dims)
        {
            if("locale".equalsIgnoreCase(dim.getGroup()))
            {
                return dim;
            }
        }
        return null;
    }

    /**
     * Get the id of the dimension asset for the name specified
     * @param ics context
     * @param name dimension name, or locale
     * @return dimension id
     */
    public static long getDimensionIdForName(ICS ics, String name)
    {
        return getDM(ics).loadDimension(name).getId().getId();
    }

    /**
     * Shorthand function to get the name given a dimension ID specified.
     * @param ics context
     * @param dimensionid ID of a locale.  Note the dimension group is not verified
     * @return dimension name, or locale name, like en_CA.
     */
    public static String getNameForDimensionId(ICS ics, long dimensionid)
    {
        return getDM(ics).loadDimension(dimensionid).getName();
    }
}
