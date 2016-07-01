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
import java.util.LinkedList;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.cm.AddRow;
import com.fatwire.gst.foundation.facade.cm.ReplaceRow;

import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn.Type;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.wra.SimpleWra;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WraPathTranslationService that is backed by the GSTUrlRegistry table.
 * 
 * @author Dolf Dijkstra
 * @since November 1, 2011
 * 
 * @deprecated May 15, 2016 by fvillalba
 * 
 */
@Deprecated
public class UrlRegistryDaoImpl implements UrlRegistryDao {

    private static final String OPT_SITE = "opt_site";

    private static final String OPT_URL_PATH = "opt_url_path";

    private static final String OPT_VWEBROOT = "opt_vwebroot";

    private static final String OPT_DEPTH = "opt_depth";

    private static final String ENDDATE = "enddate";

    private static final String STARTDATE = "startdate";

    private static final String PATH = "path";

    private static final String ASSETTYPE = "assettype";

    private static final String ASSETID = "assetid";

    private static final String ID = "id";

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.url.db.UrlRegistryDaoImpl");

    private final ICS ics;
    private static final String URLREG_TABLE = "GSTUrlRegistry";
    public static String TABLE_ACL_LIST = ""; // no ACLs because events are
                                              // anonymous

    public UrlRegistryDaoImpl(final ICS ics) {
        this.ics = ics;
    }

    public void install() {
        final TableDef def = new TableDef(URLREG_TABLE, TABLE_ACL_LIST, ftMessage.objecttbl);

        def.addColumn(ID, Type.ccbigint, true).setNullable(false);
        def.addColumn(PATH, Type.ccvarchar).setLength(4000).setNullable(false);
        def.addColumn(ASSETTYPE, Type.ccvarchar).setLength(255).setNullable(false);
        def.addColumn(ASSETID, Type.ccbigint).setNullable(false);
        def.addColumn(STARTDATE, Type.ccdatetime).setNullable(true);
        def.addColumn(ENDDATE, Type.ccdatetime).setNullable(true);
        def.addColumn(OPT_VWEBROOT, Type.ccvarchar).setLength(255).setNullable(true);
        def.addColumn(OPT_URL_PATH, Type.ccvarchar).setLength(4000).setNullable(true);
        def.addColumn(OPT_DEPTH, Type.ccinteger).setNullable(true);
        def.addColumn(OPT_SITE, Type.ccvarchar).setLength(255).setNullable(true);

        new TableCreator(ics).createTable(def);
    }

    public boolean isInstalled() {
        return SqlHelper.tableExists(ics, URLREG_TABLE);
    }

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt("SELECT * FROM " + URLREG_TABLE
            + " WHERE opt_vwebroot=? AND opt_url_path=? ORDER BY startdate,enddate",
            Collections.singletonList(URLREG_TABLE));
    private static final PreparedStmt REGISTRY_SELECT_ID = new PreparedStmt("SELECT * FROM " + URLREG_TABLE
            + " WHERE assettype=? AND assetid=?", Collections.singletonList(URLREG_TABLE));

    static {
        REGISTRY_SELECT.setElement(0, URLREG_TABLE, OPT_VWEBROOT);
        REGISTRY_SELECT.setElement(1, URLREG_TABLE, OPT_URL_PATH);
        REGISTRY_SELECT_ID.setElement(0, URLREG_TABLE, ASSETTYPE);
        REGISTRY_SELECT_ID.setElement(1, URLREG_TABLE, ASSETID);

    }

    @Override
    public List<VanityUrl> resolveAsset(final String virtual_webroot, final String url_path) {
        final StatementParam param = REGISTRY_SELECT.newParam();
        param.setString(0, virtual_webroot);
        param.setString(1, url_path);
        final List<VanityUrl> l = new LinkedList<VanityUrl>();
        for (final Row row : SqlHelper.select(ics, REGISTRY_SELECT, param)) {
            final VanityUrl url = new VanityUrl();
            url.setId(row.getLong(ID));
            url.setAssetid(row.getLong(ASSETID));
            url.setAssettype(row.getString(ASSETTYPE));
            url.setPath(row.getString(PATH));
            url.setStartdate(row.getDate(STARTDATE));
            url.setEnddate(row.getDate(ENDDATE));
            url.setOpt_depth(Integer.parseInt(row.getString(OPT_DEPTH)));
            url.setOpt_vwebroot(row.getString(OPT_VWEBROOT));
            url.setOpt_url_path(row.getString(OPT_URL_PATH));
            url.setOpt_site(row.getString(OPT_SITE));
            l.add(url);
        }

        return l;
    }

