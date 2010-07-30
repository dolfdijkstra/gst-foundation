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
package com.fatwire.gst.foundation.facade.wra;

import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dao for dealing with core fields in a WRA
 * todo: handle aliases cleanly
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public class WraCoreFieldDao {

    private final ICS ics;

    public WraCoreFieldDao() {
        this.ics = ICSFactory.newICS();
    }

    public WraCoreFieldDao(ICS ics) {
        this.ics = ics;
    }

    private static final Log LOG = LogFactory.getLog(WraCoreFieldDao.class);

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

    /**
     * Method to test whether or not an asset is web-referenceable.
     * todo: optimize as this will be called at runtime
     *
     * @param id asset ID to check
     * @return true if the asset is a valid web-referenceable asset, false if it is not
     */
    public boolean isWebReferenceable(AssetId id) {
        try {
            getWra(id);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Return a web referenceable asset bean given an input id.  Required fields must be set or an exception
     * is thrown.
     *
     * @param id asset id
     * @return WebReferenceableAsset
     * @see #isWebReferenceable
     */
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
        wra.setMetaKeyword(AttributeDataUtils.asString(data.getAttributeData("metakeyword")));
        wra.setH1Title(AttributeDataUtils.getWithFallback(data, "h1title"));
        wra.setLinkTitle(AttributeDataUtils.getWithFallback(data, "linktitle", "h1title"));
        wra.setPath(AttributeDataUtils.asString(data.getAttributeData("path")));
        wra.setTemplate(AttributeDataUtils.asString(data.getAttributeData("template")));
        return wra;
    }

    private static final String ASSETPUBLICATION_QRY = "SELECT p.name from Publication p, AssetPublication ap " + "WHERE ap.assettype = ? " + "AND ap.assetid = ? " + "AND ap.pubid=p.id";
    static final PreparedStmt AP_STMT = new PreparedStmt(ASSETPUBLICATION_QRY, Collections.singletonList("AssetPublication")); // todo:determine why publication cannot fit there.

    static {
        AP_STMT.setElement(0, "AssetPublication", "assettype");
        AP_STMT.setElement(1, "AssetPublication", "assetid");
    }

    public String resolveSite(String c, String cid) {
        final StatementParam param = AP_STMT.newParam();
        param.setString(0, c);
        param.setLong(1, Long.parseLong(cid));
        String result = null;
        for (Row pubid : SqlHelper.select(ics, AP_STMT, param)) {
            if (result != null) {
                LOG.warn("Found asset " + c + ":" + cid + " in more than one publication. It should not be shared; aliases are to be used for cross-site sharing.  Controller will use first site found: " + result);
            } else {
                result = pubid.getString("name");
            }
        }
        return result;
    }


}
