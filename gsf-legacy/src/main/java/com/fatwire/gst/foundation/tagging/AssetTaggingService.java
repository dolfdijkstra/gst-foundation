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

import com.fatwire.assetapi.data.AssetId;

/**
 * Provides core tagging support systems. Note that add, update, and delete
 * methods are smart enough to not fail if passed a non-tagged asset.
 * 
 * @author Tony Field
 * @since Jul 28, 2010
 * 
 * @deprecated as of release 12.x, replaced with WCS 12c's native tagging 
 * 
 */
public interface AssetTaggingService {

    /**
     * Look up the tags for an asset. Returns an empty list if none or set or if
     * the asset does not have a tag attribute or field
     * 
     * @param id asset id
     * @return collection of tags. Never null
     */
    Collection<Tag> getTags(AssetId id);

    /**
     * Get the tags corresponding to a whole collection of AssetIds. There is no
     * way to determine which tags correspond to each of the input asset.
     * 
     * @param ids asset ids, some of which may be tagged
     * @return tags, never null
     */
    Collection<Tag> getTags(Collection<AssetId> ids);

    /**
     * Clear any pagelets containing the specified tags.
     * 
     * @param tags tags
     */
    void clearCacheForTag(Collection<Tag> tags);

    /**
     * Record the specified tag as a dependency on the current pagelet
     * 
     * @param tag tag
     */
    void recordCacheDependency(Tag tag);

    /**
     * Handle adding a tagged asset. If the asset is not tagged, nothing
     * happens.
     * 
     * @param id asset with tag
     */
    void addAsset(AssetId id);

    /**
     * Handle updating tagged asset. If the asset is not tagged, nothing
     * happens.
     * 
     * @param id asset with tag
     */
    void updateAsset(AssetId id);

    /**
     * Handle deleting tagged asset. If the asset is not tagged, nothing
     * happens.
     * 
     * @param id tagged asset
     */
    void deleteAsset(AssetId id);

    /**
     * Return a collection of assets that are tagged with the specified tag.
     * 
     * @param tag tag to use to look up assets
     * @return collection of assets that have the specified tag set. May return
     *         an empty list; never returns null.
     */
    Collection<AssetId> lookupTaggedAssets(Tag tag);

    /**
     * Returns true if an asset is tagged, false otherwise
     * 
     * @param id id of asset
     * @return true if it's tagged, false otherwise
     */
    boolean isTagged(AssetId id);
}
