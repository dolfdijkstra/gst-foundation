/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.controller.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraBeanImpl;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Generic page-rendering action.
 * 
 * @author Tony Field
 * @since 2011-03-15
 */
public class RenderPage implements Action {

    protected static final Log LOG = LogFactory.getLog(RenderPage.class.getPackage().getName());

    @InjectForRequest
    protected ICS ics;
    @InjectForRequest
    protected WraCoreFieldDao wraCoreFieldDao;
    @InjectForRequest
    protected AliasCoreFieldDao aliasCoreFieldDao;
    @InjectForRequest
    protected WraPathTranslationService wraPathTranslationService;

    public void handleRequest(final ICS ics) {

        final AssetIdWithSite id = resolveAssetId();
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: '" + id +"' for url " + ics.pageURL(), ftErrors.pagenotfound);
        }
        LOG.debug("RenderPage found a valid asset and site: " + id);

        final WebReferenceableAsset wra = getWraAndResolveAlias(id);

        callTemplate(new AssetIdWithSite(wra.getId(), id.getSite()), wra.getTemplate());
        LOG.debug("RenderPage execution complete");
    }

    /**
     * Load the WRA, or the alias, and return it for use by the controller. If
     * the asset is not found, an exception is thrown
     * 
     * @param id asset id
     * @return WRA, never null. May be an instance of an Alias
     */
    protected WebReferenceableAsset getWraAndResolveAlias(final AssetIdWithSite id) {
        try {
            if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType())) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Loading alias: " + id);
                }
                final Alias alias = aliasCoreFieldDao.getAlias(id);
                final WraBeanImpl wra = new WraBeanImpl(alias);
                wra.setId(alias.getTarget());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loaded alias: " + id + " which resolved to " + wra.getId());
                }
                return wra;
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Loading wra: " + id);
                }
                return wraCoreFieldDao.getWra(id);
            }
        } catch (final IllegalArgumentException e) {
            throw new CSRuntimeException("Web-Referenceable Asset " + id + " is not valid", ftErrors.pagenotfound);
        }
    }

    protected void callTemplate(final AssetIdWithSite id, final String tname) {
        final CallTemplate ct = new CallTemplate();
        // ct.setFixPageCriteria(true);
        ct.setSite(id.getSite());
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        // ct.setTtype(CallTemplate.Type.CSElement);
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");
        ct.setArgument("site", id.getSite());

        final String packedargs = ics.GetVar("packedargs");
        if (packedargs != null && packedargs.length() > 0) {
            ct.setPackedargs(packedargs);
        }

        // typeless or not...
        final String targetPagename = tname.startsWith("/") ? (id.getSite() + tname) : (id.getSite() + "/"
                + id.getType() + "/" + tname);

        // create a list of parameters that can be specified as arguments to the
        // CallTemplate tag.
        final Map<String, String> arguments = new HashMap<String, String>();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Target pagename is " + targetPagename);
        }
        // Prime the map with the ics variable scope for the architect to make
        // the controller as transparent as possible
        String[] pageKeys = ics.pageCriteriaKeys(targetPagename);
        if (pageKeys != null) {
            for (final String pcVarName : pageKeys) {
                if (!CALLTEMPLATE_EXCLUDE_VARS.contains(pcVarName) && StringUtils.isNotBlank(ics.GetVar(pcVarName))) {
                    arguments.put(pcVarName, ics.GetVar(pcVarName));
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PageCriteria for " + targetPagename + " is null.");
            }
        }

        // allow users to set their own
        getCallTemplateArguments(id, arguments);

        // add them to the tag
        for (final Entry<String, String> e : arguments.entrySet()) {
            ct.setArgument(e.getKey(), e.getValue());
            if (LOG.isTraceEnabled()) {
                LOG.trace("CallTemplate param added: " + e.getKey() + "=" + e.getValue());
            }
        }

        // override calltemplate call style
        imposeCallTemplateStyle(ct, targetPagename);

        final String s = ct.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }
    }

    /**
     * This method allows developers to override the automatically-selected
     * CallTemplate call style. By default, no override is done and the
     * CallTemplate class determines the best call style.
     * 
     * @param ct CallTemplate tag
     * @param targetPagename target pagename
     * @see CallTemplate #setStyle(CallTemplate.Style)
     */
    protected void imposeCallTemplateStyle(final CallTemplate ct, final String targetPagename) {
    }

    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id;
        if (goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path"))) {
            id = wraPathTranslationService.resolveAsset(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
            if (id == null) {
                throw new CSRuntimeException("WraPathTranslationService could not find a matching asset for virtual-webroot: "+ics.GetVar("virtual-webroot")+" and url-path: "+ics.GetVar("url-path"), ftErrors.pagenotfound);
            }
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            final String site = wraCoreFieldDao.resolveSite(ics.GetVar("c"), ics.GetVar("cid"));
            if (site == null)
                throw new CSRuntimeException("No site found for asset (" + ics.GetVar("c") + ":" + ics.GetVar("cid")
                        + " ).", ftErrors.pagenotfound);
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
            "url-path", "rendermode", "ft_ss");

    /**
     * This method collects additional arguments for the CallTemplate call. New
     * arguments are added to the map as name-value pairs.
     * 
     * @param id AssetIdWithSite object
     * @param arguments Map<String,String> containing arguments for the nested
     *            CallTemplate call
     */
    protected void getCallTemplateArguments(final AssetIdWithSite id, final Map<String, String> arguments) {
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
    protected final void findAndSetP(final AssetIdWithSite id, final Map<String, String> arguments) {
        final String pVar = ics.GetVar("p");
        if (pVar != null && pVar.length() > 0) {
            arguments.put("p", pVar);
        } else {
            final long p = wraCoreFieldDao.findP(id);
            if (p > 0L) {
                arguments.put("p", Long.toString(p));
            }
        }
    }
}
