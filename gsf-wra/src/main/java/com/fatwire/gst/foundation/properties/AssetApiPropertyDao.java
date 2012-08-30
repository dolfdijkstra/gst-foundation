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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
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
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

/**
 * Class representing properties stored as an asset. Can be basic or flex but
 * fields are specifically defined
 * 
 * @author Tony Field
 * @since 11-09-02
 */
// TODO: Figure out how to assign these to a give site/publication
public final class AssetApiPropertyDao implements PropertyDao {
    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.properties");

    public static final String TYPE = "GSTProperty";
    public static final String SUBTYPE = "GSTProperty";
    private static final Query LOAD_ALL_QRY = new QueryBuilder(TYPE, SUBTYPE)
            .attributes("name", "description", "value").condition("status", OpTypeEnum.NOT_EQUALS, "VO")
            .setBasicSearch(true).toQuery();

    private final Map<String, Property> _props;
    private final ICS ics;
    private AssetDataManager assetDataManager = null;

    public static final PropertyDao getInstance(ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics must not be null.");
        }

        Object o = ics.GetObj(PropertyDao.class.getName());
        if (o instanceof PropertyDao == false) {
            o = newInstance(ics);
            ics.SetObj(PropertyDao.class.getName(), o);
        }
        return (PropertyDao) o;
    }

    public static PropertyDao newInstance(ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics must not be null.");
        }

        return new AssetApiPropertyDao(ics);
    }

    private AssetApiPropertyDao(ICS ics) {
        this.ics = ics;
        this.assetDataManager = (AssetDataManager)SessionFactory.getSession(ics).getManager(AssetDataManager.class.getName());
        _props = new HashMap<String, Property>();
        TemplateAssetAccess mgr = new TemplateAssetAccess(ics);

        LOG.trace("Loading all GSTProperties");
        for (TemplateAsset d : mgr.query(LOAD_ALL_QRY)) {
            String name = d.asString("name");
            PropertyImpl p = new PropertyImpl(name, d.asString("description"), d.asString("value"));
            _props.put(name, p);
            if (LOG.isTraceEnabled())
                LOG.trace("Loaded property: " + p);
        }
    }

    public Property getProperty(String name) {
        return _props.get(name);
    }

    public Collection<String> getPropertyNames() {
        return _props.keySet();
    }

    /**
     * Set (or re-set) a property value
     *
     * @param property property object with name and value
     */
    public void setProperty(Property property) {
        if (property == null) throw new IllegalArgumentException("Can't set a null property object");
        if (_props.containsKey(property.getName())) {
            // replace asset
            try {
                Session ses = SessionFactory.getSession();
                AssetId id = AssetList.lookupAssetId(ics, "GSTProperty", property.getName());
                ArrayList<AssetData> sAssets = new ArrayList<AssetData>();
                for (MutableAssetData data : assetDataManager.readForUpdate(Arrays.asList(id))) {
                    // sorry can't reset 'name'
                    data.getAttributeData("description").setData(property.getDescription());
                    data.getAttributeData("value").setData(property.asString());
                    appendCurrentToPublist(data.getAttributeData("Publist"));
                    sAssets.add(data);
                }
                assetDataManager.update(sAssets);
            } catch (Exception e) {
                throw new CSRuntimeException("Could not update property " + property, ftErrors.exceptionerr, e);
            }
        } else {
            // add asset
            try {
                Session ses = SessionFactory.getSession();
                MutableAssetData data = assetDataManager.newAssetData("GSTProperty", "GSTProperty");
                data.getAttributeData("name").setData(property.getName());
                data.getAttributeData("description").setData(property.getDescription());
                data.getAttributeData("value").setData(property.asString());
                appendCurrentToPublist(data.getAttributeData("Publist"));
                assetDataManager.insert(Arrays.<AssetData>asList(data));
            } catch (AssetAccessException e) {
                throw new CSRuntimeException("Could not add new property " + property, ftErrors.exceptionerr, e);
            }
        }
        _props.put(property.getName(), property); // update cache
    }

    private void appendCurrentToPublist(AttributeData data) {
        String currentPub = getCurrentSite();
        if (currentPub != null) {
            HashSet<String> pubs = new HashSet<String>();
            pubs.add(currentPub);
            pubs.addAll(data.getDataAsList());
            data.setDataAsList(new ArrayList<String>(pubs));
        }
    }

    private String getCurrentSite() {
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

    /**
     * Convenience method to set (or re-set) a property value
     *
     * @param name        property name
     * @param description property description (optional)
     * @param value       value as a string
     */
    public void setProperty(String name, String description, String value) {
        if (name == null) throw new IllegalArgumentException("Cannot set a null property name");
        setProperty(new PropertyImpl(name, description, value));
    }

    public void addToSite(String name, String ... site) {
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
            throw new CSRuntimeException("Failure adding property "+name+" to sites "+site, ftErrors.exceptionerr, e);
        }
    }
}
