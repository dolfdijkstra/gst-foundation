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

package tools.gsf.facade.assetapi.asset;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.mda.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.assetapi.AttributeDataUtils;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An asset that has loaded the attributes into memory. In the constructor all
 * the attributes are copied into memory and and can be accessed via the
 * {@link Map} methods.
 * <p>
 * This class implements Serializable interface so the object can be serialized
 * if needed. The serialization use-case is the best use-case for this class.
 * The {@link AssetMapAdapter} is a better candidate to use if you are
 * interested in accessing attribute data as a Map, for instance in a expression
 * language like JSP EL.
 *
 * @author Dolf.Dijkstra
 * @see "AssetMapAdapter"
 * @since Nov 23, 2009
 */

public class ScatteredAsset extends AbstractMap<String, Object> implements Serializable {
    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.assetapi.asset.ScatteredAsset");

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
     * @param delegate   asset data object
     * @param attributes the names of attributes to load the data from into
     *                   memory
     */
    public ScatteredAsset(AssetData delegate, String... attributes) {
        this(delegate, true, attributes);

    }

    /**
     * Reads all the attributes, in case of name collisions meta attributes take
     * precedence.
     *
     * @param delegate asset data object
     */
    public ScatteredAsset(AssetData delegate) {
        this(delegate, true, delegate.getAttributeNames().toArray(new String[0]));
    }

    /**
     * This constructor checks if the attributes are meta attributes and based
     * on the passed in <tt>meta</tt> asks for the meta value.
     *
     * @param delegate   asset data object
     * @param meta       true if the attributes are meta attributes
     * @param attributes the names of attributes to load the data from into
     *                   memory
     */

    public ScatteredAsset(AssetData delegate, boolean meta, String... attributes) {
        super();
        id = delegate.getAssetId();
        Set<String> metaAttributes = new HashSet<String>();
        for (AttributeDef d : delegate.getAssetTypeDef().getAttributeDefs()) {
            if (d.isMetaDataAttribute() == meta) {
                metaAttributes.add(d.getName());
            }
        }
        for (String name : attributes) {
            AttributeData attr = delegate.getAttributeData(name, metaAttributes.contains(name) == meta);
            if ("Dimension".equals(name)) {
                Dimension s = AttributeDataUtils.asDimension(attr);
                if (s != null) {
                    attrMap.put(name, s);
                }

            } else if (AttributeDataUtils.isSingleValued(attr)) {
                extractSingleValue(name, attr);
            } else {
                extractMultiValue(name, attr);
            }
        }
        extractParents(delegate);
    }

    private void extractParents(AssetData delegate) {

        List<AttributeDef> parentDefs = delegate.getAssetTypeDef().getParentDefs();

        if (parentDefs != null) {
            for (AttributeDef p : parentDefs) {
                String name = p.getName();
                try {
                    List<AssetId> parentIds = delegate.getImmediateParents(name);

                    attrMap.put("Group_" + name, parentIds);
                } catch (AssetAccessException e) {
                    LOG.debug(e.getMessage() + " when collecting parent " + name + " on " + delegate.getAssetId());
                }
            }

        }

    }

    /**
     * @param name attribute name
     * @param attr attribute data
     */
    private void extractSingleValue(String name, AttributeData attr) {
        switch (attr.getType()) {

            case STRING:
            case LARGE_TEXT:
                String s = AttributeDataUtils.asString(attr);
                if (s != null && s.length() > 0) {
                    attrMap.put(name, s);
                }
                break;

            case INT: {
                Integer obj = AttributeDataUtils.asInt(attr);
                if (obj != null) {
                    attrMap.put(name, obj);
                }
                break;
            }

            case LONG: {
                Long obj = AttributeDataUtils.asLong(attr);
                if (obj != null) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case MONEY:
            case FLOAT: {
                Double obj = AttributeDataUtils.asDouble(attr);
                if (obj != null) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case DATE: {
                Date obj = AttributeDataUtils.asDate(attr);
                if (obj != null) {
                    attrMap.put(name, obj);
                }
                break;
            }

            case ASSET:
            case ASSETREFERENCE: {
                AssetId obj = AttributeDataUtils.asAssetId(attr);
                if (obj != null) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case BLOB:
            case URL: {
                BlobObject blob = AttributeDataUtils.asBlob(attr);
                if (blob != null) {
                    attrMap.put(name, blob);
                }
                break;
            }

            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                Object o = attr.getData();
                int size = 0;
                if (o instanceof Collection<?>) {
                    size = ((Collection<?>) o).size();
                } else if (o instanceof Map<?, ?>) {
                    size = ((Map<?, ?>) o).size();
                } else {
                    LOG.debug("Attribute '" + name + "' of type  " + attr.getType() + " returned a "
                            + o.getClass().getName());
                    size = 1;
                }
                if (size > 0) {
                    attrMap.put(name, attr.getData());
                }
        }
    }

    /**
     * @param name attribute name
     * @param attr attribute data
     */
    private void extractMultiValue(String name, AttributeData attr) {
        switch (attr.getType()) {

            case STRING:
            case LARGE_TEXT:
                List<String> s = AttributeDataUtils.asStringList(attr);
                if (s != null && s.size() > 0) {
                    attrMap.put(name, s);
                }
                break;

            case INT: {
                List<Integer> obj = AttributeDataUtils.asIntList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }

            case LONG: {
                List<Long> obj = AttributeDataUtils.asLongList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case MONEY:
            case FLOAT: {
                List<Double> obj = AttributeDataUtils.asDoubleList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case DATE: {
                List<Date> obj = AttributeDataUtils.asDateList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }

            case ASSET:
            case ASSETREFERENCE: {
                List<AssetId> obj = AttributeDataUtils.asAssetIdList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }
            case BLOB:
            case URL: {
                List<BlobObject> obj = AttributeDataUtils.asBlobList(attr);
                if (obj != null && obj.size() > 0) {
                    attrMap.put(name, obj);
                }
                break;
            }

            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                Object o = attr.getData();
                int size = 0;
                if (o instanceof Collection<?>) {
                    size = ((Collection<?>) o).size();
                } else if (o instanceof Map<?, ?>) {
                    size = ((Map<?, ?>) o).size();
                } else {
                    LOG.debug("Attribute '" + name + "' of type  " + attr.getType() + " returned a "
                            + o.getClass().getName());
                    size = 1;
                }
                if (size > 0) {
                    attrMap.put(name, attr.getData());
                }
        }
    }

    /**
     * @return the id of this asset
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
