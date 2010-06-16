/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.developernet.CSRuntimeException;
import com.fatwire.developernet.facade.runtag.example.render.CallTemplate;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import java.util.*;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * <CALLJAVA CLASS="com.fatwire.gst.foundation.controller.BaseController" />
 *
 * @author Tony Field
 * @since Jun 10, 2010
 */
public class BaseController extends AbstractController
{
    protected void execute(ICS ics)
    {
        final AssetId id = resolveAssetId(ics);
        String templatename = lookupTemplateForAsset(ics, id);
        callTemplate(ics, id, templatename);
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
    protected void handleCSRuntimeException(ICS ics, CSRuntimeException e)
    {
        switch(e.getErrno())
        {
            case ftErrors.badparams:
                ics.StreamHeader("X-Fatwire-Status", "400");
                LOG.debug("400 status code sent due to exception " + e.toString(), e);
                break;
            case ftErrors.pagenotfound:
                ics.StreamHeader("X-Fatwire-Status", "404");
                LOG.debug("404 status code sent due to exception " + e.toString(), e);
                break;
            case ftErrors.noprivs:
                ics.StreamHeader("X-Fatwire-Status", "403");
                LOG.debug("404 status code sent due to exception " + e.toString(), e);
                break;
            default:
                LOG.error("Unhandled CSRuntimeException caught by BaseController.  Re-throwing", e);
        }
    }


    protected AssetId resolveAssetId(ICS ics)
    {
        final AssetId id;
        if(goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path")))
        {
            id = resolveAssetFromUrl(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
        }
        else if(goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) // handle these to be nice
        {
            id = new AssetIdImpl(ics.GetVar("c"), Long.valueOf(ics.GetVar("cid")));
        }
        else
        {
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path or c & cid.", ftErrors.badparams);
        }
        return id;
    }

    protected AssetId resolveAssetFromUrl(String virtual_webroot, String url_path)
    {
        return null; // todo: implement
    }

    protected String lookupTemplateForAsset(ICS ics, AssetId id)
    {

        /*
        select template from id.getType() where id = id.getId()
        */
        return null; // todo: implement
    }

    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays.asList("c", "cid", "eid", "seid", "packedargs", "variant", "context", "pagename", "childpagename", "site", "tid", "virtual-webroot", "url-path");

    protected void callTemplate(ICS ics, AssetId id, String tname)
    {
        CallTemplate ct = new CallTemplate();
        ct.setSite(ics.GetVar("site"));
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        ct.setTtype("CSElement");
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");
        ct.setStyle("pagelet");
        String variant = ics.GetVar("variant");
        if(variant != null && variant.length() > 0)
        {
            ct.setVariant(variant);
        }
        String packedargs = ics.GetVar("packedargs");
        if(packedargs != null && packedargs.length() > 0)
        {
            ct.setPackedargs(packedargs);
        }

        Enumeration vars = ics.GetVars();
        while(vars.hasMoreElements())
        {
            String varnane = (String)vars.nextElement();
            if(!CALLTEMPLATE_EXCLUDE_VARS.contains(varnane))
            {
                ct.setArgument(varnane, ics.GetVar(varnane));
            }
        }

        ct.execute(ics);
    }


}
