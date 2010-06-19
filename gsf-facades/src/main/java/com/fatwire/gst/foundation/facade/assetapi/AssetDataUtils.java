/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.assetapi;

import java.util.Arrays;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * Convenient shortcuts for working with AssetData objects
 * 
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class AssetDataUtils {
    private AssetDataUtils() {
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param c
     * @param cid
     * @param attributes
     * @return asset data
     */
    public static AssetData getAssetData(String c, String cid, String... attributes) {
        return getAssetData(new AssetIdImpl(c, Long.valueOf(cid)), attributes);
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param id
     * @param attributes
     * @return asset data
     */
    public static AssetData getAssetData(AssetId id, String... attributes) {
        Session ses = SessionFactory.getSession();
        AssetDataManager mgr = (AssetDataManager) ses.getManager(AssetDataManager.class.getName());
        try {
            return mgr.readAttributes(id, Arrays.asList(attributes));
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data", ftErrors.exceptionerr, e);
        }
    }
}
