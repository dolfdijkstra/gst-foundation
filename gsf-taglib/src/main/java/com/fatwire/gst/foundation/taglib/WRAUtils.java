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
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 * @deprecated unsafe use, as it is eating exceptions and returning null objects
 */
@Deprecated
public class WRAUtils {
    private final Log LOG = LogFactory.getLog(WRAUtils.class);
    private final WraCoreFieldDao wraDao;
    private final AliasCoreFieldDao aliasDao;

    public WRAUtils(final ICS ics) {
        wraDao = new WraCoreFieldDao(ics);
        aliasDao = new AliasCoreFieldDao(ics, wraDao);
    }

    /**
     * @param id id of web-referenceable asset
     * @return WebReferenceableAsset bean containing fields for Web-Referencable
     *         asset
     * @deprecated
     */
    @Deprecated
    public WebReferenceableAsset getWra(final AssetId id) {
        try {
            return wraDao.getWra(id);
        } catch (final RuntimeException e) {
            LOG.warn("Asset " + id + " is not a web-referenceable asset.");
            LOG.debug("Asset " + id + " is not a web-referenceable asset: " + e, e);
            return null;
        }
    }

    /**
     * @param id id of alias asset
     * @return Alias bean containing fields for Alias asset
     * @deprecated
     */
    @Deprecated
    public Alias getAlias(final AssetId id) {
        try {
            return aliasDao.getAlias(id);
        } catch (final RuntimeException e) {
            LOG.warn("Asset " + id + " is not an alias asset.");
            LOG.debug("Asset " + id + " is not an alias asset: " + e, e);
            return null;
        }
    }
}
