/*
 * Copyright 2009 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.test.event;

import COM.FutureTense.Interfaces.ICS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.basic.event.AbstractAssetEventListener;


/**
 * This class is a trivial asset event listener that reports that an asset event
 * has been heard. This is used to test the asset event system.
 * 
 * @author Tony Field
 * @since 2011-03-28
 * @deprecated - moved to new namespace
 * @see "tools.gsf.test.event.VerySimpleAssetEventListener"
 */
public final class VerySimpleAssetEventListener extends AbstractAssetEventListener {

	protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.test.event.VerySimpleAssetEventListener");

    @Override
    public void assetAdded(AssetId assetId) {
        LOG.info("Heard assetAdded event for " + assetId);
    }

    @Override
    public void assetUpdated(AssetId assetId) {
        LOG.info("Heard assetUpdated event for " + assetId);
    }

    @Override
    public void assetDeleted(AssetId assetId) {
        LOG.info("Heard assetDeleted event for " + assetId);
    }

    @Override
    public void init(ICS arg0) {
        LOG.info("init " + (arg0 == null ? " without ICS arg." : ""));

    }
}
