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

import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.openmarket.basic.event.AbstractAssetEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.fatwire.gst.foundation.facade.sql.SqlHelper.quote;

/**
 * Cache manager to be used to deal with cache updates
 * 
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class CacheMgrTaggedAssetEventListener extends AbstractAssetEventListener {

    private static final Log LOG = LogFactory
            .getLog("com.fatwire.gst.foundation.logging.CacheMgrTaggedAssetEventListener");

    private final AssetTaggingService svc;

    public CacheMgrTaggedAssetEventListener() {
        try {
            svc = AssetTaggingServiceFactory.getService(Factory.newCS());
        } catch (Exception e) {
            throw new IllegalStateException("Could not create ICS", e);
        }
    }

    @Override
    public void assetAdded(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetAdded event for " + assetId);
        }
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

    @Override
    public void assetUpdated(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetUpdated event for " + assetId);
        }
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

    @Override
    public void assetDeleted(AssetId assetId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Heard assetDeleted event for " + assetId);
        }
        if (svc.isTagged(assetId)) {
            Collection<Tag> tags = svc.getTags(assetId);
            svc.clearCacheForTag(tags);
        }
    }

    /**
     * Install self into AssetListener_reg table
     */
    public void install() {
        ICS ics = ICSFactory.newICS();
        String id = ics.genID(false);
        String listener = CacheMgrTaggedAssetEventListener.class.getName();
        String blocking = "Y";
        SqlHelper
                .execute(ics, "AssetListener_reg", "delete from AssetListener_reg where listener = " + quote(listener));
        SqlHelper.execute(ics, "AssetListener_reg", "insert into AssetListener_reg (id, listener, blocking) VALUES ("
                + quote(id) + "," + quote(listener) + "," + quote(blocking) + ")");
    }
}
