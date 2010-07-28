/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.tagging;

import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.basic.event.AbstractAssetEventListener;

/**
 * Sends requests to the tagging service.
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class TaggedAssetEventListener extends AbstractAssetEventListener {

    @Override
    public void assetAdded(AssetId assetId) {
        AssetTaggingServiceFactory.getService(getICS()).addAsset(assetId);
    }

    @Override
    public void assetUpdated(AssetId assetId) {
        AssetTaggingServiceFactory.getService(getICS()).updateAsset(assetId);
    }

    @Override
    public void assetDeleted(AssetId assetId) {
        AssetTaggingServiceFactory.getService(getICS()).deleteAsset(assetId);
    }

    private ICS getICS() {
        try {
            return Factory.newCS();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create ICS", e);
        }
    }
}
