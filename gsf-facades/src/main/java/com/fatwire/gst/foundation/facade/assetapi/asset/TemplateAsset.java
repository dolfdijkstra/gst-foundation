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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AssetTypeDef;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;

/**
 * 
 * Asset to be used in rendering code. It has easy accessors for the different
 * attribute types.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class TemplateAsset {

    private final AssetData delegate;
    private List<String> metaList = new ArrayList<String>();

    public TemplateAsset(AssetData delegate) {
        super();
        this.delegate = delegate;
        fillMetaAttributes();
    }

    public AssetData getDelegate() {
        return delegate;
    }

    /**
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAssetId()
     */
    public AssetId getAssetId() {
        return delegate.getAssetId();
    }

    /**
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAssetTypeDef()
     */
    public AssetTypeDef getAssetTypeDef() {
        return delegate.getAssetTypeDef();
    }

    /**
     * @param name name of the association
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAssociatedAssets(java.lang.String)
     */
    public List<AssetId> getAssociatedAssets(String name) {
        return delegate.getAssociatedAssets(name);
    }

    /**
     * @param name
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAttributeData(java.lang.String,
     *      boolean)
     */
    public Object getAttribute(String name) {
        return delegate.getAttributeData(name, false).getData();
    }

    /**
     * @param name
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAttributeData(java.lang.String,
     *      boolean)
     */
    public Object getMetaAttribute(String name) {
        return delegate.getAttributeData(name, true).getData();
    }

    /**
     * @param name
     * @return
     */
    public boolean isSingleValued(String name) {
        AttributeDef ad = delegate.getAssetTypeDef().getAttributeDef(name, true);
        if (ad == null) {
            ad = delegate.getAssetTypeDef().getAttributeDef(name, false);
        }
        return isSingleValued(ad);
    }

    private boolean isSingleValued(AttributeDef ad) {
        return AttributeDataUtils.isSingleValued(ad);

    }

    private AttributeData getMetaFirst(String name) {
        return delegate.getAttributeData(name, this.metaList.contains(name));
    }

    /**
     * @param name
     * @return
     */
    public List<?> asList(String name) {
        AttributeData attr = getMetaFirst(name);

        return AttributeDataUtils.asList(attr);
    }

    /*
     * <pre> INT Integer FLOAT Double STRING String DATE Date MONEY Double LONG
     * Long LARGE_TEXT String ASSET AssetId BLOB BlobObject </pre>
     */

    /**
     * @param name
     * @return
     */

    public Integer asInt(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asInt(attr);

    }

    /**
     * @param name
     * @return
     */
    public Date asDate(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asDate(attr);
    }

    /**
     * @param name
     * @return
     */
    public BlobObject asBlob(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asBlob(attr);
    }

    /**
     * @param name
     * @return
     */
    public Float asFloat(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asFloat(attr);
    }

    /**
     * @param name
     * @return
     */
    public Double asDouble(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asDouble(attr);
    }

    /**
     * @param name
     * @return
     */
    public Long asLong(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asLong(attr);
    }

    /**
     * @param name
     * @return
     */
    public AssetId asAssetId(String name) {

        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asAssetId(attr);
    }

    /**
     * @param name
     * @return
     */
    public String asString(String name) {
        AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asString(attr);
    }

    /**
     * @return the name of all the attributes of the asset
     * @see com.fatwire.assetapi.data.AssetData#getAttributeNames()
     */
    public List<String> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    /**
     * @return the name of all the attributes of the asset
     * @see com.fatwire.assetapi.data.AssetData#getAttributeNames()
     */
    public List<String> getMetaAttributeNames() {

        return metaList;
    }

    private void fillMetaAttributes() {
        for (AttributeDef def : delegate.getAssetTypeDef().getAttributeDefs()) {
            if (def.isMetaDataAttribute())
                metaList.add(def.getName());
        }
    }

    /**
     * @param name
     * @return
     * @throws AssetAccessException
     * @see com.fatwire.assetapi.data.AssetData#getImmediateParents(java.lang.String)
     */
    public List<AssetId> getImmediateParents(String name) throws AssetAccessException {
        return delegate.getImmediateParents(name);
    }

    /**
     * @return the parents of the asset
     * @throws AssetAccessException
     * @see com.fatwire.assetapi.data.AssetData#getParents()
     */
    public List<AssetId> getParents() throws AssetAccessException {
        return delegate.getParents();
    }

    /**
     * @param name
     * @param meta
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAttributeData(java.lang.String,
     *      boolean)
     */
    public AttributeData getAttributeData(String name, boolean meta) {
        return delegate.getAttributeData(name, meta);
    }

}
