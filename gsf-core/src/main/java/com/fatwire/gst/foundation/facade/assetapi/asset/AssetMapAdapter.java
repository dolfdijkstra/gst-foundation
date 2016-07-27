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

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fatwire.assetapi.def.AttributeDef;

/**
 * This class adapts an <tt>TemplateAsset</tt> into a <tt>Map</tt>. The Map is
 * convenient in expression languages like JSP EL.
 * <p>
 * This class uses lazy-loading, so if used in a View layer, it might open
 * connections to the database.
 * <p>
 * In case of naming conflicts with flex attribute names and meta attribute
 * names the meta attributes take precedent.
 * 
 * 
 * @author Dolf.Dijkstra
 * @since Nov 23,2009
 * 
 */
public class AssetMapAdapter extends AbstractMap<String, Object> implements Map<String, Object> {

    private final TemplateAsset delegate;
    private final Set<String> metaAttributes = new HashSet<String>();

    public AssetMapAdapter(TemplateAsset delegate) {
        super();
        this.delegate = delegate;
        // meta attributes are part of the assettype definition.
        for (AttributeDef d : delegate.getAssetTypeDef().getAttributeDefs()) {
            if (d.isMetaDataAttribute())
                metaAttributes.add(d.getName());
        }

    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return entries;
    }

    private Set<java.util.Map.Entry<String, Object>> entries = new java.util.AbstractSet<java.util.Map.Entry<String, Object>>() {

        @Override
        public Iterator<java.util.Map.Entry<String, Object>> iterator() {
            final Iterator<String> names = delegate.getAttributeNames().iterator();
            return new Iterator<java.util.Map.Entry<String, Object>>() {

                public boolean hasNext() {
                    return names.hasNext();
                }

                public java.util.Map.Entry<String, Object> next() {
                    final String name = names.next();
                    final Object value = delegate.getAttribute(name);
                    return new Entry<String, Object>() {

                        public String getKey() {
                            return name;
                        }

                        public Object getValue() {
                            return value;
                        }

                        public Object setValue(Object value) {
                            throw new UnsupportedOperationException(
                                    "Not allowed to set an asset attribute value, asset is immutable");
                        }

                    };
                }

                public void remove() {
                    throw new UnsupportedOperationException(
                            "Not allowed to remove an asset attribute, asset is immutable");

                }

            };
        }

        @Override
        public int size() {
            return delegate.getAttributeNames().size();
        }
    };
}
