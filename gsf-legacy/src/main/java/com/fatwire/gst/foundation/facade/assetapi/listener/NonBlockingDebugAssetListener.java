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
import com.openmarket.basic.event.AbstractAssetEventListener;

/**
 * 
 * @deprecated, incorrect use of ICS object, no replacement. All listeners using
 *              ICS should be blocking.
 */
public class NonBlockingDebugAssetListener extends AbstractAssetEventListener {
	protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.legacy.facade.assetapi.listener.NonBlockingDebugAssetListener");
	private ICS ics;

	void printAsset(final AssetId id) {
		throw new IllegalStateException(
				"Cannot be used. All access to ICS should be from blocking listeners.");

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

	}

	protected ICS getICS() {
		return ics;
	}

	@Override
	public void init(ICS ics) {
		this.ics = ics;

	}
}
