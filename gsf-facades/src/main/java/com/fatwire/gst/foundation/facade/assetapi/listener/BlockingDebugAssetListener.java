/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.facade.assetapi.listener;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.facade.install.AssetListenerInstall;
import com.openmarket.basic.event.AbstractAssetEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BlockingDebugAssetListener extends AbstractAssetEventListener {
    private static final Log LOG = LogFactory.getLog(BlockingDebugAssetListener.class.getPackage().getName());

    void printAsset(AssetId id) {
        if (LOG.isDebugEnabled()) {
            ICS ics = ICSFactory.getOrCreateICS();
            AssetData ad = AssetDataUtils.getAssetData(ics, id);
            try {
                LOG.debug("Print asset with current ICS " + id);
                LOG.debug(DebugHelper.printAsset(ad));
            } catch (AssetAccessException e) {
                LOG.error(e);
            }
        }

    }

    @Override
    public void assetAdded(AssetId id) {
        LOG.debug("Asset added " + id);
        printAsset(id);
    }

    @Override
    public void assetDeleted(AssetId id) {
        LOG.debug("Asset deleted " + id);
        printAsset(id);

    }

    @Override
    public void assetUpdated(AssetId id) {
        LOG.debug("Asset updated " + id);
        printAsset(id);

    }

    public void install(ICS ics) {
        AssetListenerInstall.register(ics, BlockingDebugAssetListener.class.getName(), true);
    }

}
