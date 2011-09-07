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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.Query;
import com.fatwire.gst.foundation.facade.assetapi.QueryBuilder;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class representing properties stored as an asset. Can be basic or flex but
 * fields are specifically defined
 * 
 * @author Tony Field
 * @since 11-09-02
 */
public final class AssetApiPropertyDao implements PropertyDao {
    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.properties");

    public static final String TYPE = "GSTProperty";
    public static final String SUBTYPE = "GSTProperty";
    private static final Query LOAD_ALL_QRY = new QueryBuilder(TYPE, SUBTYPE)
            .attributes("name", "description", "value").and("status", OpTypeEnum.NOT_EQUALS, "VO").setBasicSearch(true)
            .toQuery();

    private final Map<String, Property> _props;

    public static final PropertyDao getInstance(ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics must not be null.");
        }

        Object o = ics.GetObj(PropertyDao.class.getName());
        if (o == null) {
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
}
