/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.wra;

import java.util.Arrays;
import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dao for dealing with core fields in a WRA This DAO is not aware of Aliases.
 * To work with Alias assets, see {@link AliasCoreFieldDao}.}
 * 
 * @author Tony Field
 * @since Jul 21, 2010
 */
public class AssetApiWraCoreFieldDao implements WraCoreFieldDao {

    public static final AssetApiWraCoreFieldDao getInstance(ICS ics) {
        if (ics == null) {
            throw new IllegalArgumentException("ics must not be null.");
        }

        Object o = ics.GetObj(AssetApiWraCoreFieldDao.class.getName());
        if (o == null) {
            o = new AssetApiWraCoreFieldDao(ics);
            ics.SetObj(AssetApiWraCoreFieldDao.class.getName(), o);
        }
        return (AssetApiWraCoreFieldDao) o;
    }

    private final ICS ics;

    public AssetApiWraCoreFieldDao(ICS ics) {
        this.ics = ics;
    }

    private static final Log LOG = LogFactory.getLog(AssetApiWraCoreFieldDao.class);

    /**
     * Return an AssetData object containing the core fields found in a
     * web-referenceable asset.
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
    private AssetData getAsAssetData(AssetId id) {
        return AssetDataUtils.getAssetData(ics, id, WRA_ATTRIBUTE_NAMES);
    }

    /**
     * Method to test whether or not an asset is web-referenceable.
     * 
     * @param id asset ID to check
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(AssetId id) {
        try {
            WebReferenceableAsset wra = getWra(id);
            return isWebReferenceable(wra);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isWebReferenceable(TemplateAsset candidate) {
        return isWebReferenceable(new WraBeanImpl(candidate));
    }

    /**
     * Method to test whether or not an asset is web-referenceable. 
     *
     * @param candidate asset to test
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(WebReferenceableAsset candidate) {
        return StringUtils.isNotBlank(candidate.getPath()) && StringUtils.isNotBlank(candidate.getTemplate());
    }

    /**
     * Return a web referenceable asset bean given an input id. Required fields
     * must be set or an exception is thrown.
     * 
     * @param id asset id
     * @return WebReferenceableAsset, never null
     * @see #isWebReferenceable
     */
    public WebReferenceableAsset getWra(AssetId id) {
        AssetData data = getAsAssetData(id);
        return mapper.map(data);
    }

    private AssetMapper<WebReferenceableAsset> mapper = new AssetMapper<WebReferenceableAsset>() {

        public WebReferenceableAsset map(AssetData data) {
            WraBeanImpl wra = new WraBeanImpl();
            wra.setId(data.getAssetId());
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
            wra.setLinkText(AttributeDataUtils.getWithFallback(data, "linktext", "h1title"));
            wra.setPath(AttributeDataUtils.asString(data.getAttributeData("path")));
            wra.setTemplate(AttributeDataUtils.asString(data.getAttributeData("template")));
            return wra;
        }

    };
    private AssetMapper<VanityAsset> vmapper = new AssetMapper<VanityAsset>() {

        public VanityAsset map(AssetData data) {
            VanityAssetBean wra = new VanityAssetBean();
            wra.setId(data.getAssetId());
            wra.setName(AttributeDataUtils.getWithFallback(data, "name"));
            wra.setDescription(AttributeDataUtils.asString(data.getAttributeData("description")));
            wra.setSubtype(AttributeDataUtils.asString(data.getAttributeData("subtype")));
            wra.setStatus(AttributeDataUtils.asString(data.getAttributeData("status")));
            wra.setStartDate(AttributeDataUtils.asDate(data.getAttributeData("startdate")));
            wra.setEndDate(AttributeDataUtils.asDate(data.getAttributeData("enddate")));
            wra.setPath(AttributeDataUtils.asString(data.getAttributeData("path")));
            wra.setTemplate(AttributeDataUtils.asString(data.getAttributeData("template")));
            return wra;
        }

    };

    private static final String ASSETPUBLICATION_QRY = "SELECT p.name from Publication p, AssetPublication ap "
            + "WHERE ap.assettype = ? " + "AND ap.assetid = ? " + "AND ap.pubid=p.id";
    static final PreparedStmt AP_STMT = new PreparedStmt(ASSETPUBLICATION_QRY,
            Collections.singletonList("AssetPublication")); // todo: low
                                                            // priority:
    // determine
    // why publication
    // cannot fit there.

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
                LOG.warn("Found asset "
                        + c
                        + ":"
                        + cid
                        + " in more than one publication. It should not be shared; aliases are to be used for cross-site sharing.  Controller will use first site found: "
                        + result);
            } else {
                result = pubid.getString("name");
            }
        }
        return result;
    }

    static final PreparedStmt FIND_P = new PreparedStmt(
            "SELECT art.oid FROM AssetRelationTree art, AssetPublication ap, Publication p WHERE ap.assetid = art.oid "
                    + "AND ap.assettype = 'Page' AND ap.pubid = p.id AND p.name = ? "
                    + "AND art.otype = ap.assettype AND art.nid in "
                    + "(SELECT nparentid FROM AssetRelationtree WHERE otype=? AND oid=? AND ncode='-') ORDER BY ap.id",
            Arrays.asList("AssetRelationTree", "AssetPublication", "Publication"));

    static {
        FIND_P.setElement(0, "Publication", "name");
        FIND_P.setElement(1, "AssetRelationTree", "otype");
        FIND_P.setElement(2, "AssetRelationTree", "oid");
    }

    /**
     * Locate the page that contains the specified Web-Referenceable Asset.
     * <p/>
     * A WRA is supposed to just be placed on one page (in the unnamed
     * association block), and this method locates it. If it is not found, 0L is
     * returned.
     * <p/>
     * If multiple matches are found, a warning is logged and the first one is
     * returned.
     * 
     * @param wraAssetIdWithSite id of the web-referenceable asset. Site is
     *            included
     * @return page asset ID or 0L.
     */
    public long findP(AssetIdWithSite wraAssetIdWithSite) {
        ics.ClearErrno();
        final StatementParam param = FIND_P.newParam();
        param.setString(0, wraAssetIdWithSite.getSite());
        param.setString(1, wraAssetIdWithSite.getType());
        param.setLong(2, wraAssetIdWithSite.getId());
        long result = 0L;
        for (final Row r : SqlHelper.select(ics, FIND_P, param)) {
            if (result != 0L)
                LOG.warn("Asset "
                        + wraAssetIdWithSite
                        + " was found as the primary content on multiple pages in the site.  Web-referenceable assets should only be the primary content on one Page to comply with SEO rules.  Automatically selecting the oldest page");
            else
                result = r.getLong("oid");
        }
        if (result == 0L) {
            // could not locate
        }
        return result;

    }

    @Override
    public boolean isVanityAsset(AssetId id) {
        try {
            AssetData data = AssetDataUtils.getAssetData(ics, id, "path", "template");
            String path = AttributeDataUtils.asString(data.getAttributeData("path"));
            String template = AttributeDataUtils.asString(data.getAttributeData("template"));
            return StringUtils.isNotBlank(path) && StringUtils.isNotBlank(template);
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public VanityAsset getVanityWra(AssetId id) {
        AssetData data = AssetDataUtils.getAssetData(ics, id, VANITY_ATTRIBUTE_NAMES);
        return vmapper.map(data);
    }

}
