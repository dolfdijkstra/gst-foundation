/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.mapping;

import java.util.Map;

import com.fatwire.gst.foundation.controller.AssetIdWithSite;

/**
 * Service the read the mappings for an asset.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public interface MappingService {

    /**
     * Reads the mappings for the asset and the site.
     * 
     * @param id the asset that holds the mapping.
     * @return the mappings for the asset.
     */
    Map<String, MappingValue> readMapping(AssetIdWithSite id);

}
