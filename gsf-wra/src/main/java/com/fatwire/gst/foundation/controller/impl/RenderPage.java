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
package com.fatwire.gst.foundation.controller.impl;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.Controller;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraBeanImpl;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Generic page-rendering controller.
 *
 * @author Tony Field
 * @since 2011-03-15
 */
public class RenderPage implements Controller {

    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.controller.impl.RenderPage");

    public void handleRequest(ICS ics) {

        RenderPageContext context = getRenderPageContext(ics);

        final AssetIdWithSite id = resolveAssetId(context);
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: " + id, ftErrors.pagenotfound);
        }
        LOG.debug("RenderPage found a valid asset and site: " + id);

        WebReferenceableAsset wra = getWraAndResolveAlias(context, id);

        callTemplate(context, new AssetIdWithSite(wra.getId(), id.getSite()), wra.getTemplate());
        LOG.debug("RenderPage execution complete");
    }

    /**
     * Return a context object that is convenient for use within the RenderPage controller.
     *
     * @param ics Content Server context
     * @return render page context object
     */
    protected RenderPageContext getRenderPageContext(ICS ics) {
        RenderPageContext context = new RenderPageContext();
        context.setICS(ics);
        WraCoreFieldDao wraCoreFieldDao = new WraCoreFieldDao(ics);
        context.setWraCoreFieldDao(wraCoreFieldDao);
        context.setAliasCoreFieldDao(new AliasCoreFieldDao(ics, wraCoreFieldDao));
        context.setWraPathTranslationService(WraPathTranslationServiceFactory.getService(ics));
        return context;
    }

    /**
     * Load the WRA, or the alias, and return it for use by the controller.
     * If the asset is not found, an exception is thrown
     *
     * @param id asset id
     * @return WRA, never null.  May be an instance of an Alias
     */
    protected WebReferenceableAsset getWraAndResolveAlias(RenderPageContext context, AssetIdWithSite id) {
        try {
            if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType())) {
                if (LOG.isTraceEnabled()) LOG.trace("Loading alias: " + id);
                Alias alias = context.getAliasCoreFieldDao().getAlias(id);
                WraBeanImpl wra = new WraBeanImpl(alias);
                wra.setId(alias.getTarget());
                if (LOG.isDebugEnabled()) LOG.debug("Loaded alias: " + id + " which resolved to " + wra.getId());
                return wra;
            } else {
                if (LOG.isTraceEnabled()) LOG.trace("Loading wra: " + id);
                return context.getWraCoreFieldDao().getWra(id);
            }
        } catch (IllegalArgumentException e) {
            throw new CSRuntimeException("Web-Referenceable Asset " + id + " is not valid", ftErrors.pagenotfound);
        }
    }

    @SuppressWarnings("unchecked")
    protected void callTemplate(RenderPageContext context, final AssetIdWithSite id, final String tname) {
        ICS ics = context.getICS();
        final CallTemplate ct = new CallTemplate();
        ct.setSite(id.getSite());
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        ct.setTtype(CallTemplate.Type.CSElement);
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");

        // typeless or not...
        String target = tname.startsWith("/") ? id.getSite() + "/" + tname : id.getSite() + "/" + id.getType() + "/" + tname;
        CallTemplate.Style style = getCallTemplateCallStyle(context, target);
        if (LOG.isDebugEnabled())
            LOG.debug("BaseController about to call template on " + id + " with " + tname + " using style:" + style);
        ct.setStyle(style);

        final String variant = ics.GetVar("variant");
        if (variant != null && variant.length() > 0) {
            ct.setVariant(variant);
        }
        final String packedargs = ics.GetVar("packedargs");
        if (packedargs != null && packedargs.length() > 0) {
            ct.setPackedargs(packedargs);
        }

        ct.setArgument("site", id.getSite());

        // create a list of parameters that can be specified as arguments to the
        // CallTemplate tag.
        final Map<String, String> arguments = new HashMap<String, String>();

        // Prime the map with the ics variable scope for the architect to make
        // the
        // controller as transparent as possible
        final Enumeration<String> vars = ics.GetVars();
        while (vars.hasMoreElements()) {
            final String varname = vars.nextElement();
            // some parameters are automatically excluded because they relate
            // directly
            // to this controller only.
            if (!CALLTEMPLATE_EXCLUDE_VARS.contains(varname)) {
                // page criteria is automatically validated by the CallTemplate
                // tag, but it is a bad idea to send params through if they
                // aren't
                // page criteria.
                // todo: low priority consider validating here. Validation is duplicated but
                // may be useful
                arguments.put(varname, ics.GetVar(varname));
            }
        }
        getCallTemplateArguments(context, id, arguments);
        for (String name : arguments.keySet()) {
            ct.setArgument(name, arguments.get(name));
            if (LOG.isTraceEnabled()) LOG.trace("CallTemplate param added: " + name + "=" + arguments.get(name));
        }

        ct.execute(ics);
    }

    protected AssetIdWithSite resolveAssetId(RenderPageContext context) {
        ICS ics = context.getICS();
        final AssetIdWithSite id;
        if (goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path"))) {
            id = context.getWraPathTranslationService().resolveAsset(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            String site = context.getWraCoreFieldDao().resolveSite(ics.GetVar("c"), ics.GetVar("cid"));
            id = new AssetIdWithSite(ics.GetVar("c"), Long.parseLong(ics.GetVar("cid")), site);
        } else if (goodString(ics.GetVar("virtual-webroot")) || goodString(ics.GetVar("url-path"))) {
            // (but not both)
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path.", ftErrors.pagenotfound);
        } else {
            throw new CSRuntimeException("Missing required param c, cid.", ftErrors.pagenotfound);
        }
        return id;
    }

    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays.asList("c", "cid", "eid", "seid", "packedargs", "variant", "context", "pagename", "childpagename", "site", "tid", "virtual-webroot", "url-path");

    /**
     * This method collects additional arguments for the CallTemplate call. New
     * arguments are added to the map as name-value pairs.
     *
     * @param id        AssetIdWithSite object
     * @param arguments Map<String,String> containing arguments for the nested
     *                  CallTemplate call
     */
    protected void getCallTemplateArguments(RenderPageContext context, AssetIdWithSite id, Map<String, String> arguments) {
        findAndSetP(context, id, arguments);
    }

    /**
     * Add p to the input parameters, if it is known or knowable. First check to
     * see if it has been explicitly set, then look it up if it hasn't been. The
     * variable is not guaranteed to be found.
     *
     * @param id        asset id with site
     * @param arguments calltemplate arguments
     */
    private void findAndSetP(RenderPageContext context, AssetIdWithSite id, Map<String, String> arguments) {
        String pVar = context.getICS().GetVar("p");
        if (pVar != null && pVar.length() > 0) {
            arguments.put("p", pVar);
        } else {
            long p = context.getWraCoreFieldDao().findP(id);
            if (p > 0L) {
                arguments.put("p", Long.toString(p));
            }
        }
    }

    protected CallTemplate.Style getCallTemplateCallStyle(RenderPageContext context, String target) {
        ICS ics = context.getICS();
        if (RenderUtils.isCacheable(ics, ics.GetVar(ftMessage.PageName))) {
            // call as element when current is caching.
            // note that it may be useful to set this to "embedded" in some
            // cases.
            // override it in that situation
            return CallTemplate.Style.element;
        } else if (RenderUtils.isCacheable(ics, target)) {
            // call as embedded when current is not caching and target is
            return CallTemplate.Style.embedded;
        } else {
            // call as element when current is not caching and target is not
            return CallTemplate.Style.element;
        }
    }
}
