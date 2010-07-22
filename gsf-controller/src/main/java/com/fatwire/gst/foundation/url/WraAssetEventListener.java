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

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.core.service.ICSLocatorSupport;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.openmarket.basic.event.AbstractAssetEventListener;

import static com.fatwire.gst.foundation.facade.sql.SqlHelper.quote;

/**
 * Asset event for ensuring that a WRA is properly prepared for rendering.  Includes ensuring that
 * the asset is accessible through the WraPathTranslationService, among other things.
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public final class WraAssetEventListener extends AbstractAssetEventListener {

    @Override
    public void assetAdded(AssetId assetId) {
        getService().addAsset(assetId);
    }

    @Override
    public void assetUpdated(AssetId assetId) {
        getService().updateAsset(assetId);
    }

    @Override
    public void assetDeleted(AssetId assetId) {
        getService().deleteAsset(assetId);
    }

    private WraPathTranslationService getService() {
        return WraPathTranslationServiceFactory.getService(null);
    }

    private static final String REGISTRY_TABLE = "AssetListener_reg";

    /**
     * Install self into AssetListener_reg table
     */
    public void install() {
        ICS ics = new ICSLocatorSupport().getICS();
        String id = ics.genID(false);
        String listener = WraAssetEventListener.class.getName();
        String blocking = "Y";
        SqlHelper.execute(ics, REGISTRY_TABLE, "delete from " + REGISTRY_TABLE + " where listener = " + quote(listener));
        SqlHelper.execute(ics, REGISTRY_TABLE, "insert into " + REGISTRY_TABLE + " (id, listener, blocking) VALUES (" + quote(id) + "," + quote(listener) + "," + quote(blocking) + ")");
    }
}
