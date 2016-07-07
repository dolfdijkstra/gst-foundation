/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package com.fatwire.gst.foundation.navigation;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AssetTypeDef;
import com.fatwire.assetapi.def.AttributeTypeEnum;
import com.fatwire.mda.Dimension;

import java.util.Date;
import java.util.List;

/**
 * Simple node, representing an asset, that can be populated with asset data. Not all attributes
 * of the asset are necessarily loaded into this node. Many convenience methods exist for retrieving
 * node attribute data.
 * @author Tony Field
 * @since 2016-07-04.
 * @see com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
 */
public interface AssetNode<NODE extends AssetNode> extends Node<NODE, AssetId> {

    AssetId asAssetId(String name);
    BlobObject asBlob(String name);
    BlobObject.BlobAddress asBlobAddress(String name);
    Date asDate(String name);
    Double asDouble(String name);
    Float asFloat(String name);
    Integer asInt(String name);
    List<?> asList(String name);
    Long asLong(String name);
    String asString(String name);
    AssetTypeDef getAssetTypeDef();
    AssetId getAssociatedAsset(String name);
    List<AssetId> getAssociatedAssets(String name);
    Object getAttribute(String name);
    AssetData getAssetData();
    AttributeData getAttributeData(String name, boolean meta);
    List<String> getAttributeNames();
    List<AssetId> getImmediateParents(String name) throws AssetAccessException;
    Dimension getLocale();
    Object getMetaAttribute(String name);
    List<String> getMetaAttributeNames();
    List<AssetId> getParents() throws AssetAccessException;
    String getSubtype();
    AttributeTypeEnum getType(String name);
    boolean isAttribute(String name);
    boolean isSingleValued(String name);
}
