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
package com.fatwire.gst.foundation.url.db;

import java.util.Collections;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;

import com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn.Type;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.vwebroot.VirtualWebrootDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WraPathTranslationService that is backed by the GSTUrlRegistry table.
 * 
 * @author Dolf.Dijkstra
 * @since Jun 17, 2010
 * @deprecated use {@link UrlRegistry2}
 */
 @Deprecated
public class UrlRegistry implements WraPathTranslationService {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.foundation.url.db.UrlRegistry");

    private final ICS ics;
    private final WraCoreFieldDao wraDao;
    private final VirtualWebrootDao vwDao;
    private static final String URLREG_TABLE = "GSTUrlRegistry";
    public static String TABLE_ACL_LIST = ""; // no ACLs because events are

    // anonymous

    /**
     * 
     * @param ics Content Server context object
     * @param wraDao WRA Core Field DAO
     * @param vwDao Virtual Webroot DAO
     * @deprecated check latest build notes
     */
   @Deprecated
    public UrlRegistry(final ICS ics, final WraCoreFieldDao wraDao, final VirtualWebrootDao vwDao) {
        this.ics = ics;
        // Temporarily disable usage of asset APIs in this use case due to a bug
        // in which asset listeners
        // cause a deadlock when the asset API is used.
        this.wraDao = wraDao;// WraCoreFieldApiBypassDao.getBackdoorInstance(ics);
        this.vwDao = vwDao;// new VirtualWebrootApiBypassDao(ics);
        // End temporary deadlock workaround
    }

    public void install() {
        final TableDef def = new TableDef(URLREG_TABLE, TABLE_ACL_LIST, ftMessage.objecttbl);

        def.addColumn(new TableColumn("id", Type.ccbigint, true).setNullable(false));
        def.addColumn(new TableColumn("path", Type.ccvarchar).setLength(4000).setNullable(false));
        def.addColumn(new TableColumn("assettype", Type.ccvarchar).setLength(255).setNullable(false));
        def.addColumn(new TableColumn("assetid", Type.ccbigint).setNullable(false));
        def.addColumn(new TableColumn("startdate", Type.ccdatetime).setNullable(true));
        def.addColumn(new TableColumn("enddate", Type.ccdatetime).setNullable(true));
        def.addColumn(new TableColumn("opt_vwebroot", Type.ccvarchar).setLength(255).setNullable(true));
        def.addColumn(new TableColumn("opt_url_path", Type.ccvarchar).setLength(4000).setNullable(true));
        def.addColumn(new TableColumn("opt_depth", Type.ccinteger).setNullable(true));
        def.addColumn(new TableColumn("opt_site", Type.ccvarchar).setLength(255).setNullable(true));

        new TableCreator(ics).createTable(def);
    }

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt(
            "SELECT assettype, assetid, startdate, enddate, opt_site FROM " + URLREG_TABLE
                    + " WHERE opt_vwebroot=? AND opt_url_path=? ORDER BY startdate,enddate",
            Collections.singletonList(URLREG_TABLE));
    private static final PreparedStmt REGISTRY_SELECT_ID = new PreparedStmt("SELECT assettype, assetid FROM "
            + URLREG_TABLE + " WHERE assettype=? AND assetid=?", Collections.singletonList(URLREG_TABLE));

    static {
        REGISTRY_SELECT.setElement(0, URLREG_TABLE, "opt_vwebroot");
        REGISTRY_SELECT.setElement(1, URLREG_TABLE, "opt_url_path");
        REGISTRY_SELECT_ID.setElement(0, URLREG_TABLE, "assettype");
        REGISTRY_SELECT_ID.setElement(1, URLREG_TABLE, "assetid");

    }

    @Override
    public AssetIdWithSite resolveAsset(final String virtual_webroot, final String url_path) {
        final StatementParam param = REGISTRY_SELECT.newParam();
        param.setString(0, virtual_webroot);
        param.setString(1, url_path);
        for (final Row asset : SqlHelper.select(ics, REGISTRY_SELECT, param)) {
            final String assettype = asset.getString("assettype");
            final String assetid = asset.getString("assetid");
            final AssetIdWithSite id = new AssetIdWithSite(assettype, Long.parseLong(assetid),
                    asset.getString("opt_site"));
            if (FilterAssetsByDate.isValidOnDate(ics, id, null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved and validated effective date for asset " + id + " from virtual-webroot:"
                            + virtual_webroot + " and url-path:" + url_path);
                }
                return id;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved asset "
                            + id
                            + " but it is not valid on the effective date as determined by the asset.filterassetsbydate tag.");
                }
            }
        }

        return null;
    }

    @Override
    public void addAsset(final AssetId asset) {
        if (wraDao.isVanityAsset(asset)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempting to add WRA " + asset + " to url registry");
            }
            final VanityAsset wra = wraDao.getVanityWra(asset);
            addAsset(wra);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Heard addAsset event for " + asset + " but since it is not a WRA we are ignoring it");
            }
        }
    }

    private void addAsset(final VanityAsset wra) {
        final AssetId asset = wra.getId();

        final VirtualWebroot vw = vwDao.lookupVirtualWebrootForAsset(wra);

        if (vw != null) {
            final String vwebroot = vw.getEnvironmentVirtualWebroot();

            final String urlpath = wra.getPath().substring(vw.getMasterVirtualWebroot().length());
            final int depth = StringUtils.countMatches(urlpath, "/");

            final String site = wraDao.resolveSite(asset.getType(), Long.toString(asset.getId()));

            final FTValList vl = new FTValList();
            vl.setValString("ftcmd", "addrow");
            vl.setValString("tablename", URLREG_TABLE);
            vl.setValString("id", ics.genID(true));
            vl.setValString("path", wra.getPath());
            vl.setValString("assettype", asset.getType());
            vl.setValString("assetid", Long.toString(asset.getId()));
            if (wra.getStartDate() != null) {
                vl.setValString("startdate", Util.formatJdbcDate(wra.getStartDate()));
            }
            if (wra.getEndDate() != null) {
                vl.setValString("enddate", Util.formatJdbcDate(wra.getEndDate()));
            }
            vl.setValString("opt_vwebroot", vwebroot);
            vl.setValString("opt_url_path", urlpath);
            vl.setValString("opt_depth", Integer.toString(depth));
            vl.setValString("opt_site", site);

            if (!ics.CatalogManager(vl) || ics.GetErrno() < 0) {
                throw new CSRuntimeException("Failure adding tag to tag registry", ics.GetErrno());
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Added WRA " + asset + " to url registry");
            }

        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Did not add WRA " + asset + " to url registry because no valid virtual webroot was found");
            }
        }

    }

    @Override
    public void updateAsset(final AssetId id) {
        // todo: low priority: optimize (assest api incache will mitigate the
        // performance issue)

        final StatementParam param = REGISTRY_SELECT_ID.newParam();
        param.setString(0, id.getType());
        param.setLong(1, id.getId());
        final Row row = SqlHelper.selectSingle(ics, REGISTRY_SELECT_ID, param);
        if (row != null) {
            deleteAsset(id);
        }

        if (wraDao.isVanityAsset(id)) {
            addAsset(id);
        }
    }

    @Override
    public void deleteAsset(final AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Attempting to delete asset " + id
                    + " from url registry (it might not have been there but we must try anyway)");
        }
        SqlHelper.execute(ics, URLREG_TABLE, "DELETE FROM " + URLREG_TABLE + " WHERE assettype = '" + id.getType()
                + "' AND assetid = " + id.getId());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Asset " + id + " was either never present or is now removed from url registry");
        }
    }
}
