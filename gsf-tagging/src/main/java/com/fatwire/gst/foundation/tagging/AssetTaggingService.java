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
     * Clear any cached items associated with the tag.
     *
     * @param tags tags
     */
    void clearCacheForTag(Collection<Tag> tags);

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

}
