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
package tools.gsf.properties;

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
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.QueryBuilder;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.runtime.CSRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Class representing properties stored as an asset. Can be basic or flex. Property name field must be a core
 * asset field ("name" is usually chosen). Other fields can be set as needed.
 * <p>
 * Adding a new property adds it to the current site, as defined by the pubid session variable.
 *
 * @author Tony Field
 * @since 2011-09-02
 */
public final class AssetApiPropertyDao implements PropertyDao {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.properties.AssetApiPropertyDao");
    private static final int TIMEOUT_MINUTES = 60 * 24; // one day
    private static final int MAX_SIZE = 1000000; // a million
    private static final String ID = "id";
    private static final String PUBLIST = "Publist";

    private final ISyncHash _props;
    private final AssetDataManager assetDataManager;
    private final SiteManager siteManager;
    private final String assetType;
    private final String assetFlexDefinition;
    private final String propertyNameAttr;
    private final String propertyDescriptionAttr;
    private final String propertyValueAttr;
    private final ICS _ics;


    /**
     * Property dao backed by a basic or flex asset.
     *
     * @param adm           asset data manager
     * @param siteManager   site manager
     * @param type          asset type to be used
     * @param flexDefName   flex definition, if using a flex asset; null if using a basic asset
     * @param propNameAttr  attribute name holding property name. Must be a core asset field (i.e. in the main row of the asset) so that lookups can be done properly. Must be a string attribute type.
     * @param propDescAttr  attribute name holding the description of the property Must be a string attribute type.
     * @param propValueAttr attribute name holding the property value. Must be a string attribute type.
     * @param ics           ics context
     */
    public AssetApiPropertyDao(AssetDataManager adm, SiteManager siteManager, String type, String flexDefName, String propNameAttr, String propDescAttr, String propValueAttr, ICS ics) {
        this.assetDataManager = adm;
        this.siteManager = siteManager;
        this.assetType = type;
        this.assetFlexDefinition = flexDefName;
        this.propertyNameAttr = propNameAttr;
        this.propertyDescriptionAttr = propDescAttr;
        this.propertyValueAttr = propValueAttr;
        this._props = ics.GetSynchronizedHash(AssetApiPropertyDao.class.getName(), true, TIMEOUT_MINUTES, MAX_SIZE, true, true, Collections.singletonList(ics.GetProperty("cs.dsn") + assetType));
        this._ics = ics;
    }

