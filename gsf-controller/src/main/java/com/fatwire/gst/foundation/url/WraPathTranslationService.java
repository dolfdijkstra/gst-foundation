/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.url;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;

/**
 * Path translation service for going from assetid to WRA path and back
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public interface WraPathTranslationService {

    /**
     * Installs any required components and bootstraps the service.
     */
    void install();

    /**
     * Look up the asset corresponding to the input virtual-0webroot and url-path
     *
     * @param virtual_webroot
     * @param url_path
     * @return asset id and site
     */
    AssetIdWithSite resolveAsset(final String virtual_webroot, final String url_path);

    void addAsset(AssetId id);

    void updateAsset(AssetId id);

    void deleteAsset(AssetId id);

}
