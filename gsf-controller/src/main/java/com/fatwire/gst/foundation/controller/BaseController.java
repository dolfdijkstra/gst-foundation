/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.controller;

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
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import static COM.FutureTense.Interfaces.Utilities.goodString;
import static com.fatwire.cs.core.db.Util.parseJdbcDate;

/**
 * <p>
 * This is the controller (dispatcher) that dispatches the request to the
 * correct asset/template combination based on the path field for a Web
 * Referenceable Asset.
 * </p>
 * <p/>
 * This controller should be called from an outer XML element via the
 * <tt>CALLJAVA</tt> tag: &lt;CALLJAVA
 * CLASS="com.fatwire.gst.foundation.controller.BaseController" /&gt;
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class BaseController extends AbstractController {
    @Override
    protected void doExecute() {
        recordCompositionalDependencies();

        final AssetIdWithSite id = resolveAssetId();
        if (id == null) {
            throw new CSRuntimeException("No asset found", ftErrors.pagenotfound);
        }
        if (id.getSite() == null) {
            throw new CSRuntimeException("Could not locate site for " + id, ftErrors.pagenotfound);
        }
        ics.SetVar("site", id.getSite()); // must be set into variable pool
        LOG.trace("BaseController found a valid asset and site: " + id);

        final String templatename = lookupTemplateForAsset(id);
        if (templatename == null) {
            throw new CSRuntimeException("No template found", ftErrors.pagenotfound);
        }
        LOG.trace("BaseController found a valid template:" + templatename);

        callTemplate(id.getSite(), id, templatename);
        LOG.trace("BaseController execution complete");
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
     * Record compositional dependencies that are required for the controller
     */
    protected void recordCompositionalDependencies() {
        if (ics.isCacheable(ics.GetVar(ftMessage.PageName))) {
            if (goodString(ics.GetVar("seid"))) {
                LogDep.logDep(ics, "SiteEntry", ics.GetVar("seid"));
            }
            if (goodString(ics.GetVar("eid"))) {
                LogDep.logDep(ics, "CSElement", ics.GetVar("eid"));
            }
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
     * @param e exception
     */
    protected void handleCSRuntimeException(final CSRuntimeException e) {
        switch (e.getErrno()) {
            case 400:
            case ftErrors.badparams:
                sendError(400, e);
                break;
            case 404:
            case ftErrors.pagenotfound:
                sendError(404, e);
                break;
            case 403:
            case ftErrors.noprivs:
                sendError(403, e);
                break;
            default:
                sendError(500, e);
                break;
        }
    }

    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id;
        if (goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path"))) {
            id = resolveAssetFromUrl(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            String site = resolveSiteForWRA(ics.GetVar("c"), ics.GetVar("cid"));
            id = new AssetIdWithSite(ics.GetVar("c"), Long.parseLong(ics.GetVar("cid")), site);
        } else if (goodString(ics.GetVar("virtual-webroot")) || goodString(ics.GetVar("url-path"))) {
            // (but not both)
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path.", ftErrors.pagenotfound);
        } else {
            throw new CSRuntimeException("Missing required param c, cid.", ftErrors.pagenotfound);
        }
        return id;
    }

    static final PreparedStmt REGISTRY_SELECT = new PreparedStmt("SELECT assettype, assetid, startdate, enddate, opt_site FROM GSTUrlRegistry WHERE opt_vwebroot=? AND opt_url_path=? ORDER BY startdate,enddate", Collections.singletonList("GSTUrlRegistry"));

    static {
        REGISTRY_SELECT.setElement(0, "GSTUrlRegistry", "opt_vwebroot");
        REGISTRY_SELECT.setElement(1, "GSTUrlRegistry", "opt_url_path");
    }

    protected AssetIdWithSite resolveAssetFromUrl(final String virtual_webroot, final String url_path) {
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


    private static final String ASSETPUBLICATION_QRY = "SELECT p.name from Publication p, AssetPublication ap " + "WHERE ap.assettype = ? " + "AND ap.assetid = ? " + "AND ap.pubid=p.id";
    static final PreparedStmt AP_STMT = new PreparedStmt(ASSETPUBLICATION_QRY, Arrays.asList("Publication, AssetPublication"));

    static {
        AP_STMT.setElement(0, "AssetPublication", "assettype");
        AP_STMT.setElement(1, "AssetPublication", "assetid");
    }

    protected String resolveSiteForWRA(String c, String cid) {
        final StatementParam param = AP_STMT.newParam();
        param.setString(0, c);
        param.setLong(1, Long.valueOf(cid));
        String result = null;
        // todo: this query fails with a strange error
        //        for (Row pubid : SqlHelper.select(ics, AP_STMT, param)) {
        for (Row pubid : SqlHelper.select(ics, "AssetPublication,Publication", "SELECT p.name from Publication p, AssetPublication ap " + "WHERE ap.assettype =  '" + c + "'AND ap.assetid =  " + cid + "AND ap.pubid=p.id")) {
            if (result != null) {
                LOG.warn("Found asset " + c + ":" + cid + " in more than one publication. It should not be shared; aliases are to be used for cross-site sharing.  Controller will use first site found");
            } else {
                result = pubid.getString("name");
            }
        }
        return result;
    }

    // TODO: Finish this method

    protected boolean inRange(final Row asset, final Date now) {
        final Date startdate = asset.getString("startdate") != null ? parseJdbcDate(asset.getString("startdate")) : null;
        final Date enddate = asset.getString("enddate") != null ? parseJdbcDate(asset.getString("enddate")) : null;

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
        final String select = "SELECT template FROM " + id.getType() + " where id=?";

        final PreparedStmt stmt = new PreparedStmt(select, Collections.singletonList(id.getType()));
        stmt.setElement(0, id.getType(), "id");

        final StatementParam param = stmt.newParam();
        param.setLong(0, id.getId());

        for (final Row row : SqlHelper.select(ics, stmt, param)) {
            return row.getString("template");
        }
        return null;
    }

    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays.asList("c", "cid", "eid", "seid", "packedargs", "variant", "context", "pagename", "childpagename", "site", "tid", "virtual-webroot", "url-path");

    @SuppressWarnings("unchecked")
    protected void callTemplate(final String site, final AssetId id, final String tname) {
        final CallTemplate ct = new CallTemplate();
        ct.setSite(site);
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        ct.setTtype(CallTemplate.Type.CSElement);
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");

        // typeless or not...
        String target = tname.startsWith("/") ? site + "/" + tname : site + "/" + id.getType() + "/" + tname;
        Style style = getCallTemplateCallStyle(target);
        if (LOG.isTraceEnabled())
            LOG.trace("BaseController about to call template on " + id + " with " + tname + " using style:" + style);
        ct.setStyle(style);

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
            final String varname = vars.nextElement();
            if (!CALLTEMPLATE_EXCLUDE_VARS.contains(varname)) {
                // page criteria is automatically validated by the CallTemplate tag,
                // but it is a bad idea to send params through if they aren't page criteria.
                // todo: consider validating here. Validation is duplicated but may be useful
                ct.setArgument(varname, ics.GetVar(varname));
                if (LOG.isTraceEnabled()) LOG.trace("CallTemplate param added: " + varname + "=" + ics.GetVar(varname));
            }
        }

        ct.execute(ics);
    }

    protected Style getCallTemplateCallStyle(String target) {
        if (ics.isCacheable(ics.GetVar(ftMessage.PageName))) {
            // call as element when current is caching.
            // note that it may be useful to set this to "embedded" in some cases.
            // override it in that situation
            return Style.element;
        } else if (ics.isCacheable(target)) {
            // call as embedded when current is not caching and target is
            return Style.embedded;
        } else {
            // call as element when current is not caching and target is not
            return Style.element;
        }
    }

}
