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
package com.fatwire.gst.foundation.taglib;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiAliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class WRAUtils {
    private final Log LOG = LogFactory.getLog(WRAUtils.class);
    private WraCoreFieldDao wraDao;
    private AliasCoreFieldDao aliasDao;

    public WRAUtils(ICS ics) {
        wraDao = new AssetApiWraCoreFieldDao(ics);
        aliasDao = new AssetApiAliasCoreFieldDao(ics,wraDao);
    }

    /**
     * @param id id of web-referenceable asset
     * @return WebReferenceableAsset bean containing fields for Web-Referencable asset
     */
    public WebReferenceableAsset getWra(AssetId id) {
    	try {
    		return wraDao.getWra(id);
    	} catch (RuntimeException e) {
    		LOG.warn("Asset " + id + " is not a web-referenceable asset.");
    		return null;
    	}
    }

    /**
     * @param id id of alias asset
     * @return Alias bean containing fields for Alias asset
     */
    public Alias getAlias(AssetId id) {
    	try {
    		return aliasDao.getAlias(id);
    	} catch (RuntimeException e) {
    		LOG.warn("Asset " + id + " is not an alias asset.");
    		return null;
    	}
    }
}
