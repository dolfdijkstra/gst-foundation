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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.install.AssetListenerInstall;
import com.openmarket.basic.event.AbstractAssetEventListener;


public class EventPrintAssetListener extends AbstractAssetEventListener {
	protected static final Logger LOG = LoggerFactory.getLogger(EventPrintAssetListener.class);

    @Override
    public void assetAdded(final AssetId id) {
        LOG.info("Asset added " + id + " (" + Thread.currentThread().getName() + ")");
    }

    @Override
    public void assetDeleted(final AssetId id) {
        LOG.info("Asset deleted " + id + " (" + Thread.currentThread().getName() + ")");
    }

    @Override
    public void assetUpdated(final AssetId id) {
        LOG.info("Asset updated " + id + " (" + Thread.currentThread().getName() + ")");
    }

    public void install(final ICS ics) {
        AssetListenerInstall.register(ics, EventPrintAssetListener.class.getName(), true);
    }

    @Override
    public void init(ICS arg0) {
        // TODO Auto-generated method stub
        
    }

}
