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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.realtime.PageCacheUpdaterImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.fatwire.gst.foundation.tagging.TagUtils.convertTagToCacheDepString;

/**
 * RealTime publishing includes an API entitled RealTime CacheUpdater. We will
 * override the default com.fatwire.realtime.PageCacheUpdaterImpl to override
 * the beforeSelect() method. The flush and regen keys will be extended, to
 * automatically include all pagelets containing the GSTTag attribute value.
 * This ensures that even though parent has not changed, pagelets that reference
 * it are automatically flushed. This ensures that by simply tagging an asset,
 * it automatically and instantly appears in pages that render it.
 * <p/>
 * The GSTTagRegistry table is read for the specific assets before the new
 * values are inserted. This is to make sure that pagelets are also flushed with
 * the old tag values for the cases where the tag is deleted or the values have
 * changed. Implementation examples can be found in the guide Customizing
 * RealTime Publishing Cache Management.
 * 
 * @author Tony Field
 * @since Jul 30, 2010
 */
public final class TaggedAssetRealtimeCacheUpdater extends PageCacheUpdaterImpl {

    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.tagging");

    @Override
    protected void beforeSelect(ICS ics, Collection<String> strings, Collection<String> strings1,
            Collection<AssetId> assetIds) {
        AssetTaggingService svc = AssetTaggingServiceFactory.getService(ics);
        for (Tag tag : svc.getTags(assetIds)) {
            if (LOG.isDebugEnabled())
                LOG.debug("AssetTag found in beforeSelect: " + tag
                        + ". Adding this to the list of compositional dependencies to be flushed.");
            String sTag = convertTagToCacheDepString(tag);
            // todo: ensure this is right.... I THINK it's flushStrings &
            // refreshStrings... and if so, we may not need to refresh.
            strings.add(sTag);
            strings1.add(sTag);
        }
        super.beforeSelect(ics, strings, strings1, assetIds);
    }
}
