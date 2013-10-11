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

import static COM.FutureTense.Interfaces.Utilities.goodString;

import java.util.Map;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.VanityAssetBean;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.openmarket.xcelerate.publish.PubConstants;

/**
 * 
 * The BaseRenderPage class in the implementation that renders an asset. It's
 * intended use is to be called from a Controller (outer wrapper).
 * <p>
 * For the passing of arguments it makes heavy use of page criteria on the
 * calling Template
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since June 2010
 * 
 */
public class WraRenderPage extends BaseRenderPage {
    public static final String URL_PATH = "url-path";

    public static final String VIRTUAL_WEBROOT = "virtual-webroot";

    protected WraPathTranslationService pathTranslationService;
    protected WraCoreFieldDao wraCoreFieldDao;
    protected AliasCoreFieldDao aliasCoreFieldDao;

    public WraRenderPage() {
        super();
    }

    /**
     * Add p to the input parameters, if it is known or knowable. First check to
     * see if it has been explicitly set, then look it up if it hasn't been. The
     * variable is not guaranteed to be found.
     * 
     * @param id asset id with site
     * @param arguments calltemplate arguments
     */
    @Override
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

    /**
     * Load the WRA, or the alias, and return it for use by the controller. If
     * the asset is not found, an exception is thrown
     * 
     * @param id asset id
     * @return WRA, never null. May be an instance of an Alias
     */
    protected VanityAsset getWraAndResolveAlias(AssetIdWithSite id) {
        try {
            if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType())) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading alias: " + id);
                Alias alias = aliasCoreFieldDao.getAlias(id);
                VanityAssetBean wra = new VanityAssetBean(alias);

                if (LOG.isDebugEnabled())
                    LOG.debug("Loaded alias: " + id + " which resolved to " + wra.getId());
                return wra;
            } else {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading wra: " + id);
                return wraCoreFieldDao.getVanityWra(id);
            }
        } catch (IllegalArgumentException e) {
            throw new CSRuntimeException("Web-Referenceable Asset " + id + " is not valid", ftErrors.pagenotfound);
        }
    }

    /**
     * This method is the entry of the class, it does the rendering of the page.
     */
    @Override
    protected void renderPage() {
        // the preview code path is adding all the additional arguments in
        // packedargs.
        // unpack here so we can use
        String pa = unpackPackedArgs();//ics.GetVar(PubConstants.PACKEDARGS);
        
        final AssetIdWithSite id = resolveAssetId();
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: '" + id + "' for url " + ics.pageURL(),
                    ftErrors.pagenotfound);
        }
        if (LOG.isDebugEnabled())
            LOG.debug("RenderPage found a valid asset and site: " + id);

        // if childpagename is passed in (preview code path) we use this and do
        // a render:satellitepage
        if (ics.GetVar(PubConstants.CHILDPAGENAME) != null) {
            // preview UI support
            callPage(id, ics.GetVar(PubConstants.CHILDPAGENAME), pa);
        } else {
            // alternatively, render the live logic.
            VanityAsset wra = getWraAndResolveAlias(id);

            callTemplate(new AssetIdWithSite(wra.getId().getType(), wra.getId().getId(), id.getSite()),
                    wra.getTemplate());

        }
        LOG.debug("WraRenderPage execution complete");
    }

    /**
     * In this method the AssetId and the site are resolved. Based on either
     * c/cid or the virtual-webroot/url-path arguments the site which this asset
     * belongs to is discovered from the AssetPublication table.
     * 
     * @return the AssetId and the site that the asset belongs to.
     */
    @Override
    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id;
        if (goodString(ics.GetVar(VIRTUAL_WEBROOT)) && goodString(ics.GetVar(URL_PATH))) {
            id = pathTranslationService.resolveAsset(ics.GetVar(VIRTUAL_WEBROOT), ics.GetVar(URL_PATH));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            String site = wraCoreFieldDao.resolveSite(ics.GetVar("c"), ics.GetVar("cid"));

            if (site == null)
                throw new CSRuntimeException("No site found for asset (" + ics.GetVar("c") + ":" + ics.GetVar("cid")
                        + " ).", ftErrors.pagenotfound);

            id = new AssetIdWithSite(ics.GetVar("c"), Long.parseLong(ics.GetVar("cid")), site);
        } else if (goodString(ics.GetVar(VIRTUAL_WEBROOT)) || goodString(ics.GetVar(URL_PATH))) {
            // (but not both)
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path.", ftErrors.pagenotfound);
        } else {
            throw new CSRuntimeException("Missing required param c, cid.", ftErrors.pagenotfound);
        }
        return id;
    }

}