    public synchronized Property getProperty(String name) {
        PropertyHolder ph = (PropertyHolder) _props.get(name);
        if (ph == null) {
            ph = _readProperty(name);
            _props.put(name, ph);
        }
        if (ph.getId() != null) {
            LogDep.logDep(_ics, ph.getId()); // can't rely on asset API to do this for us since we're caching
        }
        return ph.getProp();
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection<String> getPropertyNames() {
        ArrayList<String> keys = new ArrayList<>();
        for (Object key : _props.keySet()) {
            String sKey = (String) key;
            PropertyHolder ph = (PropertyHolder) _props.get(sKey);
            if (ph != PropertyHolder.EMPTY_HOLDER) {
                keys.add(sKey);
            }
        }
        return keys;
    }

    private PropertyHolder _readProperty(String name) {
        Query loadQuery = new QueryBuilder(assetType, assetFlexDefinition).attributes(ID, propertyNameAttr, propertyDescriptionAttr, propertyValueAttr)
                .condition("status", OpTypeEnum.NOT_EQUALS, "VO")
                .condition(propertyNameAttr, OpTypeEnum.EQUALS, name)
                .setBasicSearch(true) // note must be a core field for lookup to work
                .setFixedList(true)  // we are being precise
                .toQuery();
        try {
            for (AssetData propData : assetDataManager.read(loadQuery)) {
                AttributeData oName = propData.getAttributeData(propertyNameAttr);
                AttributeData oDesc = propData.getAttributeData(propertyDescriptionAttr);
                AttributeData oVal = propData.getAttributeData(propertyValueAttr);
                AssetId id = propData.getAssetId();
                if (oName == null) {
                    throw new IllegalStateException("Property name attribute configured in PropertyDao not found in asset: " + propertyNameAttr);
                }
                if (oDesc == null) {
                    throw new IllegalStateException("Property description attribute configured in PropertyDao not found in asset" + propertyDescriptionAttr);
                }
                if (oVal == null) {
                    throw new IllegalStateException("Property value attribute configured in PropertyDao not found in asset" + propertyValueAttr);
                }
                LOG.debug("Loaded property {}", name);
                return new PropertyHolder(name, AttributeDataUtils.asString(oDesc), AttributeDataUtils.asString(oVal), id);
            }
        } catch (AssetAccessException e) {
            LOG.error("Failure reading property: {}", name, e);
            return AssetApiPropertyDao.PropertyHolder.EMPTY_HOLDER;
        }
        LOG.debug("Property not found: {}", name);
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
        if (name == null) {
            throw new IllegalArgumentException("Cannot set a null property name");
        }

        String pubid = _ics.GetSSVar("pubid");
        String site = _lookupSiteName(pubid);

        PropertyHolder holder = _readProperty(name);
        if (holder == PropertyHolder.EMPTY_HOLDER) {
            // add
            try {
                MutableAssetData data = assetDataManager.newAssetData(assetType, assetFlexDefinition);
                data.getAttributeData(propertyNameAttr).setData(name);
                data.getAttributeData(propertyDescriptionAttr).setData(description);
                data.getAttributeData(propertyValueAttr).setData(value);
                _appendToPublist(data.getAttributeData(PUBLIST), site);
                assetDataManager.insert(Collections.singletonList(data));
                holder = new PropertyHolder(name, description, value, data.getAssetId());
            } catch (AssetAccessException e) {
                throw new CSRuntimeException("Could not add new property " + name, ftErrors.exceptionerr, e);
            }
        } else {
            // replace
            try {
                ArrayList<AssetData> sAssets = new ArrayList<>();
                for (MutableAssetData data : assetDataManager.readForUpdate(Collections.singletonList(holder.getId()))) {
                    // sorry can't reset 'name'
                    data.getAttributeData(propertyDescriptionAttr).setData(description);
                    data.getAttributeData(propertyValueAttr).setData(value);
                    _appendToPublist(data.getAttributeData(PUBLIST), site);
                    sAssets.add(data);
                    holder = new PropertyHolder(name, description, value, holder.getId());
                }
                assetDataManager.update(sAssets); // there can only be one since we are loading by id
            } catch (AssetAccessException e) {
                throw new CSRuntimeException("Could not update property " + name, ftErrors.exceptionerr, e);
            }
        }
        // cache or replace in cache
        _props.put(name, holder);
    }

    /**
     * Set (or re-set) a property value
     *
     * @param property property object with name and value
     */
    public synchronized void setProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Can't set a null property object");
        }
        setProperty(property.getName(), property.getDescription(), property.asString());
    }

    @SuppressWarnings("unchecked")
    private void _appendToPublist(AttributeData data, String... siteName) {
        if (siteName != null) {
            HashSet<String> pubs = new HashSet<>();
            pubs.addAll(Arrays.asList(siteName));
            pubs.addAll(data.getDataAsList());
            data.setDataAsList(new ArrayList<>(pubs));
        }
    }

    private String _lookupSiteName(String pubid) {
        if (pubid != null) {
            long id = Long.valueOf(pubid);
            try {
                for (SiteInfo si : siteManager.list()) {
                    if (si.getId() == id) {
                        return si.getName();
                    }
                }
            } catch (SiteAccessException e) {
                throw new CSRuntimeException("Could not determine name of current site: " + e, ftErrors.exceptionerr, e);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public synchronized void addToSite(String name, String... site) {
        if (name == null) {
            throw new IllegalArgumentException("Invalid property name null");
        }
        PropertyHolder holder = _readProperty(name);
        if (holder == PropertyHolder.EMPTY_HOLDER) {
            throw new IllegalArgumentException("Could not locate property " + name);
        }

        try {
            ArrayList<AssetData> sAssets = new ArrayList<>();
            for (MutableAssetData data : assetDataManager.readForUpdate(Collections.singletonList(holder.getId()))) {
                AttributeData publistData = data.getAttributeData(PUBLIST);
                _appendToPublist(publistData, site);
                sAssets.add(data);
            }
            assetDataManager.update(sAssets);
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failure adding property " + name + " to sites " + Arrays.asList(site), ftErrors.exceptionerr, e);
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
         *
         * @param name        property name
         * @param description property description
         * @param value       property value
         * @param id          property id
         */
        private PropertyHolder(String name, String description, String value, AssetId id) {
            if (name == null) {
                throw new IllegalArgumentException("Null name not allowed");
            }
            if (value == null) {
                throw new IllegalArgumentException("Null value not allowed");
            }
            this.id = id;
            this.prop = new PropertyImpl(id.getType(), name, description, value);
        }

        private Property getProp() {
            return prop;
        }

        private AssetId getId() {
            return id;
        }
    }
}