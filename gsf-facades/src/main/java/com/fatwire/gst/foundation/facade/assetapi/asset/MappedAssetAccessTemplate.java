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

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.query.Query;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetRelationTreeUtils;

/**
 * @author Dolf Dijkstra
 * 
 */
public class MappedAssetAccessTemplate<T> extends AssetAccessTemplate {
    protected final AssetMapper<T> mapper;
    protected final ICS ics;

    /**
     * @param ics Content Server context object
     */
    public MappedAssetAccessTemplate(ICS ics, AssetMapper<T> mapper) {
        super(ics);
        this.ics = ics;
        this.mapper = mapper;

    }

    /**
     * @param id asset id
     * @return the mapped object
     */
    public T read(final AssetId id) {
        return this.readAsset(id, mapper);
    }

    /**
     * Reads the attributes of an asset.
     * 
     * @param id asset id
     * @param attributes
     * @return the mapped object
     */
    public T read(final AssetId id, final String... attributes) {
        return this.readAsset(id, mapper, attributes);
    }

    /**
     * Reads the associated assets of an asset and returns them as a
     * ScatteredAsset. This takes care of the asset read operation of the
     * associated assets.
     * 
     * @param id asset id
     * @param associationType
     * @return the assets from the associations.
     */
    public Collection<T> readAssociatedAssets(final AssetId id, final String associationType) {
        final Collection<AssetId> list = readAssociatedAssetIds(id, associationType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        final List<T> l = new LinkedList<T>();
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
     * Read the assets that are parents of the current asset given the specified
     * association name or names.  Note this can result in an unknown dependency
     * @param id the child asset
     * @param associationName the association name or names to be used to filter parent query.
     * @return collection of parents; never null.
     */
    public Collection<AssetId> readParentAssetIds(final AssetId id, final String ... associationName) {
        return AssetRelationTreeUtils.getParents(ics, id, associationName);
    }

    /**
     * Read the assets that are parents of the current asset given the specified
     * association name or names.  Note this can result in an unknown dependency
     * @param associationName the association name or names to be used to filter parent query.
     * @return collection of parents; never null.
     */
    public Collection<AssetId> readParentAssetIds(final String ... associationName) {
        return readParentAssetIds(currentId(), associationName);
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
    public Collection<T> readAssociatedAssets(final AssetId id, final String associationType,
            final String... attributes) {
        final List<AssetId> list = this.readAsset(id).getAssociatedAssets(associationType);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        final List<T> l = new LinkedList<T>();
        for (final AssetId child : list) {
            l.add(read(child, attributes));
        }
        return l;

    }

    /**
     * Reads the asset attributes of the asset identified by the current c and
     * cid variables on the ics scope.
     * 
     * @return the mapped object
     */
    public T readCurrent() {
        final AssetId id = currentId();
        return this.read(id);
    }

    public AssetId currentId() {
        return AssetIdUtils.currentId(ics);
    }

    /**
     * Reads the mentioned asset attributes of the asset identified by the
     * current c and cid variables on the ics scope.
     * 
     * @param attributes
     * @return the mapped object
     */
    public T readCurrent(final String... attributes) {
        final AssetId id = currentId();
        return this.readAsset(id, mapper, attributes);
    }

    /**
     * Queries the asset repository with the provided query.
     * 
     * @param query
     * @return Iterable of mapped objects
     */
    public Iterable<T> query(final Query query) {
        return readAssets(query, mapper);
    }

    /**
     * Queries for a list of scattered assets.
     * <p>
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
     * @return a Iterable of mapped objects
     */
    public Iterable<T> query(final String assetType, final String subType, final String query) {
        return query(assetType, subType, query, mapper);
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
     * @return a Iterable of mapped objects
     */
    public Iterable<T> query(final String assetType, final String subType, final String query,
            final String... attributes) {
        return query(assetType, subType, query, mapper, attributes);
    }

}
