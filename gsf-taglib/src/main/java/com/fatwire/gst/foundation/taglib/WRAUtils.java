package com.fatwire.gst.foundation.taglib;

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.facade.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.facade.wra.WraCoreFieldDao;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class WRAUtils {
    private ICS ics;
    private WraCoreFieldDao dao;

    public WRAUtils(ICS ics) {
        this.ics = ics;
        dao = new WraCoreFieldDao();
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

        WebReferenceableAsset wra = dao.getWra(new AssetIdImpl(c, Long.valueOf(cid)));
        coreFields.put("metatitle", wra.getMetaTitle());
        coreFields.put("metadescription", wra.getMetaDescription());
        coreFields.put("metakeyword", wra.getMetaKeyword());
        coreFields.put("h1title", wra.getH1Title());
        coreFields.put("linktitle", wra.getLinkTitle());
        coreFields.put("path", wra.getPath());
        coreFields.put("template", wra.getTemplate());
        // include bonus fields
        coreFields.put("id", cid);
        coreFields.put("name", wra.getName());
        coreFields.put("subtype", wra.getSubtype()); // maybe null
        if (wra.getStartDate() != null)
            coreFields.put("startdate", Util.formatJdbcDate(wra.getStartDate())); // maybe null.... note not a Date object
        if (wra.getEndDate() != null)
            coreFields.put("enddate", Util.formatJdbcDate(wra.getEndDate())); // maybe null.... note not a Date object
        coreFields.put("status", wra.getStatus());

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
        return dao.getAsAssetData(id);
    }
}
