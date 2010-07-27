package com.fatwire.gst.foundation.url.db;

import java.util.Collections;
import java.util.Date;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn.Type;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.facade.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.facade.vwebroot.VirtualWebrootDao;
import com.fatwire.gst.foundation.facade.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.facade.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.url.WraPathTranslationService;

import static COM.FutureTense.Interfaces.Utilities.goodString;
import static com.fatwire.cs.core.db.Util.parseJdbcDate;
import static com.fatwire.gst.foundation.facade.sql.SqlHelper.quote;

/**
 * WraPathTranslationService that is backed by the GSTUrlRegistry table.
 *
 * @author Dolf.Dijkstra
 * @since Jun 17, 2010
 */
public class UrlRegistry implements WraPathTranslationService {

    private final ICS ics;
    private final WraCoreFieldDao wraDao;
    private final VirtualWebrootDao vwDao;
    private static final String URLREG_TABLE = "GSTUrlRegistry";

    UrlRegistry(ICS ics) {
        this.ics = ics;
        wraDao = new WraCoreFieldDao();
        vwDao = new VirtualWebrootDao(ics);
    }

    public void install() {
        TableDef def = new TableDef(URLREG_TABLE, ftMessage.Browser, ftMessage.objecttbl);

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

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt("SELECT assettype, assetid, startdate, enddate, opt_site FROM " + URLREG_TABLE + " WHERE opt_vwebroot=? AND opt_url_path=? ORDER BY startdate,enddate", Collections.singletonList(URLREG_TABLE));

    static {
        REGISTRY_SELECT.setElement(0, URLREG_TABLE, "opt_vwebroot");
        REGISTRY_SELECT.setElement(1, URLREG_TABLE, "opt_url_path");
    }

    public AssetIdWithSite resolveAsset(final String virtual_webroot, final String url_path) {
        final StatementParam param = REGISTRY_SELECT.newParam();
        param.setString(0, virtual_webroot);
        param.setString(1, url_path);
        final Date now = new Date();
        for (final Row asset : SqlHelper.select(ics, REGISTRY_SELECT, param)) {
            final String assettype = asset.getString("assettype");
            final String assetid = asset.getString("assetid");
            if (inRange(asset, now)) {
                return new AssetIdWithSite(assettype, Long.parseLong(assetid), asset.getString("opt_site"));
            }
        }

        return null;
    }

    private boolean inRange(final Row asset, final Date now) {
        final Date startdate = goodString(asset.getString("startdate")) ? parseJdbcDate(asset.getString("startdate")) : null;
        final Date enddate = goodString(asset.getString("enddate")) ? parseJdbcDate(asset.getString("enddate")) : null;

        if (startdate != null || enddate != null) {
            if (startdate == null) {
                if (enddate.before(now)) {
                    return false;
                }
            } else {
                if (startdate.after(now)) {
                    return false;
                }
            }

        }
        return true;
    }

    public void addAsset(AssetId asset) {
        WebReferenceableAsset wra = wraDao.getWra(asset);

        String id = ics.genID(false);
        String path = wra.getPath();
        String assettype = asset.getType();
        long assetid = asset.getId();
        Date start = wra.getStartDate();
        Date end = wra.getEndDate();
        VirtualWebroot vw = vwDao.lookupVirtualWebrootForAsset(wra);
        String vwebroot = vw.getEnvironmentVirtualWebroot();
        String urlpath = wra.getPath().substring(vw.getMasterVirtualWebroot().length() + 1);
        int depth = urlpath != null && urlpath.length() > 0 ? urlpath.split("/").length : 0;
        String site = wraDao.resolveSite(asset.getType(), Long.toString(asset.getId()));

        String qry = "insert into " + URLREG_TABLE + " (id, path, assettype, assetid, " + (start == null ? "" : "startdate, ") + (end == null ? "" : "enddate,") + " opt_vwebroot, opt_url_path, opt_depth, opt_site)" + " VALUES " + "(" + id + "," + quote(path) + "," + quote(assettype) + "," + assetid + "," + (start == null ? "" : quote(Util.formatJdbcDate(start)) + ",") + (end == null ? "" : quote(Util.formatJdbcDate(end)) + ",") + quote(vwebroot) + "," + quote(urlpath) + "," + depth + "," + quote(site) + ")";
        SqlHelper.execute(ics, URLREG_TABLE, qry);
    }

    public void updateAsset(AssetId id) {
        // todo: optimize
        deleteAsset(id);
        addAsset(id);
    }

    public void deleteAsset(AssetId id) {
        SqlHelper.execute(ics, URLREG_TABLE, "delete from " + URLREG_TABLE + " where assettype = '" + id.getType() + "' and assetid = " + id.getId());
    }
}
