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
import java.util.List;

import COM.FutureTense.Interfaces.FTValList;
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
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.Asset;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import com.openmarket.xcelerate.interfaces.IApprovalDependency;
import com.openmarket.xcelerate.interfaces.IAsset;

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
     * @param ics
     */
    public AssetAccessTemplate(ICS ics) {
        if (ics == null)
            throw new IllegalArgumentException("ics cannot be null.");
        session = SessionFactory.getSession(ics);
    }

    public AssetId createAssetId(String c, String cid) {
        return new AssetIdImpl(c, Long.parseLong(cid));
    }

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

    /**
     * @return
     */
    protected AssetDataManager getAssetDataManager() {
        AssetDataManager m = (AssetDataManager) session.getManager(AssetDataManager.class.getName());
        return m;
    }

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

    public Iterable<AssetData> query(Query q) {
        AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(q);
        } catch (AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

        return assets;
    }

    public AssetId findByName(ICS ics, String assetType, String name) {
        // TODO: name does not need to be unique, how do we handle this?
        FTValList l = new FTValList();
        l.setValString("name", name);

        IList list = Asset.List(ics, l, "name", assetType);
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

    public IAsset loadByName(ICS ics, String assetType, String name) {
        return (IAsset) Asset.Load(ics, "name", name, assetType, IApprovalDependency.DEPTYPE_EXISTS,
                IAsset.LOAD_READONLY, null);
    }

    public IAsset loadById(ICS ics, AssetId id) {
        return Asset.callLoad(ics, id.getId(), null, id.getType(), false);

    }

    public SimpleQuery createNameQuery(String assetType, String assetName) {
        final SimpleQuery q = new SimpleQuery(assetType, null, ConditionFactory.createCondition("name",
                OpTypeEnum.EQUALS, assetName), Arrays.asList("id"));
        q.getProperties().setIsBasicSearch(true);
        return q;
    }

}
