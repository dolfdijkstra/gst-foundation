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

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.basic.event.AbstractAssetEventListener;

/**
 * Cache manager to be used to deal with cache updates
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class CacheMgrTaggedAssetEventListener extends AbstractAssetEventListener {

    private final AssetTaggingService svc;

    public CacheMgrTaggedAssetEventListener() {
        try {
            svc = AssetTaggingServiceFactory.getService(Factory.newCS());
        } catch (Exception e) {
            throw new IllegalStateException("Could not create ICS", e);
        }
    }

    @Override
    public void assetAdded(AssetId assetId) {
        svc.clearCacheForTag(svc.getTags(assetId));
    }

    @Override
    public void assetUpdated(AssetId assetId) {
        svc.clearCacheForTag(svc.getTags(assetId));
    }

    @Override
    public void assetDeleted(AssetId assetId) {
        svc.clearCacheForTag(svc.getTags(assetId));
    }
}
