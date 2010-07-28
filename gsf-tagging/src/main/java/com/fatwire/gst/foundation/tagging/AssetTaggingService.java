/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.tagging;

import java.util.Collection;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;

/**
 * Provides core tagging support systems.  Note that add, update, and delete methods are smart enough to not
 * fail if passed a non-tagged asset.
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public interface AssetTaggingService {

    /**
     * Bootstrap thyself
     */
    void install();

    /**
     * Look up the tags for an asset. Returns an empty list if none or set or if the asset does not have a
     * tag attribute or field
     *
     * @param id asset id
     * @return collection of tags. Never null
     */
    Collection<Tag> getTags(AssetId id);

    /**
     * Clear any cached items associated with the tag.
     *
     * @param tags tags
     */
    void clearCacheForTag(ICS ics, Collection<Tag> tags);

    /**
     * Record the specified tag as a dependency on the current pagelet
     *
     * @param ics ICS context
     * @param tag tag
     */
    void recordCacheDependency(ICS ics, Tag tag);

    /**
     * Handle adding a tagged asset.  If the asset is not tagged, nothing happens.
     *
     * @param id asset with tag
     */
    void addAsset(AssetId id);

    /**
     * Handle updating tagged asset.  If the asset is not tagged, nothing happens.
     *
     * @param id asset with tag
     */
    void updateAsset(AssetId id);

    /**
     * Handle deleting tagged asset.  If the asset is not tagged, nothing happens.
     *
     * @param id tagged asset
     */
    void deleteAsset(AssetId id);

    /**
     * Return a collection of assets that are tagged with the specified tag.
     *
     * @param tag tag to use to look up assets
     * @return collection of assets that have the specified tag set.  May return an empty list; never returns null.
     */
    Collection<AssetId> lookupTaggedAssets(Tag tag);
}
