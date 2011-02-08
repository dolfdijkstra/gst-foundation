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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;

/**
 * 
 * Very simple form of an asset, that has loaded the attributes into memory
 * 
 * @author Dolf.Dijkstra
 * @since Nov 23, 2009
 * 
 */

public class ScatteredAsset extends AbstractMap<String, Object> implements Serializable {
    private static final Log log = LogFactory.getLog(ScatteredAsset.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = -4978875079485732671L;

    private final AssetId id;

    private final Map<String, Object> attrMap = new TreeMap<String, Object>();

    /**
     * This constructor checks if the attributes are meta attributes and if so,
     * asks for the meta value. In case that you have a flex attribute with the
     * name 'description' the flex attribute value will not be used, but the
     * primary asset row 'description' field value.
     * 
     * @param delegate
     * @param attributes the names of attributes to load the data from into
     *            memory
     */
    public ScatteredAsset(AssetData delegate, String... attributes) {
        this(delegate, true, attributes);

    }

    /**
     * Reads all the attributes, in case of name collisions meta attributes take
     * precedence.
     * 
     * @param delegate
     */
    public ScatteredAsset(AssetData delegate) {
        this(delegate, true, delegate.getAttributeNames().toArray(new String[0]));
    }

    /**
     * This constructor checks if the attributes are meta attributes and based
     * on the passed in <tt>meta</tt> asks for the meta value.
     * 
     * @param delegate
     * @param meta true if the attributes are meta attributes
     * @param attributes the names of attributes to load the data from into
     *            memory
     */

    public ScatteredAsset(AssetData delegate, boolean meta, String... attributes) {
        super();
        id = delegate.getAssetId();
        Set<String> metaAttributes = new HashSet<String>();
        for (AttributeDef d : delegate.getAssetTypeDef().getAttributeDefs()) {
            if (d.isMetaDataAttribute() == meta)
                metaAttributes.add(d.getName());
        }

        for (String name : attributes) {
            AttributeData attr = delegate.getAttributeData(name, metaAttributes.contains(name) == meta);

            switch (attr.getType()) {

                case STRING:
                case LARGE_TEXT:
                    String s = AttributeDataUtils.asString(attr);
                    if (s != null && s.length() > 0)
                        attrMap.put(name, s);
                    break;

                case INT: {
                    Object obj = AttributeDataUtils.asInt(attr);
                    if (obj != null)
                        attrMap.put(name, obj);
                    break;
                }

                case LONG: {
                    Long obj = AttributeDataUtils.asLong(attr);
                    if (obj != null)
                        attrMap.put(name, obj);
                    break;
                }
                case MONEY:
                case FLOAT: {
                    Object obj = AttributeDataUtils.asFloat(attr);
                    if (obj != null)
                        attrMap.put(name, obj);
                    break;
                }
                case DATE: {
                    Object obj = AttributeDataUtils.asDate(attr);
                    if (obj != null)
                        attrMap.put(name, obj);
                    break;
                }

                case ASSET:
                case ASSETREFERENCE: {
                    Object obj = AttributeDataUtils.asAssetId(attr);
                    if (obj != null)
                        attrMap.put(name, obj);
                    break;
                }
                case BLOB:
                case URL: {
                    BlobObject blob = AttributeDataUtils.asBlob(attr);
                    if (Utilities.goodString(blob.getFilename()))
                        attrMap.put(name, blob.getBlobAddress());
                    break;
                }

                case ARRAY:
                case STRUCT:
                case LIST:
                case ONEOF:
                    Object o = attr.getData();
                    int size = 0;
                    if (o instanceof List) {
                        size = ((List<?>) o).size();
                    } else if (o instanceof Set) {
                        size = ((Set<?>) o).size();
                    } else if (o instanceof Map) {
                        size = ((Map<?, ?>) o).size();
                    } else {
                        log.info(name + " " + o.getClass().getName());
                        size = 1;
                    }
                    if (size > 0)
                        attrMap.put(name, attr.getData());
            }
        }
    }

    /**
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAssetId()
     */
    public AssetId getAssetId() {
        return id;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return attrMap.entrySet();
    }

}
