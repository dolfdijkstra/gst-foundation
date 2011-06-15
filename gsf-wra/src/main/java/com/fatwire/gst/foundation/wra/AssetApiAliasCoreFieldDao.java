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

import java.util.Collections;
import java.util.Date;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.runtag.asset.Children;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Dao for dealing with core fields in an alias. Aliases may override fields in
 * their target WRA if it points to another asset. Aliases may also refer to
 * external URLs.
 * 
 * @author Tony Field
 * @since Jul 21, 2010
 */
public class AssetApiAliasCoreFieldDao implements AliasCoreFieldDao {

    public static final String TARGET_ASSOCIATION_NAME = "target";
    private final ICS ics;
    private final WraCoreFieldDao wraCoreFieldDao;


    public AssetApiAliasCoreFieldDao(ICS ics, WraCoreFieldDao wraCoreFieldDao) {
        this.ics = ics;
        this.wraCoreFieldDao = wraCoreFieldDao;
    }


    private static final Log LOG = LogFactory.getLog(AssetApiAliasCoreFieldDao.class);

    /**
     * Return an AssetData object containing the core fields found in an alias
     * asset.
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
     * @param id id of alias asset
     * @return AssetData containing core fields for Alias asset
     */
    public AssetData getAsAssetData(AssetId id) {
        return AssetDataUtils.getAssetData(id); // load all instead of specific
                                                // fields, because we do not
                                                // know which fields are set.
    }

    /**
     * Method to test whether or not an asset is an Alias. todo: low priority:
     * optimize as this will be called at runtime
     * 
     * @param id asset ID to check
     * @return true if the asset is a valid Alias asset, false if it is not
     */
    public boolean isAlias(AssetId id) {
        if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType()) == false)
            return false;
        try {
            getAlias(id);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Return an alias asset bean given an input id. Required fields must be set
     * or an exception is thrown.
     * 
     * @param id asset id
     * @return Alias
     * @see #isAlias
     */
    public Alias getAlias(AssetId id) {
        TemplateAsset alias = new TemplateAsset(getAsAssetData(id));
        AssetId target = Children.getOptionalSingleAssociation(ics, id.getType(), Long.toString(id.getId()), TARGET_ASSOCIATION_NAME);
        if (target == null) {
            String targetUrl = alias.asString("target_url");
            if (targetUrl != null && targetUrl.length() == 0)
                throw new IllegalStateException(
                        "Asset is not an alias because it has neither a target_url attribute nor a target named association: "
                                + id);
            LOG.trace("Alias " + id + " refers to an external URL");

            AliasBeanImpl o = new AliasBeanImpl();
            // Wra fields
            o.setId(id);
            o.setName(alias.asString("name"));
            o.setDescription(alias.asString("description"));
            o.setSubtype("GSTAlias");
            o.setStatus(alias.asString("status"));
            o.setStartDate(alias.asDate("startdate"));
            o.setEndDate(alias.asDate("enddate"));
            String linktext = alias.asString("linktext");
            o.setLinkText(goodString(linktext) ? linktext : o.getH1Title());

            // these fields really don't make much sense in an external link...
            if (alias.isAttribute("h1title"))
                o.setH1Title(alias.asString("h1title"));
            if (alias.isAttribute("metatitle"))
                o.setMetaTitle(alias.asString("metatitle"));
            if (alias.isAttribute("metadescription"))
                o.setMetaDescription(alias.asString("metadescription"));
            if (alias.isAttribute("metakeyword"))
                o.setMetaKeyword(alias.asString("metakeyword"));
            o.setPath(alias.asString("path"));
            o.setTemplate(alias.asString("template"));

            // Alias fields
            o.setTargetUrl(targetUrl);
            if (alias.isAttribute("popup"))
                o.setPopup(alias.asString("popup"));
            if (alias.isAttribute("linkimage"))
                o.setLinkImage(alias.asAssetId("linkimage"));
            return o;

        } else {
            if (!wraCoreFieldDao.isWebReferenceable(target)) {
                throw new IllegalStateException(
                        "Asset is not a valid alias because it refers to a target asset that is not web-referenceable. Alias:"
                                + id + ", target:" + target);
            }
            WebReferenceableAsset wra = wraCoreFieldDao.getWra(target);
            LOG.trace("Alias " + id + " refers to another wra asset: " + target);

            AliasBeanImpl o = new AliasBeanImpl();
            // Wra fields
            o.setId(id);
            if (LOG.isTraceEnabled())
                LOG.trace("alias name: " + alias.asString("name") + ", wra name:" + wra.getName());
            o.setName(alias.asString("name"));
            if (LOG.isTraceEnabled())
                LOG.trace("alias description: " + alias.asString("description") + ", wra description:"
                        + wra.getDescription());
            o.setDescription(alias.asString("description"));
            o.setSubtype("GSTAlias");
            if (LOG.isTraceEnabled())
                LOG.trace("alias status: " + alias.asString("status") + ", wra status:" + wra.getStatus());
            o.setStatus(alias.asString("status"));

            Date d = alias.asDate("startdate");
            o.setStartDate(d == null ? wra.getStartDate() : d);
            d = alias.asDate("enddate");
            o.setEndDate(d == null ? wra.getEndDate() : d);

            if (alias.isAttribute("h1title")) {
                String s = alias.asString("h1title");
                o.setH1Title(s == null ? wra.getH1Title() : s);
            }

            if (alias.isAttribute("linktext")) {
                String s = alias.asString("linktext");
                o.setLinkText(s == null ? wra.getLinkText() : s);
            }

            if (alias.isAttribute("metatitle")) {
                String s = alias.asString("metatitle");
                if (!goodString(s))
                    s = wra.getMetaTitle();
                o.setMetaTitle(s);
            }

            if (alias.isAttribute("metadescription")) {
                String s = alias.asString("metadescription");
                if (!goodString(s))
                    s = wra.getMetaDescription();
                o.setMetaDescription(s);
            }

            if (alias.isAttribute("metakeyword")) {
                String s = alias.asString("metakeyword");
                if (!goodString(s))
                    s = wra.getMetaKeyword();
                o.setMetaKeyword(s);
            }

            String s = alias.asString("path");
            if (LOG.isTraceEnabled())
                LOG.trace("alias path: " + s + ", wra path:" + wra.getPath());
            if (!goodString(s))
                s = wra.getPath();
            o.setPath(s);

            s = alias.asString("template");
            if (LOG.isTraceEnabled())
                LOG.trace("alias template: " + s + ", wra template:" + wra.getTemplate());
            if (!goodString(s))
                s = wra.getTemplate();
            o.setTemplate(s);

            // Alias fields
            o.setTarget(target);
            if (alias.isAttribute("popup"))
                o.setPopup(alias.asString("popup"));
            if (alias.isAttribute("linkimage"))
                o.setLinkImage(alias.asAssetId("linkimage"));

            return o;
        }

    }

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

}
