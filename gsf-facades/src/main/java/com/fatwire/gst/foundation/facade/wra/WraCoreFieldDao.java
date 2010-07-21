/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.wra;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;

/**
 * Dao for dealing with core fields in a WRA
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public class WraCoreFieldDao {

    /**
     * Return an AssetData object containing the core fields found in a web-referenceable asset.
     * <p/>
     * Also includes selected metadata fields:
     * <ul>
     * <li>id</li>
     * <li>name</li>
     * <li>subtype</li>
     * <li>startdate</li>
     * <li>enddate</li>
     * <li>status</li>
     * </ul>
     *
     * @param id id of web-referenceable asset
     * @return AssetData containing core fields for Web-Referencable asset
     */
    public AssetData getAsAssetData(AssetId id) {
        return AssetDataUtils.getAssetData(id, "metatitle", "metadescription", "metakeyword", "h1title", "linktitle", "path", "template", "id", "name", "subtype", "startdate", "enddate", "status");
    }

    public WebReferenceableAsset getWra(AssetId id) {
        AssetData data = getAsAssetData(id);
        WraBeanImpl wra = new WraBeanImpl();
        wra.setId(id);
        wra.setName(AttributeDataUtils.getWithFallback(data, "name"));
        wra.setDescription(AttributeDataUtils.asString(data.getAttributeData("description")));
        wra.setSubtype(AttributeDataUtils.asString(data.getAttributeData("subtype")));
        wra.setStatus(AttributeDataUtils.asString(data.getAttributeData("status")));
        wra.setStartDate(AttributeDataUtils.asDate(data.getAttributeData("startdate")));
        wra.setEndDate(AttributeDataUtils.asDate(data.getAttributeData("enddate")));
        wra.setMetaTitle(AttributeDataUtils.getWithFallback(data, "metatitle"));
        wra.setMetaDescription(AttributeDataUtils.getWithFallback(data, "metadescription"));
        wra.setMetaKeyword(AttributeDataUtils.getWithFallback(data, "metakeyword"));
        wra.setH1Title(AttributeDataUtils.getWithFallback(data, "h1title"));
        wra.setLinkTitle(AttributeDataUtils.getWithFallback(data, "linktitle", "h1title"));
        wra.setPath(AttributeDataUtils.getWithFallback(data, "path"));
        wra.setTemplate(AttributeDataUtils.getWithFallback(data, "template"));
        return wra;
    }

}
