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
package com.fatwire.gst.foundation.url;

import java.util.Map;

import COM.FutureTense.Export.Reference;
import COM.FutureTense.Export.ReferenceException;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IReference;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.uri.Definition;
import com.fatwire.gst.foundation.vwebroot.AssetApiVirtualWebrootDao;
import com.fatwire.gst.foundation.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.wra.AssetApiWraCoreFieldDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import com.openmarket.xcelerate.publish.PageRef;
import com.openmarket.xcelerate.publish.PubConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * This is the WebReferenceable assets PageRef class. This overrides the default
 * PageRef, so we can manipulate the arguments that go into the assembler while
 * we have database access.
 * <p/>
 * <p>
 * It will alter the input args map by adding the derived parameters
 * <ul>
 * <li>virtual-webroot</li>
 * <li>url-path</li>
 * <li>pagename</li>
 * </ul>
 * to the map before calling super.setParameters(args, ics).
 * <p/>
 * This modification is ONLY done when
 * <ol>
 * <li>the satellite context is Satellite</li>
 * <li>the app type is Content Server</li>
 * <li>the environment name is set</li>
 * <li>the asset is found</li>
 * <li>the asset has a path set</li>
 * <li>a virtual webroot can be found for the path set and environment name</li>
 * </p>
 * <p>
 * <ol>
 * <li>PageRef extends Reference implements IPageRef</li>
 * <li>Reference implements IReference</li>
 * <li>interface IPageRef extends IReference</li>
 * <li>interface IReference extends Definition</li>
 * </ol>
 * </p>
 * 
 * @author Dolf Dijkstra
 * @author Tony Field
 * @see Reference
 * @see IReference
 * @see Definition
 * @since Jun 17, 2010
 */

public class WraPageReference extends PageRef {

    /**
     * This logic is quite specific to the GSF so create a dedicated logger.
     */
    private static final Log log = LogFactory.getLog(WraPageReference.class.getName());

