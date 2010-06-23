package com.fatwire.gst.foundation.taglib;

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class WRAUtils {
    private ICS ics;

    public WRAUtils(ICS ics) {
        this.ics = ics;
    }

    /**
     * Return a Map<String,String> containing the core fields in
     * a web-referenceable asset.  This method will automatically
     * substitute h1title as the value for linktitle if linktitle is not supplied.
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
     * @param c   asset type
     * @param cid asset id
     * @return Map<String,String> with attribute data
     */
    public Map<String, String> getCoreFields(String c, String cid) {
        Map<String, String> coreFields = new HashMap<String, String>();

        AssetData data = getCoreFieldsAsAssetData(new AssetIdImpl(c, Long.valueOf(cid)));

        coreFields.put("metatitle", AttributeDataUtils.getWithFallback(data, "metatitle"));
        coreFields.put("metadescription", AttributeDataUtils.getWithFallback(data, "metadescription"));
        coreFields.put("metakeyword", AttributeDataUtils.getWithFallback(data, "metakeyword"));
        coreFields.put("h1title", AttributeDataUtils.getWithFallback(data, "h1title"));
        coreFields.put("linktitle", AttributeDataUtils.getWithFallback(data, "linktitle", "h1title"));
        coreFields.put("path", AttributeDataUtils.getWithFallback(data, "path"));
        coreFields.put("template", AttributeDataUtils.getWithFallback(data, "template"));
        // include bonus fields
        coreFields.put("id", AttributeDataUtils.getWithFallback(data, "id"));
        coreFields.put("name", AttributeDataUtils.getWithFallback(data, "name"));
        coreFields.put("subtype", AttributeDataUtils.getWithFallback(data, "subtype"));
        coreFields.put("startdate", AttributeDataUtils.getWithFallback(data, "startdate"));
        coreFields.put("enddate", AttributeDataUtils.getWithFallback(data, "enddate"));
        coreFields.put("status", AttributeDataUtils.getWithFallback(data, "status"));

        return coreFields;
    }

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
    public AssetData getCoreFieldsAsAssetData(AssetId id) {
        return AssetDataUtils.getAssetData(id, "metatitle", "metadescription", "metakeyword", "h1title", "linktitle",
                "path", "template", "id", "name", "subtype", "startdate", "enddate", "status");
    }
}
