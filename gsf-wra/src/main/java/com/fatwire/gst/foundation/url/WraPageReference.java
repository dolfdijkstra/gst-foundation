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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.xcelerate.publish.PageRef#setParameters(java.util.Map,
     * COM.FutureTense.Interfaces.ICS)
     */

    
    public static final String GST_DISPATCHER = "GST/Dispatcher";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setParameters(Map args, ICS ics) throws ReferenceException {

        // no processing to do if not serving a page for SS
        if (getSatelliteContext() == SatelliteContext.SATELLITE_SERVER && isGetTemplateUrl(args)) {
            AssetId id = new AssetIdImpl((String) args.get("c"), Long.parseLong((String) args.get("cid")));
            AssetApiVirtualWebrootDao vwDao = new AssetApiVirtualWebrootDao(ics);
            WraCoreFieldDao wraDao = new AssetApiWraCoreFieldDao(ics);
            String currentEnvironment = vwDao.getVirtualWebrootEnvironment();
            // only look up webroots for WRAs when the environment is configured
            if (currentEnvironment != null && wraDao.isVanityAsset(id)) {
                 VanityAsset wra = wraDao.getVanityWra(id);
                // get the webroot
                VirtualWebroot vw = vwDao.lookupVirtualWebrootForAsset(wra);
                if (vw != null) {
                    // set the special fields
                    args.put("virtual-webroot", vw.getEnvironmentVirtualWebroot());
                    args.put("url-path", wra.getPath().substring(vw.getMasterVirtualWebroot().length()));
                    // has pagename been set? if not, use default.
                    String pagename = ics.GetProperty(WraPathAssembler.DISPATCHER_PROPNAME,
                            "ServletRequest.properties", true);
                    if (!goodString(pagename)) {
                        pagename = GST_DISPATCHER;
                    }
                    // pagename or wrapperpage depending on whether or not we're
                    // going to use a wrapper.
                    if (args.get(PubConstants.WRAPPERPAGE) != null)
                        args.put(PubConstants.WRAPPERPAGE, pagename);
                    else
                        args.put("pagename", pagename);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Not adding WRAPathAssembler args because no matching virtual webroot found for path "
                                + wra.getPath() + " and environemnt " + currentEnvironment);
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    if (currentEnvironment == null) {
                        log.debug("Not adding WraPathAssembler args because virtual webroot environment is not configured");
                    } else {
                        log.debug("Not adding WraPathAssembler args because asset " + id + " is not web referenceable.");
                    }
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Not adding WRAPathAssembler args because context is not satellite server (it is "
                        + getSatelliteContext() + ").  Args: " + args);
            }
        }
        super.setParameters(args, ics);
    }

    /**
     * Check to see if the tag being called is a getTemplateUrl tag. If it is
     * not, we should not be processing this for special links. Note it's not
     * that easy to figure this out, and there could be missing pieces here.
     * 
     * @param args tag args
     * @return true if it's a gettemplateurl tag, false otherwise.
     */
    private boolean isGetTemplateUrl(Map<String, String> args) {
        if (args.get("c") == null)
            return false;
        if (args.get("cid") == null)
            return false;
        String pagename = args.get(ftMessage.PageName);
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
