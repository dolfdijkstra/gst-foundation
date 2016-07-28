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

package tools.gsf.facade.assetapi;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.common.SiteAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.Query;
import com.fatwire.assetapi.query.SimpleQuery;
import com.fatwire.assetapi.site.Site;
import com.fatwire.assetapi.site.SiteInfo;
import com.fatwire.assetapi.site.SiteManager;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import tools.gsf.facade.runtag.asset.AssetList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is a one-stop-shop for all read-only access to AssetData. It acts
 * as a helper class to facilitate {@link AssetDataManager} use in a simplified
 * way in delivery ContentServer templates.
 * <p>
 * This class is inspired by springframework data access template classes like
 * org.springframework.jdbc.core.JdbcTemplate.
 * <p>
 * This class is not thread safe and should not be shared between threads.
 *
 * @author Dolf.Dijkstra
 * @since Nov 23, 2009
 */
public class AssetAccessTemplate {

    private final Session session;
    private AssetDataManager assetDataManager;

    /**
     * @param session session object
     */
    public AssetAccessTemplate(final Session session) {
        super();
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null.");
        }
        this.session = session;
    }

    /**
     * Constructor that accepts ICS as an argument.
     *
     * @param ics Content Server context object
     */
    public AssetAccessTemplate(final ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics cannot be null.");
        }
        session = SessionFactory.getSession(ics);
    }

    /**
     * Helper method to create an AssetId from c and cid as string values.
     *
     * @param c   current asset
     * @param cid content id
     * @return the assetId for c/cid.
     */
    public AssetId createAssetId(final String c, final String cid) {
        return new AssetIdImpl(c, Long.parseLong(cid));
    }

    /**
     * Helper method to create an AssetId from c and cid as string values.
     *
     * @param c   current asset
     * @param cid content id
     * @return the assetId for c/cid.
     */
    public AssetId createAssetId(final String c, final long cid) {
        return new AssetIdImpl(c, cid);
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper.
     *
     * @param <T>    type specified from asset mapper
     * @param id     asset id
     * @param mapper asset mapper
     * @return the Object created by the mapper.
     */
    public <T> T readAsset(final AssetId id, final AssetMapper<T> mapper) {
        final AssetDataManager m = getAssetDataManager();

        T t = null;
        try {
            final Iterable<AssetData> assets = m.read(Arrays.asList(id));
            for (final AssetData assetData : assets) {
                t = mapper.map(assetData);
            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        return t;
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper interface..
     *
     * @param <T>    type specified from asset mapper
     * @param c      the assetType
     * @param cid    the asset id
     * @param mapper asset mapper
     * @return the object created by the mapper.
     */
    public <T> T readAsset(final String c, final String cid, final AssetMapper<T> mapper) {
        return readAsset(this.createAssetId(c, cid), mapper);
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper.
     *
     * @param <T>    type specified from asset mapper
     * @param c      the assetType
     * @param cid    the asset id
     * @param mapper asset mapper
     * @return the object created by the mapper.
     */
    public <T> T readAsset(final String c, final long cid, final AssetMapper<T> mapper) {
        return readAsset(new AssetIdImpl(c, cid), mapper);
    }

    /**
     * Method to read an asset and use the AssetMapper to transform the
     * AssetData into another object as specified by the AssetMapper. Only the
     * list of lister attributes is retrieved from the asset.
     *
     * @param <T>        type specified from asset mapper
     * @param id         asset id
     * @param mapper     asset mapper
     * @param attributes array of attribute names
     * @return the object created by the mapper.
     */
    public <T> T readAsset(final AssetId id, final AssetMapper<T> mapper, final String... attributes) {
        final AssetDataManager m = getAssetDataManager();

        T t = null;
        try {
            final AssetData asset = m.readAttributes(id, Arrays.asList(attributes));
            t = mapper.map(asset);
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        return t;
    }

    /**
     * Method to read an asset and provide the AssetClosure with the AssetData.
     * Only the list of lister attributes is retrieved from the asset.
     *
     * @param id         asset id
     * @param closure    asset closure
     * @param attributes array of attribute names
     */
    public void readAsset(final AssetId id, final AssetClosure closure, final String... attributes) {
        final AssetDataManager m = getAssetDataManager();

        try {
            final AssetData asset = m.readAttributes(id, Arrays.asList(attributes));
            if (asset != null) {
                if (!closure.work(asset)) {
                    return;
                }
            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
    }

    /**
     * Method to read an asset and pass the results to the closure for further
     * handling.
     *
     * @param id      the assetid to read
     * @param closure the closure
     */
    public void readAsset(final AssetId id, final AssetClosure closure) {
        final AssetDataManager m = getAssetDataManager();

        try {
            final Iterable<AssetData> assets = m.read(Arrays.asList(id));
            for (final AssetData assetData : assets) {
                if (!closure.work(assetData)) {
                    return;
                }

            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

    }

    /**
     * Method to read an asset and pass the results to the closure for further
     * handling.
     *
     * @param ids     a list of AssetIds
     * @param closure the closure
     */
    public void readAsset(final List<AssetId> ids, final AssetClosure closure) {
        final AssetDataManager m = getAssetDataManager();

        try {
            final Iterable<AssetData> assets = m.read(ids);
            for (final AssetData assetData : assets) {
                if (!closure.work(assetData)) {
                    return;
                }

            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

    }

    /**
     * Method to read an asset and pass the results to the closure for further
     * handling.
     *
     * @param ids        a list of AssetIds
     * @param closure    the closure
     * @param attributes array of attribute names
     */
    public void readAsset(final Iterable<AssetId> ids, final AssetClosure closure, final String... attributes) {
        final AssetDataManager m = getAssetDataManager();

        try {
            for (final AssetId id : ids) {
                final AssetData asset = m.readAttributes(id, Arrays.asList(attributes));
                if (asset != null) {
                    if (!closure.work(asset)) {
                        return;
                    }
                }

            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

    }

    /**
     * Reads an asset based on the listed attribute names
     * <p>
     * TODO: do we need to load the attribute values and prevent access to
     * non-listed attributes (prevent lazy loading)
     *
     * @param id         the assetid
     * @param attributes the list of attributes to return
     * @return the asset found
     */
    public AssetData readAsset(final AssetId id, final String... attributes) {
        final AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(Arrays.asList(id));
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        if (assets == null) {
            return null;
        }
        if (assets instanceof List<?>) {
            final List<AssetData> x = (List<AssetData>) assets;
            if (x.isEmpty()) {
                return null;
            } else {
                return x.get(0);
            }
        }
        final Iterator<AssetData> i = assets.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

    /**
     * @return the manager
     */
    protected AssetDataManager getAssetDataManager() {
        if (assetDataManager == null) {
            assetDataManager = (AssetDataManager) session.getManager(AssetDataManager.class.getName());
        }
        return assetDataManager;
    }

    /**
     * @param id the assetid to read
     * @return the assetdata for this id
     */
    public AssetData readAsset(final AssetId id) {

        final AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(Collections.singletonList(id));
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
        if (assets == null) {
            return null;
        }
        if (assets instanceof List<?>) {
            final List<AssetData> x = (List<AssetData>) assets;
            if (x.isEmpty()) {
                return null;
            } else {
                return x.get(0);
            }
        }
        final Iterator<AssetData> i = assets.iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return null;
    }

    /**
     * @param query query object
     * @return iterable with AssetData from the query result.
     */
    public Iterable<AssetData> readAssets(final Query query) {
        final AssetDataManager m = getAssetDataManager();

        Iterable<AssetData> assets;
        try {
            assets = m.read(query);
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }

        return assets;
    }

    /**
     * Invokes the work(asset) method on the provided Closure for assets
     * returned by the Query.
     *
     * @param query   the query
     * @param closure the closure
     */
    public void readAssets(final Query query, final AssetClosure closure) {
        final AssetDataManager m = getAssetDataManager();

        try {
            for (final AssetData asset : m.read(query)) {
                if (!closure.work(asset)) {
                    return;
                }

            }
        } catch (final AssetAccessException e) {
            throw new RuntimeAssetAccessException(e);
        }
    }

    /**
     * Reading assets with the Query and using the mapper to transform the
     * AssetData into another object, as specified by T.
     *
     * @param <T>    type specified from asset mapper
     * @param query  query object
     * @param mapper asset mapper
     * @return the objects created by the mapper.
     */
    public <T> Iterable<T> readAssets(final Query query, final AssetMapper<T> mapper) {

        final List<T> r = new LinkedList<T>();

        for (final AssetData data : readAssets(query)) {
            r.add(mapper.map(data));
        }

        return r;
    }

    /**
     * Finds the assetid by the name of the asset in a particular site. The
     * asset can not be voided.
     *
     * @param ics       Content Server context object
     * @param assetType the type of the asset.
     * @param name      the name of the asset.
     * @param siteid    the Site id.
     * @return the assetid, null if asset is not found.
     */
    public AssetId findByName(final ICS ics, final String assetType, final String name, final long siteid) {
        // TODO: name does not need to be unique, how do we handle this?
        final AssetList tag = new AssetList();
        tag.setType(assetType);
        tag.setField("name", name);
        tag.setExcludeVoided(true);
        tag.setPubid(Long.toString(siteid));
        tag.setList("name__");
        tag.execute(ics);

        final IList list = ics.GetList("name__");
        ics.RegisterList("name__", null);
        if (list != null && list.hasData()) {
            list.moveTo(1);
            try {
                return new AssetIdImpl(assetType, Long.parseLong(list.getValue("id")));
            } catch (final NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }

    }

    /**
     * Finds the assetid by the name of the asset. The asset can not be voided.
     *
     * @param ics       Content Server context object
     * @param assetType the type of the asset.
     * @param name      the name of the asset.
     * @return the assetid, null if asset is not found.
     */

    public AssetId findByName(final ICS ics, final String assetType, final String name) {
        // TODO: name does not need to be unique, how do we handle this?
        final AssetList x = new AssetList();
        x.setType(assetType);
        x.setField("name", name);
        x.setExcludeVoided(true);
        x.setList("name__");
        x.execute(ics);

        final IList list = ics.GetList("name__");
        ics.RegisterList("name__", null);
        if (list != null && list.hasData()) {
            list.moveTo(1);
            try {
                return new AssetIdImpl(assetType, Long.parseLong(list.getValue("id")));
            } catch (final NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }

    }

    /**
     * Creates a Query to retrieve the asset by it's name.
     *
     * @param assetType string value of asset type
     * @param assetName string value of asset name
     * @return the simple query
     */
    public SimpleQuery createNameQuery(final String assetType, final String assetName) {
        final SimpleQuery q = new SimpleQuery(assetType, null, ConditionFactory.createCondition("name",
                OpTypeEnum.EQUALS, assetName), Arrays.asList("id"));
        q.getProperties().setIsBasicSearch(true);
        return q;
    }

    /**
     * Finds the Site object by the given name.
     *
     * @param name the name of the site.
     * @return the Site object.
     */
    public Site readSite(final String name) {
        final SiteManager sm = (SiteManager) session.getManager(SiteManager.class.getName());
        try {

            final List<Site> list = sm.read(Arrays.asList(name));
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.get(0);
        } catch (final SiteAccessException e) {
            throw new SiteAccessRuntimeException(e);
        }
    }

    /**
     * @param name site name
     * @return the site info object
     */
    public SiteInfo readSiteInfo(final String name) {
        final SiteManager sm = (SiteManager) session.getManager(SiteManager.class.getName());
        try {

            for (SiteInfo si : sm.list()) {
                if (name.equals(si.getName())) {
                    return si;
                }
            }
        } catch (final SiteAccessException e) {
            throw new SiteAccessRuntimeException(e);
        }
        throw new SiteAccessRuntimeException("Site " + name + " does not exist in Content Server.");
    }

    /**
     * Reads the associated assets of the asset and returns the AssetIds.
     *
     * @param id              asset id
     * @param associationType association type
     * @return the assets from the associations.
     */
    public Collection<AssetId> readAssociatedAssetIds(final AssetId id, final String associationType) {
        final List<AssetId> list = readAsset(id).getAssociatedAssets(associationType);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;

    }

    /**
     * Reads the associated assets of an asset and returns them as a
     * ScatteredAsset. This takes care of the asset read operation of the
     * associated assets. The returned ScatteredAssets are only loaded with the
     * mentioned attributes.
     *
     * @param id              the parent asset
     * @param associationType the name of the association or '-' for an unnamed
     *                        association
     * @param closure         the AssetClosure to work on.
     * @param attributes      the list of attributes to load
     */
    public void readAssociatedAssets(final AssetId id, final String associationType, final AssetClosure closure,
                                     final String... attributes) {
        final List<AssetId> list = this.readAsset(id).getAssociatedAssets(associationType);
        if (list == null || list.isEmpty()) {
            return;
        }

        for (final AssetId child : list) {
            readAsset(child, closure, attributes);
        }

    }

    /**
     * Queries for a list of objects as mapped by the AssetMapper.
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
     * </ul>
     *
     * @param <T>        type specified by asset mapper
     * @param assetType  asset type
     * @param subType    sub-type
     * @param query      string value of query
     * @param mapper     asset mapper
     * @param attributes array of attribute names
     * @return a iterable of assets.
     * @see AssetAccessTemplate#query(String, String, String, AssetMapper,
     * String...)
     */
    public <T> Iterable<T> query(final String assetType, final String subType, final String query,
                                 AssetMapper<T> mapper, final String... attributes) {
        final Query q = new QueryBuilder(assetType, subType).condition(query).attributes(attributes).toQuery();
        return this.readAssets(q, mapper);
    }

    /**
     * Queries for a list of objects as mapped by the AssetMapper.
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
     * </ul>
     *
     * @param <T>       type specified by asset mapper
     * @param assetType asset type
     * @param subType   sub-type
     * @param query     string value of query
     * @param mapper    asset mapper
     * @return a iterable of assets.
     */
    public <T> Iterable<T> query(final String assetType, final String subType, final String query, AssetMapper<T> mapper) {
        final Query q = new QueryBuilder(assetType, subType).condition(query).setReadAll(true).toQuery();
        return this.readAssets(q, mapper);
    }

}
