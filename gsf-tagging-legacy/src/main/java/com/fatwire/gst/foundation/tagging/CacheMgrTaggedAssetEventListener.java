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

import java.util.Collection;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.listener.RunOnceAssetEventListener;

/**
 * Cache manager to be used to deal with cache updates
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 28, 2010
 */
public final class CacheMgrTaggedAssetEventListener extends RunOnceAssetEventListener {

    

    private AssetTaggingService getService() {
        return AssetTaggingServiceFactory.getService(getICS());
    }

    @Override
    public void doAssetAdded(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetAdded event for " + assetId);
        }

        AssetTaggingService svc = getService();
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

    @Override
    public void doAssetUpdated(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetUpdated event for " + assetId);
        }
        AssetTaggingService svc = getService();
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

    @Override
    public void doAssetDeleted(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetDeleted event for " + assetId);
        }
        AssetTaggingService svc = getService();
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

 
}
