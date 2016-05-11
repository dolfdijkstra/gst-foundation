/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.foundation.tagging;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.listener.RunOnceAssetEventListener;

/**
 * Sends asset events to the tagging service.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 28, 2010
 */
public final class TaggedAssetEventListener extends RunOnceAssetEventListener {

   
    public TaggedAssetEventListener() {
    }

    AssetTaggingService getService() {
        return AssetTaggingServiceFactory.getService(getICS());
    }

    @Override
    public void doAssetAdded(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetAdded event for " + assetId);
        }
        getService().addAsset(assetId);
    }

    @Override
    public void doAssetUpdated(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetUpdated event for " + assetId);
        }
        getService().updateAsset(assetId);
    }

    @Override
    public void doAssetDeleted(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetDeleted event for " + assetId);
        }
        getService().deleteAsset(assetId);
    }

   

}
