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

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.install.AssetListenerInstall;
import com.openmarket.basic.event.AbstractAssetEventListener;

public class BlockingDebugAssetListener extends AbstractAssetEventListener {

	protected static final Logger LOG = LoggerFactory.getLogger(BlockingDebugAssetListener.class);
	private ICS ics;

	void printAsset(final AssetId id) {
		if (LOG.isDebugEnabled()) {

			final ICS ics = getICS();// ;
			final AssetData ad = AssetDataUtils.getAssetData(ics, id);
			try {
				LOG.debug("Print asset with current ICS " + id);
				LOG.debug(DebugHelper.printAsset(ad));
			} catch (final AssetAccessException e) {
				LOG.error("Exception thrown whilst accessing asset " + id, e);
			}
		}

	}

	protected ICS getICS() {
		if (ics == null)
			throw new IllegalStateException(
					"ICS cannot be null. The listener needs to be configured as blocking");
		return ics;
	}

	@Override
	public void assetAdded(final AssetId id) {
		LOG.debug("Asset added " + id);
		printAsset(id);
	}

	@Override
	public void assetDeleted(final AssetId id) {
		LOG.debug("Asset deleted " + id);
		printAsset(id);

	}

	@Override
	public void assetUpdated(final AssetId id) {
		LOG.debug("Asset updated " + id);
		printAsset(id);

	}

	public void install(final ICS ics) {
		AssetListenerInstall.register(ics,
				BlockingDebugAssetListener.class.getName(), true);
	}

	@Override
	public void init(ICS ics) {
		this.ics = ics;

	}

}
