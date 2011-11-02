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
package com.fatwire.gst.foundation.url;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.listener.RunOnceAssetEventListener;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.url.db.UrlRegistry2;

/**
 * Asset event for ensuring that a WRA is properly prepared for rendering.
 * Includes ensuring that the asset is accessible through the
 * WraPathTranslationService, among other things.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 21, 2010
 */
public final class WraAssetEventListener extends RunOnceAssetEventListener {

    @Override
    protected void doAssetAdded(final AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetAdded event for " + assetId);
        }
        getService().addAsset(assetId);
    }

    @Override
    protected void doAssetUpdated(final AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetUpdated event for " + assetId);
        }
        getService().updateAsset(assetId);
    }

    @Override
    protected void doAssetDeleted(final AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetDeleted event for " + assetId);
        }
        getService().deleteAsset(assetId);
    }

    private WraPathTranslationService getService() {
        final ICS ics = ICSFactory.getOrCreateICS();
        // return new UrlRegistry(ics,
        // WraCoreFieldApiBypassDao.getBackdoorInstance(ics), new
        // VirtualWebrootApiBypassDao(ics));
        return UrlRegistry2.lookup(ics);
    }

}
