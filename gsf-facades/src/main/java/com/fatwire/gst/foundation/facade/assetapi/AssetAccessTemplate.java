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

package com.fatwire.gst.foundation.facade.assetapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.Query;
import com.fatwire.assetapi.query.SimpleQuery;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * 
 * Helper class to facilitate {@link AssetDataManager} use in a simplified way
 * in delivery ContentServer templates.
 * <p/>
 * This class is inspired by springframework data access template classes like
 * org.springframework.jdbc.core.JdbcTemplate.
 * <p/>
 * This class is not thread safe and should not be shared between threads.
 * 
 * @author Dolf.Dijkstra
 * @since Nov 23, 2009
 * 
 */
public class AssetAccessTemplate {

    private final Session session;

    /**
     * @param session
     */
    public AssetAccessTemplate(Session session) {
        super();
        if (session == null)
            throw new IllegalArgumentException("session cannot be null.");
        this.session = session;
    }

    /**
     * Constructor that accepts ICS as an argument.
     * 
     * @param ics
     */
    public AssetAccessTemplate(ICS ics) {
        if (ics == null)
            throw new IllegalArgumentException("ics cannot be null.");
        session = SessionFactory.getSession(ics);
    }

    /**
     * Helper method to create an AssetId from c and cid as string values.
     * 
     * @param c
     * @param cid
     * @return
     */
    public AssetId createAssetId(String c, String cid) {
        return new AssetIdImpl(c, Long.parseLong(cid));
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper.
     * 
     * @param <T>
     * @param id
     * @param mapper
     * @return
     */
    public <T> T readAsset(AssetId id, AssetMapper<T> mapper) {
        AssetDataManager m = getAssetDataManager();

        T t = null;
        try {
            Iterable<AssetData> assets = m.read(Arrays.asList(id));
            for (AssetData assetData : assets) {
                t = mapper.map(assetData);
            }
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        return t;
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper.
     * 
     * @param <T>
     * @param c the assetType
     * @param cid the asset id
     * @param mapper
     * @return
     */
    public <T> T readAsset(String c, String cid, AssetMapper<T> mapper) {
        return readAsset(this.createAssetId(c, cid), mapper);
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper.
     * 
     * @param <T>
     * @param c the assetType
     * @param cid the asset id
     * @param mapper
     * @return
     */
    public <T> T readAsset(String c, long cid, AssetMapper<T> mapper) {
        return readAsset(new AssetIdImpl(c, cid), mapper);
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper. Only the
     * list of lister attributes is retrieved from the asset.
     * 
     * @param <T>
     * @param id
     * @param mapper
     * @param attributes
     * @return
     */
    public <T> T readAsset(AssetId id, AssetMapper<T> mapper, String[] attributes) {
        AssetDataManager m = getAssetDataManager();

        T t = null;
        try {
            AssetData asset = m.readAttributes(id, Arrays.asList(attributes));
            t = mapper.map(asset);
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        return t;
    }

    /**
     * Method to read an asset and pass the results to the closure for further
     * handling.
     * 
     * @param id
     * @param closure
     */
    public void readAsset(AssetId id, AssetClosure closure) {
        AssetDataManager m = getAssetDataManager();

        try {
            Iterable<AssetData> assets = m.read(Arrays.asList(id));
            for (AssetData assetData : assets) {
                closure.work(assetData);

            }
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

    }

    /**
     * Reads an asset based on the listed attribute names
     * 
     * TODO: do we need to load the attribute values and prevent access to
     * non-listed attributes (prevent lazy loading)
     * 
     * @param id the assetid
     * @param attributes the list of attributes to return
     * @return the asset found
     * 
     * 
     */
    public AssetData readAsset(AssetId id, String... attributes) {
        AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(Arrays.asList(id));
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        if (assets == null)
            return null;
        if (assets instanceof List<?>) {
            List<AssetData> x = (List<AssetData>) assets;
            if (x.isEmpty())
                return null;
            else
                return x.get(0);
        }
        Iterator<AssetData> i = assets.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

    private AssetDataManager assetDataManager;

    /**
     * @return
     */
    protected AssetDataManager getAssetDataManager() {
        if (assetDataManager == null) {
            assetDataManager = (AssetDataManager) session.getManager(AssetDataManager.class.getName());
        }
        return assetDataManager;
    }

    /**
     * @param id
     * @return
     */
    public AssetData readAsset(AssetId id) {

        AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(Collections.singletonList(id));
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        if (assets == null)
            return null;
        if (assets instanceof List<?>) {
            List<AssetData> x = (List<AssetData>) assets;
            if (x.isEmpty())
                return null;
            else
                return x.get(0);
        }
        Iterator<AssetData> i = assets.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

    /**
     * @param q
     * @return
     */
    public Iterable<AssetData> readAssets(Query q) {
        AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(q);
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

        return assets;
    }

    /**
     * Reading assets with the Query and using the mapper to transform the AssetData into another object, as specified by T.
     * @param <T>
     * @param q
     * @param mapper
     * @return
     */
    public <T> Iterable<T> readAssets(Query q, AssetMapper<T> mapper) {

        List<T> r = new LinkedList<T>();

        for (AssetData data : readAssets(q)) {
            r.add(mapper.map(data));
        }

        return r;
    }

    /**
     * @param ics
     * @param assetType
     * @param name
     * @return
     */
    public AssetId findByName(ICS ics, String assetType, String name) {
        // TODO: name does not need to be unique, how do we handle this?
        AssetList x = new AssetList();
        x.setType(assetType);
        x.setField("name", name);
        x.setList("name");
        x.execute(ics);

        IList list = ics.GetList("name__");
        ics.RegisterList("name__", null);
        if (list != null && list.hasData()) {
            list.moveTo(1);
            try {
                return new AssetIdImpl(assetType, Long.parseLong(list.getValue("id")));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } else {
            // TODO: is this correct, or should we return null, searching for
            // something that does not exists keeps many people busy and happy
            throw new RuntimeException(assetType + " with name '" + name + "' not found.");
        }

    }

    public SimpleQuery createNameQuery(String assetType, String assetName) {
        final SimpleQuery q = new SimpleQuery(assetType, null, ConditionFactory.createCondition("name",
                OpTypeEnum.EQUALS, assetName), Arrays.asList("id"));
        q.getProperties().setIsBasicSearch(true);
        return q;
    }

}
