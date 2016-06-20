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
package com.fatwire.gst.foundation.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.ISyncHash;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.common.SiteAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.MutableAssetData;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.Query;
import com.fatwire.assetapi.site.SiteInfo;
import com.fatwire.assetapi.site.SiteManager;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.assetapi.QueryBuilder;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * Class representing properties stored as an asset. Can be basic or flex but
 * fields are specifically defined
 * 
 * @author Tony Field
 * @since 11-09-02
 */
// TODO: Figure out how to assign these to a give site/publication
public final class AssetApiPropertyDao implements PropertyDao {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.foundation.properties.AssetApiPropertyDao");

    public static final String TYPE = "GSTProperty";
    public static final String SUBTYPE = "GSTProperty";

    private static final int TIMEOUT_MINUTES = 60 * 24; // one day
    private static final int MAX_SIZE = 1000000; // a million
    private final ISyncHash _props;
    private final ICS ics;
    private final AssetDataManager assetDataManager;

    public static final PropertyDao getInstance(ICS ics) {
        return newInstance(ics);
    }

    public static final PropertyDao newInstance(ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics must not be null.");
        }

        return new AssetApiPropertyDao(ics);
    }

    private AssetApiPropertyDao(ICS ics) {
        this.ics = ics;
        this.assetDataManager = (AssetDataManager)SessionFactory.getSession(ics).getManager(AssetDataManager.class.getName());
        this._props = ics.GetSynchronizedHash(AssetApiPropertyDao.class.getName(), true, TIMEOUT_MINUTES, MAX_SIZE, true, true, Arrays.asList(ics.GetProperty("cs.dsn")+TYPE));
    }

    public synchronized Property getProperty(String name) {
        PropertyHolder ph = (PropertyHolder) _props.get(name);
        if (ph == null) {
            ph = _readProperty(name);
            _props.put(name, ph);
        }
        if (ph.getId() != null) {
            LogDep.logDep(ics, ph.getId());
        }
        return ph.getProp();
    }

    @SuppressWarnings("unchecked")
	public synchronized Collection<String> getPropertyNames() {
        return _props.keySet();
    }

    private PropertyHolder _readProperty(String name) {
        Query loadQuery = new QueryBuilder(TYPE, SUBTYPE).attributes("id", "name", "description", "value")
                .condition("status", OpTypeEnum.NOT_EQUALS, "VO")
                .condition("name", OpTypeEnum.EQUALS, name)
                .setBasicSearch(true)
                .setFixedList(true)  // we are being precise
                .toQuery();
        TemplateAssetAccess templateAssetAccess = new TemplateAssetAccess(ics);
        for (TemplateAsset d : templateAssetAccess.query(loadQuery)) {
            if (LOG.isTraceEnabled())
                LOG.trace("Loaded property: " + name);
            return new AssetApiPropertyDao.PropertyHolder(name, d.asString("description"), d.asString("value"), TYPE, d.asLong("id"));
        }
        if (LOG.isTraceEnabled())
            LOG.trace("Property not found: "+name);
        return AssetApiPropertyDao.PropertyHolder.EMPTY_HOLDER;
    }

    /**
     * Convenience method to set (or re-set) a property value
     *
     * @param name        property name
     * @param description property description (optional)
     * @param value       value as a string
     */
    public synchronized void setProperty(String name, String description, String value) {
        if (name == null) throw new IllegalArgumentException("Cannot set a null property name");

        AssetId id = AssetList.lookupAssetId(ics, TYPE, name);
        if (id == null) {
            try {// add
                MutableAssetData data = assetDataManager.newAssetData("GSTProperty", "GSTProperty");
                data.getAttributeData("name").setData(name);
                data.getAttributeData("description").setData(description);
                data.getAttributeData("value").setData(value);
                _appendCurrentToPublist(data.getAttributeData("Publist"));
                assetDataManager.insert(Arrays.<AssetData>asList(data));
                id = data.getAssetId();
            } catch (AssetAccessException e) {
                throw new CSRuntimeException("Could not add new property "+name, ftErrors.exceptionerr, e);
            }
        } else {
            // replace
            try {
                ArrayList<AssetData> sAssets = new ArrayList<AssetData>();
                for (MutableAssetData data : assetDataManager.readForUpdate(Arrays.asList(id))) {
                    // sorry can't reset 'name'
                    data.getAttributeData("description").setData(description);
                    data.getAttributeData("value").setData(value);
                    _appendCurrentToPublist(data.getAttributeData("Publist"));
                    sAssets.add(data);
                }
                assetDataManager.update(sAssets); // there can only be one since we are loading by id
            } catch (AssetAccessException e) {
                throw new CSRuntimeException("Could not update property "+name, ftErrors.exceptionerr, e);
            }
        }
        // cache or re-cache
        _props.put(name, new AssetApiPropertyDao.PropertyHolder(name, description, value, id.getType(), id.getId()));
    }

    /**
     * Set (or re-set) a property value
     *
     * @param property property object with name and value
     */
    public synchronized void setProperty(Property property) {
        if (property == null) throw new IllegalArgumentException("Can't set a null property object");
        setProperty(property.getName(), property.getDescription(), property.asString());
    }

    @SuppressWarnings("unchecked")
	private void _appendCurrentToPublist(AttributeData data) {
        String currentPub = _getCurrentSite();
        if (currentPub != null) {
            HashSet<String> pubs = new HashSet<String>();
            pubs.add(currentPub);
            pubs.addAll(data.getDataAsList());
            data.setDataAsList(new ArrayList<String>(pubs));
        }
    }

    private String _getCurrentSite() {
        String pubid = ics.GetSSVar("pubid");
        if (pubid != null) {
            long id = Long.valueOf(pubid);
            SiteManager sm = (SiteManager) SessionFactory.getSession().getManager(SiteManager.class.getName());
            try {
                for (SiteInfo si : sm.list()) {
                    if (si.getId() == id) return si.getName();
                }
            } catch (SiteAccessException e) {
                throw new CSRuntimeException("Could not determine name of current site: " + e, ftErrors.exceptionerr, e);
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void addToSite(String name, String ... site) {
        if (name == null) throw new IllegalArgumentException("Invalid property name null");
        AssetId id = AssetList.lookupAssetId(ics, "GSTProperty", name);
        if (id == null) throw new IllegalArgumentException("Could not locate property "+id);

        try {
            ArrayList<AssetData> sAssets = new ArrayList<AssetData>();
            for (MutableAssetData data : assetDataManager.readForUpdate(Arrays.asList(id))) {
                HashSet<String> pubs = new HashSet<String>();
                pubs.addAll(Arrays.asList(site));
                AttributeData publistData = data.getAttributeData("Publist");
                pubs.addAll(publistData.getDataAsList());
                publistData.setDataAsList(new ArrayList(pubs));
                sAssets.add(data);
            }
            assetDataManager.update(sAssets);
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failure adding property "+name+" to sites "+Arrays.asList(site), ftErrors.exceptionerr, e);
        }
    }

    private static class PropertyHolder {

        private static final PropertyHolder EMPTY_HOLDER = new PropertyHolder();
        private final Property prop;
        private final AssetId id;

        /**
         * Default constructor for empty object.
         */
        private PropertyHolder() {
            prop = null;
            id = null;
        }

        /**
         * Deep copy constructor, to be used when adding, to prevent messy cache issues
         * @param name property name
         * @param description property description
         * @param value property value
         * @param type property type
         * @param propid property id
         */
        private PropertyHolder (String name, String description, String value, String type, long propid) {
            if (name == null) throw new IllegalArgumentException("Null name not allowed");
            if (value == null) throw new IllegalArgumentException("Null value not allowed");
            if (type == null) throw new IllegalArgumentException("Null property asset type not allowed");
            this.prop = new PropertyImpl(name, description, value);
            this.id = new AssetIdImpl(type, propid);
        }

        private Property getProp() {
            return prop;
        }

        private AssetId getId() {
            return id;
        }
    }
}
