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
package com.fatwire.gst.foundation.vwebroot;

import java.util.SortedSet;

import com.fatwire.gst.foundation.wra.VanityAsset;



/**
 * DAO for Virtual Webroot table
 * @author Dolf Dijkstra
 *
 *
 * @deprecated as of release 12.x, replace with WCS 12c's native vanity URLs support.
 * 
 */
@Deprecated
public interface VirtualWebrootDao {

    VirtualWebroot lookupVirtualWebrootForAsset(VanityAsset wra);

    VirtualWebroot lookupVirtualWebrootForUri(String uri);

    /**
     * Get all of the virtual webroots, sorted by URL length.
     *
     * @return list of virtual webroots
     */
    public SortedSet<VirtualWebroot> getAllVirtualWebroots();
}