    public static final String GST_DISPATCHER = "GST/Dispatcher";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.xcelerate.publish.PageRef#setParameters(java.util.Map,
     * COM.FutureTense.Interfaces.ICS)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setParameters(Map args, ICS ics) throws ReferenceException {

        if (doVanityUrl(args, ics)) {
            VanityUrlCalculationContext ctx = getVanityUrlCalculationContext(args, ics);

            VirtualWebroot vw = ctx.getVirtualWebrootForAsset(args, ics);

            args.put("virtual-webroot", vw.getEnvironmentVirtualWebroot());
            args.put("url-path", ctx.getVanityAsset(args, ics).getPath().substring(vw.getMasterVirtualWebroot().length()));

            // has pagename been set? if not, use default.
            String pagename = ics.GetProperty(WraPathAssembler.DISPATCHER_PROPNAME, "ServletRequest.properties", true);
            if (!goodString(pagename)) {
                pagename = GST_DISPATCHER;
            }
            // pagename or wrapperpage depending on whether or not we're going to use a wrapper.
            if (args.get(PubConstants.WRAPPERPAGE) != null)
                args.put(PubConstants.WRAPPERPAGE, pagename);
            else
                args.put("pagename", pagename);
        }
        super.setParameters(args, ics);
    }

    /**
     * Determine if we should look up the virtual webroot and url path or not
     */
    private boolean doVanityUrl(Map args, ICS ics) {

        if (log.isDebugEnabled()) {
            log.debug("checking to see if the URL for this asset should be a vanity url.  asset: "+args.get("c")+":"+args.get("cid"));
        }

        if (getSatelliteContext() != SatelliteContext.SATELLITE_SERVER) {
            log.debug("not applying vanity URL because satellite context is not satellite");
            return false;
        }

        VanityUrlCalculationContext ctx = getVanityUrlCalculationContext(args, ics);

        if (!VanityUrlCalculationContext.isGetTemplateUrl(args, ics)) {
            log.debug("not applying vanity URL because API usage was not found to be the gettemplateurl tag");
            return false;
        }

        if (!ctx.isGsfEnvironmentSet(args, ics)) {
            log.debug("not applying vanity URL because virtual webroot environment is not set");
            return false;
        }

        if (!ctx.isVanityAsset(args, ics)) {
            log.debug("not applying vanityURL because asset is not a WRA");
            return false;
        }

        if (!ctx.hasVirtualWebroot(args, ics)) {
            log.debug("not applying vanity URL because we could not find a valid virtual webroot in the asset's path field");
            return false;
        }

        log.debug("Asset passed tests and a vanity URL will be created");

        return true;
    }

    /**
     * Figuring out whether or not we should do vanity URL validation is expensive.  Optimize the process as much
     * as possible
     * @param args input params
     * @param ics ics context
     * @return calcualtion context, containing pointers to heavy objects
     */
    private VanityUrlCalculationContext getVanityUrlCalculationContext(Map args, ICS ics) {
        String c = (String) args.get("c");
        String cid = (String) args.get("cid");
        String key = "gsf:vanityUrlCalculationContext:"+c+":"+cid;
        Object o = ics.GetObj(key);
        if (o == null) {
            o = new VanityUrlCalculationContext(c, cid);
            ics.SetObj(key, o);
        }
        return (VanityUrlCalculationContext) o;
    }

    /**
     * Handy context object, which holds expensive objects required when determining if
     * a vanity URL should be created for an asset, and when actually calculating it.
     */
    private static class VanityUrlCalculationContext {

        final AssetId assetId;
        private AssetApiVirtualWebrootDao assetApiVirtualWebrootDao;
        private WraCoreFieldDao wraCoreFieldDao;

        boolean checkedForEnvironment = false;
        private String currentEnvironment;

        boolean checkedForWebroot = false;
        VirtualWebroot virtualWebrootForAsset;

        boolean checkedForWra = false;
        VanityAsset vanityAsset;

        VanityUrlCalculationContext(String c, String cid) {
            assetId = new AssetIdImpl(c, Long.parseLong(cid));
        }

        private AssetApiVirtualWebrootDao getAssetApiVirtualWebrootDao(Map args, ICS ics) {
            if (assetApiVirtualWebrootDao == null) {
                assetApiVirtualWebrootDao = new AssetApiVirtualWebrootDao(ics);
            }
            return assetApiVirtualWebrootDao;
        }

        private WraCoreFieldDao getWraCoreFieldDao(Map args, ICS ics) {
            if (wraCoreFieldDao == null) {
                wraCoreFieldDao = new AssetApiWraCoreFieldDao(ics);
            }
            return wraCoreFieldDao;
        }


        private boolean isVanityAsset(Map args, ICS ics) {
           return getWraCoreFieldDao(args, ics).isWebReferenceable(assetId);
        }

        private boolean isGsfEnvironmentSet(Map args, ICS ics) {
            return getCurrentEnvironment(args, ics) != null;
        }

        private String getCurrentEnvironment(Map args, ICS ics) {
            if (currentEnvironment == null && checkedForEnvironment == false) {
                currentEnvironment = getAssetApiVirtualWebrootDao(args, ics).getVirtualWebrootEnvironment();
                checkedForEnvironment = true;
            }
            return currentEnvironment;
        }

        private VanityAsset getVanityAsset(Map args, ICS ics) {
            if (vanityAsset == null && checkedForWra == false) {
                vanityAsset = getWraCoreFieldDao(args, ics).getWra(assetId);
                checkedForWra = true;
            }
            return vanityAsset;
        }

        private boolean hasVirtualWebroot(Map args, ICS ics) {
            return getVirtualWebrootForAsset(args, ics) != null;
        }

        private VirtualWebroot getVirtualWebrootForAsset(Map args, ICS ics) {
            if (virtualWebrootForAsset == null && checkedForWebroot == false) {
                AssetApiVirtualWebrootDao vwdao = getAssetApiVirtualWebrootDao(args, ics);
                VanityAsset v = getVanityAsset(args, ics);
                virtualWebrootForAsset = vwdao.lookupVirtualWebrootForAsset(v);
                checkedForWebroot = true;
            }
            return virtualWebrootForAsset;
        }



        /**
         * Check to see if the tag being called is a getTemplateUrl tag. If it is
         * not, we should not be processing this for special links. Note it's not
         * that easy to figure this out, and there could be missing pieces here.
         *
         * @param args tag args
         * @return true if it's a gettemplateurl tag, false otherwise.
         */
        private static boolean isGetTemplateUrl(Map args, ICS ics) {
            if (args.get("c") == null)
                return false;
            if (args.get("cid") == null)
                return false;
            String pagename = (String)args.get(ftMessage.PageName);
            if (pagename == null)
                return false;
            if (pagename.split("/").length < 2)
                return false; // need site/type/tname or site/tname at least for a valid URL
            if (args.get(PubConstants.WRAPPERPAGE) != null)
                return true; // wrapper is only supported for GTU calls
            else {
                // possible further checks here just in case
            }
            return true;
        }
    }
}
