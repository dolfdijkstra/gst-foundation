/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.assetapi.asset;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;

/**
 * @author Dolf Dijkstra
 * 
 */
public class ScatteredAssetAccessTemplate extends AssetAccessTemplate {
    private ICS ics;
    private AssetMapper<ScatteredAsset> mapper = new AssetMapper<ScatteredAsset>() {

        public ScatteredAsset map(AssetData assetData) {
            return new ScatteredAsset(assetData);
        }

    };

    /**
     * @param ics
     */
    public ScatteredAssetAccessTemplate(ICS ics) {
        super(ics);
        this.ics = ics;
    }

    /**
     * @param id
     * @return
     */
    public ScatteredAsset read(AssetId id) {
        return this.readAsset(id, mapper);
    }

    /**
     * @param attributes
     * @return the ScatteredAsset
     */
    public ScatteredAsset readCurrent() {
        AssetId id = createAssetId(ics.GetVar("c"), ics.GetVar("cid"));
        return this.read(id);
    }

    /**
     * @param attributes
     * @return the ScatteredAsset
     */
    public ScatteredAsset readCurrent(String... attributes) {
        AssetId id = createAssetId(ics.GetVar("c"), ics.GetVar("cid"));
        return this.readAsset(id, mapper, attributes);
    }

}
