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
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;

/**
 * This class provides simple access to AssetData. It is intended to be a
 * one-stop-shop for all read operations on assets.
 * <p>
 * In many cases it returns a {@link ScatteredAsset} for easy access to
 * AssetData.
 * <p>
 * The object has the same lifecycle as the ICS object, one per request.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class ScatteredAssetAccessTemplate extends MappedAssetAccessTemplate<ScatteredAsset> {
    /**
     * The asset mapper that returned a ScatteredAsset.
     */
    private static final AssetMapper<ScatteredAsset> mapper_ = new AssetMapper<ScatteredAsset>() {

        public ScatteredAsset map(final AssetData assetData) {
            return new ScatteredAsset(assetData);
        }
    };

    /**
     * @param ics Content Server context object
     */
    public ScatteredAssetAccessTemplate(final ICS ics) {
        super(ics, mapper_);

    }

}
