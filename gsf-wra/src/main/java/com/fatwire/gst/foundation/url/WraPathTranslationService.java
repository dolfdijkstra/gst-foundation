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
package com.fatwire.gst.foundation.url;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;

/**
 * Path translation service for going from assetid to WRA path and back
 * <p>
 * todo: low priority: for any given partial url, get next children down, and
 * "are they parents too" TODO: low priority: remove asset listener methods from
 * interface, create separate interface for these methods
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 21, 2010
 */
public interface WraPathTranslationService {

    /**
     * Look up the asset corresponding to the input virtual-webroot and url-path
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
