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
import com.fatwire.assetapi.data.BlobObject.BlobAddress;
import com.fatwire.assetapi.def.AssetTypeDef;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.assetapi.def.AttributeTypeEnum;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.mda.Dimension;

/**
 * 
 * This class provides easy access to AssetData, to be used in rendering code in
 * read-only mode. It has casting accessors for values of the different
 * attribute types.
 * <p/>
 * It must be noted that naming conflicts between flex attribute names and meta
 * attribute names are resolved by giving the meta attribute preference.
 * <p/>
 * <code>
 * TemplateAsset asset = ...;
 * String name = asset.asString("name");
 * </code>
 * 
 * @author Dolf Dijkstra
 * 
 */
public class TemplateAsset {

    private final AssetData delegate;
    private final List<String> metaList = new ArrayList<String>();

    /**
     * @param delegate
     */
    public TemplateAsset(final AssetData delegate) {
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
    public List<AssetId> getAssociatedAssets(final String name) {
        return delegate.getAssociatedAssets(name);
    }

    /**
     * @param name
     * @return
     * @see com.fatwire.assetapi.data.AssetData#getAttributeData(java.lang.String,
     *      boolean)
     */
    public Object getAttribute(final String name) {
        return delegate.getAttributeData(name, false).getData();
    }

    /**
     * @param name the name of the attribute.
     * @return the meta attribute value.
     * @see com.fatwire.assetapi.data.AssetData#getAttributeData(java.lang.String,
     *      boolean)
     */
    public Object getMetaAttribute(final String name) {
        return delegate.getAttributeData(name, true).getData();
    }

    /**
     * @param name
     * @return true is the attribute is defined as single valued.
     */
    public boolean isSingleValued(final String name) {
        AttributeDef ad = delegate.getAssetTypeDef().getAttributeDef(name, true);
        if (ad == null) {
            ad = delegate.getAssetTypeDef().getAttributeDef(name, false);
        }
        return isSingleValued(ad);
    }

    private boolean isSingleValued(final AttributeDef ad) {
        return AttributeDataUtils.isSingleValued(ad);

    }

    private boolean isMetaAttribute(final String name) {
        return this.metaList.contains(name);
    }

    private AttributeData getMetaFirst(final String name) {
        return delegate.getAttributeData(name, isMetaAttribute(name));
    }

    /**
     * @param name
     * @return attribute as a List
     */
    public List<?> asList(final String name) {
        final AttributeData attr = getMetaFirst(name);

        return AttributeDataUtils.asList(attr);
    }

    /*
     * <pre> INT Integer FLOAT Double STRING String DATE Date MONEY Double LONG
     * Long LARGE_TEXT String ASSET AssetId BLOB BlobObject </pre>
     */

    /**
     * @param name
     * @return attribute value as a Integer, can be null; please be careful with
     *         autoboxing.
     */

    public Integer asInt(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asInt(attr);

    }

    /**
     * @param name
     * @return attribute value as a Date.
     */
    public Date asDate(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asDate(attr);
    }

    /**
     * @param name
     * @return attribute value as a BlobObject.
     */
    public BlobObject asBlob(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asBlob(attr);
    }

    /**
     * @param name
     * @return attribute value as a Float, can be null; please be careful with
     *         autoboxing.
     */
    public Float asFloat(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asFloat(attr);
    }

    /**
     * @param name
     * @return attribute value as a Double, can be null; please be careful with
     *         autoboxing.
     */
    public Double asDouble(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asDouble(attr);
    }

    /**
     * @param name
     * @return attribute value as a Long, can be null; please be careful with
     *         autoboxing.
     */
    public Long asLong(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asLong(attr);
    }

    /**
     * @param name
     * @return attribute value as a AssetId.
     */
    public AssetId asAssetId(final String name) {

        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asAssetId(attr);
    }

    /**
     * @param name
     * @return attribute value as a String.
     */
    public String asString(final String name) {
        final AttributeData attr = getMetaFirst(name);
        return AttributeDataUtils.asString(attr);
    }

    /**
     * @param name
     * @return attribute value as a BlobAddress.
     */
    public BlobAddress asBlobAddress(final String name) {
        final BlobObject blob = asBlob(name);
        return blob == null ? null : blob.getBlobAddress();
    }

    /**
     * Get all the attribute names.
     * 
     * @return the name of all the attributes of the asset
     * @see com.fatwire.assetapi.data.AssetData#getAttributeNames()
     */
    public List<String> getAttributeNames() {

        return delegate.getAttributeNames();
    }

    /**
     * Get the type of the attribute.
     * 
     * @param name
     * @return
     */
    public AttributeTypeEnum getType(String name) {
        return delegate.getAssetTypeDef().getAttributeDef(name, isMetaAttribute(name)).getType();
    }

    /**
     * Gets all the names of the meta attributes.
     * 
     * @return the name of all the attributes of the asset
     * @see com.fatwire.assetapi.data.AssetData#getAttributeNames()
     */
    public List<String> getMetaAttributeNames() {

        return metaList;
    }

    private void fillMetaAttributes() {
        for (final AttributeDef def : delegate.getAssetTypeDef().getAttributeDefs()) {
            if (def.isMetaDataAttribute()) {
                metaList.add(def.getName());
            }
        }
    }

    /**
     * @param name
     * @return
     * @throws AssetAccessException
     * @see com.fatwire.assetapi.data.AssetData#getImmediateParents(java.lang.String)
     */
    public List<AssetId> getImmediateParents(final String name) throws AssetAccessException {
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
    public AttributeData getAttributeData(final String name, final boolean meta) {
        return delegate.getAttributeData(name, meta);
    }

    public Dimension getLocale() {
        AttributeData dim = getAttributeData("Dimension", true);
        if (dim == null)
            return null;
        return AttributeDataUtils.asDimension(dim);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TemplateAsset))
            return false;
        TemplateAsset other = (TemplateAsset) obj;
        if (delegate == null) {
            if (other.delegate != null)
                return false;
        } else if (!delegate.equals(other.delegate))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TemplateAsset [getAssetId()=" + getAssetId() + "]";
    }

}
