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
package com.fatwire.gst.foundation.include;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;

/**
 * Service to include Templates, SiteEntries and CSElements.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public interface IncludeService {

    /**
     * Creates a IncludeTemplate and registers this under the name for retrieval
     * in the View layer by that name.
     * 
     * @param name the name of the Include for reference in the View layer
     * @param asset the asset to render.
     * @param tname the template name.
     * @return the created IncludeTemplate
     */
    IncludeTemplate template(String name, AssetId asset, String tname);

    /**
     * Creates a IncludePage and registers this under the name for retrieval in
     * the View layer by that name.
     * 
     * @param name the name of the Include for reference in the View layer.
     * @param pagename the name of the SiteCatalog entry to call.
     * @param style style
     * @return the created IncludePage
     */
    IncludePage page(String name, String pagename, Style style);

    /**
     * Creates a IncludeElement and registers this under the name for retrieval
     * in the View layer by that name.
     * 
     * @param name the name of the Include for reference in the View layer.
     * @param elementname the name of the element to call.
     * @return the created IncludeElement
     */
    IncludeElement element(String name, String elementname);
}
