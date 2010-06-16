/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.controller;

import static COM.FutureTense.Interfaces.Utilities.goodString;
import static com.fatwire.cs.core.db.Util.parseJdbcDate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * 
 * <p>This is the controller (dispatcher) that dispatches the request to the correct asset/template combination based on the path field for a 
 * Web Referenceable Asset.</p>
 *  <p>This controller should be called from an outer XML element via the <tt>CALLJAVA</tt> tag: 
 *   * &lt;CALLJAVA CLASS="com.fatwire.gst.foundation.controller.BaseController" /&gt;
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class BaseController extends AbstractController {
    @Override
    protected void doExecute() {
        final AssetId id = resolveAssetId();
        if (id == null) {
            throw new CSRuntimeException("No asset found", 404);
        }
        final String templatename = lookupTemplateForAsset(id);
        if (templatename == null) {
            throw new CSRuntimeException("No template found", 404);
        }
        callTemplate(id, templatename);
    }

    @Override
    protected void handleException(final Exception e) {
        if (e instanceof CSRuntimeException) {
            handleCSRuntimeException((CSRuntimeException) e);
        } else {
            sendError(500, e);
        }
    }

    /**
     * This method transforms errno values into http status codes and sets them
     * using the X-Fatwire-Status header.
     * <p/>
     * Only some errnos are handled by this base class.
     * <p/>
     * More info coming soon
     *
     * @param ics ics context
     * @param e exception
     */
    protected void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
        case ftErrors.badparams:
            sendError(400, e);
            break;
        case ftErrors.pagenotfound:
            sendError(404, e);
            break;
        case ftErrors.noprivs:
            sendError(403, e);
            break;
        default:
            sendError(500, e);
            break;
        }
    }

    protected AssetId resolveAssetId() {
        final AssetId id;
        if (goodString(ics.GetVar("virtual-webroot"))
                && goodString(ics.GetVar("url-path"))) {
            id = resolveAssetFromUrl(ics.GetVar("virtual-webroot"), ics
                    .GetVar("url-path"));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) { // handle these to be nice
            id = new AssetIdImpl(ics.GetVar("c"), Long.parseLong(ics
                    .GetVar("cid")));
        } else {
            if (goodString(ics.GetVar("virtual-webroot"))
                    && goodString(ics.GetVar("url-path"))) {
                throw new CSRuntimeException(
                        "Missing required param virtual-webroot & url-path.",
                        ftErrors.badparams);
            } else {
                throw new CSRuntimeException("Missing required param c & cid.",
                        ftErrors.badparams);

            }

        }
        return id;
    }

    static final String REGISTRY_SELECT = "SELECT assettype ,assetid ,startdate ,enddate FROM GSTUrlRegistry WHERE opt_vwebroot=? AND opt_url_path=? ORDER BY startdate,enddate";

    static final String REGISTRY_TABLE = "GSTUrlRegistry";

    protected AssetId resolveAssetFromUrl(final String virtual_webroot,
            final String url_path) {

        final PreparedStmt stmt = new PreparedStmt(REGISTRY_SELECT, Collections
                .singletonList(REGISTRY_TABLE));
        stmt.setElement(0, REGISTRY_TABLE, "opt_vwebroot");
        stmt.setElement(1, REGISTRY_TABLE, "opt_url_path");
        final StatementParam param = stmt.newParam();
        param.setString(0, virtual_webroot);
        param.setString(1, url_path);
        final Date now = new Date();
        for (final Row asset : SqlHelper.select(ics, stmt, param)) {

            final String assettype = asset.getString("assettype");
            final String assetid = asset.getString("assetid");
            if (inRange(asset, now)) {
                return new AssetIdImpl(assettype, Long.parseLong(assetid));
            }
        }

        return null;
    }

    //TODO: Finish this method
    protected boolean inRange(final Row asset, final Date now) {
        final Date startdate = asset.getString("startdate") != null ? parseJdbcDate(asset
                .getString("startdate"))
                : null;
        final Date enddate = asset.getString("enddate") != null ? parseJdbcDate(asset
                .getString("enddate"))
                : null;

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

    protected String lookupTemplateForAsset(final AssetId id) {
        final String select = "SELECT template FROM " + id.getType()
                + " where id=?";

        final PreparedStmt stmt = new PreparedStmt(select, Collections
                .singletonList(id.getType()));
        stmt.setElement(0, id.getType(), "id");

        final StatementParam param = stmt.newParam();
        param.setLong(0, id.getId());

        for (final Row row : SqlHelper.select(ics, stmt, param)) {
            return row.getString("template");
        }
        return null;
    }

    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays
            .asList("c", "cid", "eid", "seid", "packedargs", "variant",
                    "context", "pagename", "childpagename", "site", "tid",
                    "virtual-webroot", "url-path");

    @SuppressWarnings("unchecked")
    protected void callTemplate(final AssetId id, final String tname) {
        final CallTemplate ct = new CallTemplate();
        ct.setSite(ics.GetVar("site"));
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        ct.setTtype("CSElement");
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");
        String target = tname.startsWith("/") ? ics.GetVar("site") + "/"
                + tname : ics.GetVar("site") + "/" + id.getType() + "/" + tname; //typeless or not
        
        if (ics.isCacheable(ics.GetVar(ftMessage.PageName))) {
            ct.setStyle("element"); // call as element when current is caching    
        } else if (ics.isCacheable(target)) {
            ct.setStyle("pagelet"); // call as pagelet when current is not caching and target is
        } else {
            ct.setStyle("element"); // call as pagelet when current is not caching and target is not
        }

        final String variant = ics.GetVar("variant");
        if (variant != null && variant.length() > 0) {
            ct.setVariant(variant);
        }
        final String packedargs = ics.GetVar("packedargs");
        if (packedargs != null && packedargs.length() > 0) {
            ct.setPackedargs(packedargs);
        }

        final Enumeration<String> vars = ics.GetVars();
        while (vars.hasMoreElements()) {
            final String varnane = vars.nextElement();
            if (!CALLTEMPLATE_EXCLUDE_VARS.contains(varnane)) {
                //TODO: handle pagecriteria if we are calling cacheable
                ct.setArgument(varnane, ics.GetVar(varnane));
            }
        }

        ct.execute(ics);
    }

}
