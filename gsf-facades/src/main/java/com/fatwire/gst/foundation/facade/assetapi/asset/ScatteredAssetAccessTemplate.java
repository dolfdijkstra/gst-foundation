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
import com.fatwire.assetapi.query.Query;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;

import org.apache.commons.lang.StringUtils;

/**
 * This class provides simple access to AssetData. It is intended to be a
 * one-stop-shop for all read operations on assets.
 * <p/>
 * In many cases it returns a {@link ScatteredAsset} for easy access to
 * AssetData.
 * <p/>
 * The object has the same lifecycle as the ICS object, one per request.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class ScatteredAssetAccessTemplate extends AssetAccessTemplate {
    private final ICS ics;
    /**
     * The asset mapper that returned a ScatteredAsset.
     */
    private final AssetMapper<ScatteredAsset> mapper = new AssetMapper<ScatteredAsset>() {

        public ScatteredAsset map(final AssetData assetData) {
            return new ScatteredAsset(assetData);
        }

    };

    /**
     * @param ics
     */
    public ScatteredAssetAccessTemplate(final ICS ics) {
        super(ics);
        this.ics = ics;
    }

    /**
     * @param id
     * @return
     */
    public ScatteredAsset read(final AssetId id) {
        return this.readAsset(id, mapper);
    }

    /**
     * Reads the attributes of an asset.
     * 
     * @param id
     * @param attributes
     * @return
     */
    public ScatteredAsset read(final AssetId id, final String... attributes) {
        return this.readAsset(id, mapper, attributes);
    }

    /**
     * Reads the associated assets of an asset and returns them as a
     * ScatteredAsset. This takes care of the asset read operation of the
     * associated assets.
     * 
     * @param id
     * @param associationType
     * @return the assets from the associations.
     */
    public Collection<ScatteredAsset> readAssociatedAssets(final AssetId id, final String associationType) {
        final Collection<AssetId> list = readAssociatedAssetIds(id, associationType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ScatteredAsset> l = new LinkedList<ScatteredAsset>();
        for (final AssetId child : list) {
            l.add(read(child));
        }
        return l;

    }

    /**
     * Reads the list of associated assets based on the current c and cid cs
     * variables.
     * 
     * @param associationType
     * @return the assets from the associations.
     */
    public Collection<AssetId> readAssociatedAssetIds(final String associationType) {
        return readAssociatedAssetIds(currentId(), associationType);
    }

    /**
     * Reads the associated assets of an asset and returns them as a
     * ScatteredAsset. This takes care of the asset read operation of the
     * associated assets. The returned ScatteredAssets are only loaded with the
     * mentioned attributes.
     * 
     * @param id the parent asset
     * @param associationType the name of the association or '-' for an unnamed
     *            association
     * @param attributes the list of attributes to load
     * @return the assets from the associations.
     */
    public Collection<ScatteredAsset> readAssociatedAssets(final AssetId id, final String associationType,
            final String... attributes) {
        final List<AssetId> list = this.readAsset(id).getAssociatedAssets(associationType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ScatteredAsset> l = new LinkedList<ScatteredAsset>();
        for (final AssetId child : list) {
            l.add(read(child, attributes));
        }
        return l;

    }

    /**
     * Reads the asset attributes of the asset identified by the current c and
     * cid variables on the ics scope.
     * 
     * @param attributes
     * @return the ScatteredAsset
     */
    public ScatteredAsset readCurrent() {
        final AssetId id = currentId();
        return this.read(id);
    }

    public AssetId currentId() {
        final String c = ics.GetVar("c");
        final String cid = ics.GetVar("cid");
        if (StringUtils.isBlank(c) || StringUtils.isBlank(cid)) {
            throw new IllegalStateException("c or cid is not set as a ics variable.");
        }
        final AssetId id = createAssetId(c, cid);
        return id;
    }

    /**
     * Reads the mentioned asset attributes of the asset identified by the
     * current c and cid variables on the ics scope.
     * 
     * @param attributes
     * @return the ScatteredAsset
     */
    public ScatteredAsset readCurrent(final String... attributes) {
        final AssetId id = currentId();
        return this.readAsset(id, mapper, attributes);
    }

    /**
     * Queries the asset repository with the provided query.
     * 
     * @param query
     * @return
     */
    public Iterable<ScatteredAsset> query(final Query query) {
        return readAssets(query, mapper);
    }

    /**
     * Queries for a list of scattered assets.
     * <p/>
     * Sample queries are:
     * <ul>
     * <li>name='foo'</li>
     * <li>name = 'foo'</li>
     * <li>name = foo</li>
     * <li>name= 'foo bar'</li>
     * <li>size=[1,2]</li>
     * <li>size{10,250}</li>
     * <li>name!='foo'</li>
     * 
     * @param query
     * @return a list of scattered assets.
     */
    public Iterable<ScatteredAsset> query(final String assetType, final String subType, final String query) {
        return query(assetType,subType,query, mapper);
    }

    /**
     * 
     * Queries for a list of scattered assets. Only the mentioned attributes are
     * returned.
     * 
     * @param assetType
     * @param subType
     * @param query
     * @param attributes
     * @return
     */
    public Iterable<ScatteredAsset> query(final String assetType, final String subType, final String query,
            final String... attributes) {
        return query(assetType,subType,query, mapper,attributes);
    }

}