    @Override
    public void add(final SimpleWra wra, final VirtualWebroot vw, final String site) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("addAsset(AssetId) called for asset " + wra);
        }
        final AssetId asset = wra.getId();

        if (vw != null) {
            final String vwebroot = vw.getEnvironmentVirtualWebroot();

            final String urlpath = wra.getPath().substring(vw.getMasterVirtualWebroot().length());
            final int depth = StringUtils.countMatches(urlpath, "/");

            AddRow addRow = new AddRow(URLREG_TABLE);
            addRow.set(ID, ics.genID(true));
            addRow.set(PATH, wra.getPath());
            addRow.set(ASSETTYPE, asset.getType());
            addRow.set(ASSETID, Long.toString(asset.getId()));
            addRow.set(STARTDATE, wra.getStartDate());
            addRow.set(ENDDATE, wra.getEndDate());
            addRow.set(OPT_VWEBROOT, vwebroot);
            addRow.set(OPT_URL_PATH, urlpath);
            addRow.set(OPT_DEPTH, Integer.toString(depth));
            addRow.set(OPT_SITE, site);
            addRow.execute(ics);

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
    public void update(final VanityUrl url) {
        final ReplaceRow u = new ReplaceRow(URLREG_TABLE);
        u.set(ID, url.getId());
        u.set(PATH, url.getPath());
        u.set(ASSETTYPE, url.getAssettype());
        u.set(ASSETID, url.getAssetid());
        u.set(STARTDATE, url.getStartdate());
        u.set(ENDDATE, url.getEnddate());
        u.set(OPT_VWEBROOT, url.getOpt_vwebroot());
        u.set(OPT_URL_PATH, url.getOpt_url_path());
        u.set(OPT_DEPTH, url.getOpt_depth());
        u.set(OPT_SITE, url.getOpt_site());
        u.execute(ics);
    }

    @Override
    public VanityUrl read(final AssetId id) {
        final StatementParam param = REGISTRY_SELECT_ID.newParam();
        param.setString(0, id.getType());
        param.setLong(1, id.getId());
        final Row row = SqlHelper.selectSingle(ics, REGISTRY_SELECT_ID, param);
        if (row == null) {
            return null;
        }
        final VanityUrl url = new VanityUrl();
        url.setId(row.getLong(ID));
        url.setAssetid(row.getLong(ASSETID));
        url.setAssettype(row.getString(ASSETTYPE));
        url.setPath(row.getString(PATH));
        url.setStartdate(row.getDate(STARTDATE));
        url.setEnddate(row.getDate(ENDDATE));
        url.setOpt_depth(Integer.parseInt(row.getString(OPT_DEPTH)));
        url.setOpt_vwebroot(row.getString(OPT_VWEBROOT));
        url.setOpt_url_path(row.getString(OPT_URL_PATH));
        url.setOpt_site(row.getString(OPT_SITE));
        return url;

    }

    @Override
    public void delete(final AssetId id) {
        final StatementParam param = REGISTRY_SELECT_ID.newParam();
        param.setString(0, id.getType());
        param.setLong(1, id.getId());
        final Row row = SqlHelper.selectSingle(ics, REGISTRY_SELECT_ID, param);
        if (row != null) {
            //only delete if a record is found to prevent trashing.
            deleteAsset_(id);
        }
    }

    private void deleteAsset_(final AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Attempting to delete asset " + id + " from " + URLREG_TABLE);
        }
        SqlHelper.execute(ics, URLREG_TABLE, "DELETE FROM " + URLREG_TABLE + " WHERE assettype = '" + id.getType()
                + "' AND assetid = " + id.getId());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Asset " + id + " is now removed from " + URLREG_TABLE);
        }
    }

    public void clear() {
        LOG.debug("Attempting to purge all rows from "+URLREG_TABLE);
        SqlHelper.execute(ics, URLREG_TABLE, "DELETE FROM " + URLREG_TABLE);
        LOG.info("Purged all rows from "+URLREG_TABLE);
    }
}
