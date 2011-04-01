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
package com.fatwire.gst.foundation.controller;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraBeanImpl;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * <p>
 * This is the controller (dispatcher) that dispatches the request to the
 * correct asset/template combination based on the path field for a Web
 * Referenceable Asset.
 * </p>
 * <p/>
 * This controller should be called from an outer XML element via the
 * <tt>CALLJAVA</tt> tag:
 * <code>&lt;CALLJAVA CLASS="com.fatwire.gst.foundation.controller.BaseController" /&gt;
 * </code>
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class BaseController extends AbstractController {

    protected WraPathTranslationService pathTranslationService;
    protected WraCoreFieldDao wraCoreFieldDao;
    protected AliasCoreFieldDao aliasCoreFieldDao;

    @Override
    public void SetAppLogic(IPS ips) {
        super.SetAppLogic(ips);
        pathTranslationService = WraPathTranslationServiceFactory.getService(ics);
        wraCoreFieldDao = new WraCoreFieldDao(ics);
        aliasCoreFieldDao = new AliasCoreFieldDao(ics, wraCoreFieldDao);
    }

    @Override
    protected void doExecute() {
        recordCompositionalDependencies();

        final AssetIdWithSite id = resolveAssetId();
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: " + id, ftErrors.pagenotfound);
        }
        LOG.trace("BaseController found a valid asset and site: " + id);

        WebReferenceableAsset wra = getWraAndResolveAlias(id);

        callTemplate(new AssetIdWithSite(wra.getId().getType(), wra.getId().getId(), id.getSite()), wra.getTemplate());
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

    /**
     * Load the WRA, or the alias, and return it for use by the controller. If
     * the asset is not found, an exception is thrown
     * 
     * @param id asset id
     * @return WRA, never null. May be an instance of an Alias
     */
    protected WebReferenceableAsset getWraAndResolveAlias(AssetIdWithSite id) {
        try {
            if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType())) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading alias: " + id);
                Alias alias = aliasCoreFieldDao.getAlias(id);
                WraBeanImpl wra = new WraBeanImpl(alias);
                wra.setId(alias.getTarget());
                if (LOG.isDebugEnabled())
                    LOG.debug("Loaded alias: " + id + " which resolved to " + wra.getId());
                return wra;
            } else {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading wra: " + id);
                return wraCoreFieldDao.getWra(id);
            }
        } catch (IllegalArgumentException e) {
            throw new CSRuntimeException("Web-Referenceable Asset " + id + " is not valid", ftErrors.pagenotfound);
        }
    }

    @SuppressWarnings("unchecked")
    protected void callTemplate(final AssetIdWithSite id, final String tname) {
        final CallTemplate ct = new CallTemplate();
        ct.setSite(id.getSite());
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        ct.setTtype(CallTemplate.Type.CSElement);
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");

        // typeless or not...
        String target = tname.startsWith("/") ? id.getSite() + "/" + tname : id.getSite() + "/" + id.getType() + "/"
                + tname;
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
                // todo: low priority consider validating here. Validation is
                // duplicated but
                // may be useful
                arguments.put(varname, ics.GetVar(varname));
            }
        }
        getCallTemplateArguments(id, arguments);
        for (String name : arguments.keySet()) {
            ct.setArgument(name, arguments.get(name));
            if (LOG.isTraceEnabled())
                LOG.trace("CallTemplate param added: " + name + "=" + arguments.get(name));
        }

        String s = ct.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }
    }

    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id;
        if (goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path"))) {
            id = pathTranslationService.resolveAsset(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            String site = wraCoreFieldDao.resolveSite(ics.GetVar("c"), ics.GetVar("cid"));
            id = new AssetIdWithSite(ics.GetVar("c"), Long.parseLong(ics.GetVar("cid")), site);
        } else if (goodString(ics.GetVar("virtual-webroot")) || goodString(ics.GetVar("url-path"))) {
            // (but not both)
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path.", ftErrors.pagenotfound);
        } else {
            throw new CSRuntimeException("Missing required param c, cid.", ftErrors.pagenotfound);
        }
        return id;
    }

    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays.asList("c", "cid", "eid", "seid",
            "packedargs", "variant", "context", "pagename", "childpagename", "site", "tid", "virtual-webroot",
            "url-path");

    /**
     * This method collects additional arguments for the CallTemplate call. New
     * arguments are added to the map as name-value pairs.
     * 
     * @param id AssetIdWithSite object
     * @param arguments Map<String,String> containing arguments for the nested
     *            CallTemplate call
     */
    protected void getCallTemplateArguments(AssetIdWithSite id, Map<String, String> arguments) {
        findAndSetP(id, arguments);
    }

    /**
     * Add p to the input parameters, if it is known or knowable. First check to
     * see if it has been explicitly set, then look it up if it hasn't been. The
     * variable is not guaranteed to be found.
     * 
     * @param id asset id with site
     * @param arguments calltemplate arguments
     */
    private void findAndSetP(AssetIdWithSite id, Map<String, String> arguments) {
        String pVar = ics.GetVar("p");
        if (pVar != null && pVar.length() > 0) {
            arguments.put("p", pVar);
        } else {
            long p = wraCoreFieldDao.findP(id);
            if (p > 0L) {
                arguments.put("p", Long.toString(p));
            }
        }
    }

    protected Style getCallTemplateCallStyle(String target) {
        if (RenderUtils.isCacheable(ics, ics.GetVar(ftMessage.PageName))) {
            // call as element when current is caching.
            // note that it may be useful to set this to "embedded" in some
            // cases.
            // override it in that situation
            return Style.element;
        } else if (RenderUtils.isCacheable(ics, target)) {
            // call as embedded when current is not caching and target is
            return Style.embedded;
        } else {
            // call as element when current is not caching and target is not
            return Style.element;
        }
    }

}
