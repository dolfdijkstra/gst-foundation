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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
     * @param id
     * @param attributes
     * @return
     */
    public ScatteredAsset read(AssetId id, String... attributes) {
        return this.readAsset(id, mapper, attributes);
    }

    /**
     * @param id
     * @param associationType
     * @return the assets from the associations.
     */
    public Collection<ScatteredAsset> readAssociatedAssets(AssetId id, String associationType) {
        List<AssetId> list = this.readAsset(id).getAssociatedAssets(associationType);
        if (list == null || list.isEmpty())
            return Collections.emptyList();
        List<ScatteredAsset> l = new LinkedList<ScatteredAsset>();
        for (AssetId child : list) {
            l.add(read(child));
        }
        return l;

    }

    /**
     * @param id the parent asset
     * @param associationType the name of the association or '-' for an unnamed
     *            association
     * @param attributes the list of attributes to load
     * @return the assets from the associations.
     */
    public Collection<ScatteredAsset> readAssociatedAssets(AssetId id, String associationType, String... attributes) {
        List<AssetId> list = this.readAsset(id).getAssociatedAssets(associationType);
        if (list == null || list.isEmpty())
            return Collections.emptyList();
        List<ScatteredAsset> l = new LinkedList<ScatteredAsset>();
        for (AssetId child : list) {
            l.add(read(child, attributes));
        }
        return l;

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
