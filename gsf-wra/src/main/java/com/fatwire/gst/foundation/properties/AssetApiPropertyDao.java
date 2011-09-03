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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.query.Condition;
import com.fatwire.assetapi.query.ConditionFactory;
import com.fatwire.assetapi.query.OpTypeEnum;
import com.fatwire.assetapi.query.SimpleQuery;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.system.SessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class representing properties stored as an asset.  Can be basic or flex but fields are specifically defined
 *
 * @author Tony Field
 * @since 11-09-02
 */
public final class AssetApiPropertyDao implements PropertyDao {
    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.properties");

    public static final String TYPE = "GSTProperty";
    public static final String SUBTYPE = "GSTProperty";
    public static final String[] ATTRS = {"name", "description", "value"};
    private static final Condition COND = ConditionFactory.createCondition("status", OpTypeEnum.NOT_EQUALS, "VO"); // not valid for flex?
    //private static final SimpleQuery LOAD_ALL_QRY = new SimpleQuery(TYPE, SUBTYPE, COND, Arrays.asList(ATTRS));
    private static final SimpleQuery LOAD_ALL_QRY = new SimpleQuery(TYPE, SUBTYPE, null, Arrays.asList(ATTRS));

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
        AssetDataManager mgr = (AssetDataManager) SessionFactory.getSession(ics).getManager(AssetDataManager.class.getName());
        try {
            LOG.trace("Loading all GSTProperties");
            for (AssetData d : mgr.read(LOAD_ALL_QRY)) {
                String name = AttributeDataUtils.asString(d.getAttributeData("name"));
                PropertyImpl p = new PropertyImpl(name, AttributeDataUtils.asString(d.getAttributeData("description")), AttributeDataUtils.asString(d.getAttributeData("value")));
                _props.put(name, p);
                if (LOG.isTraceEnabled()) LOG.trace("Loaded property: "+p);
            }
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failure loading property asset", ftErrors.exceptionerr, e);
        }
    }

    public Property getProperty(String name) {
        return _props.get(name);
    }

    public Collection<String> getPropertyNames() {
        return _props.keySet();
    }
}
