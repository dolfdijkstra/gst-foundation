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
package com.fatwire.gst.foundation.url.db;

import java.util.List;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.wra.SimpleWra;

/**
 * @author Dolf Dijkstra
 * @since November 1, 2011
 * 
 */
public interface UrlRegistryDao {

    public List<VanityUrl> resolveAsset(final String virtual_webroot, final String url_path);

    public void add(SimpleWra wra, VirtualWebroot vw, String site);

    public void update(VanityUrl url);

    public VanityUrl read(AssetId id);

    public void delete(AssetId id);

}
