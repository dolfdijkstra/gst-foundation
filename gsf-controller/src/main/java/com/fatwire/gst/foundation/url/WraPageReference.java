package com.fatwire.gst.foundation.url;

import java.util.Iterator;
import java.util.Map;

import COM.FutureTense.Common.E;
import COM.FutureTense.Export.ReferenceException;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.openmarket.xcelerate.publish.PageRef;
import com.openmarket.xcelerate.publish.PubConstants;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * This is the WebReferenceable assets PageRef class. This overrides the default
 * PageRef, so we can manipulate the arguments that go into the assembler while
 * we have database access.
 * <p/>
 * <p> It will alter the input args map by adding the derived parameters
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
 * @see COM.FutureTense.Export.Reference
 * @see COM.FutureTense.Interfaces.IReference
 * @see com.fatwire.cs.core.uri.Definition
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

    @Override
    public void setParameters(Map args, ICS ics) throws ReferenceException {
        _dumpVars(args, ics);
        if (getSatelliteContext() == SatelliteContext.SATELLITE_SERVER && getAppType() == AppType.CONTENT_SERVER) {
            String current_environment = getEnvironment(ics);
            String path = getPathForAsset((String) args.get("c"), (String) args.get("cid"));
            if (goodString(current_environment) && goodString(path)) {
                GSTVirtualWebroot vw = findMatchingVirtualWebroot(ics, current_environment, path);
                if (vw != null) {
                    args.put("virtual-webroot", vw.getEnvVWebroot());
                    args.put("url-path", path.substring(vw.getMasterVWebroot().length() + 1));
                    String pagename = ics.GetProperty(WraPathAssembler.DISPATCHER_PROPNAME, "ServletRequest.properties", true);
                    if (!goodString(pagename)) {
                        pagename = "GST/Dispatcher";
                    }
                    if (args.get(PubConstants.WRAPPERPAGE) != null) args.put(PubConstants.WRAPPERPAGE, pagename);
                    else args.put("pagename", pagename);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Not adding WRAPathAssembler args because no matching virtual webroot found for path " + path + " and environemnt " + current_environment);
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Not adding WRAPathAssembler args because env_name is not set, or we cannot find the path for the input asset.  Args:" + args);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Not adding WRAPathAssembler args because context is not satellite server (it is " + getSatelliteContext() + ") and the app type is not ContentServer (it is " + getAppType() + ").  Args: " + args);
            }
        }
        super.setParameters(args, ics);
    }

    private String getPathForAsset(String c, String cid) {
        if (goodString(c) && goodString(cid)) {
            AssetData data = AssetDataUtils.getAssetData(c, cid, "path");
            return AttributeDataUtils.asString(data.getAttributeData("path"));
        }
        return null;
    }

    private GSTVirtualWebroot findMatchingVirtualWebroot(ICS ics, String current_environment, String path) {
        for (GSTVirtualWebroot vw : GSTVirtualWebroot.getAllVirtualWebroots(ics)) {
            // find longest first one that is found in the prefix of path. that is virtual-webroot
            if (current_environment.equals(vw.getEnvName()) && path.startsWith(vw.getMasterVWebroot())) {
                return vw;
            }
        }
        return null; // no match
    }

    private String getEnvironment(ICS ics) {
        String environmentName = System.getProperty("com.fatwire.gst.foundation.env-name", null);
        if (environmentName == null) {
            // allow user to have accidentally mis-configured things
            environmentName = ics.GetProperty("com.fatwire.gst.foundation.env-name");
        }
        return environmentName;
    }

    /**
     * dump vars to disk
     *
     * @param ftValList
     * @param ics
     */
    private void _dumpVars(Map ftValList, ICS ics) {
        if (log.isDebugEnabled()) {
            for (Iterator<E> i = ftValList.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();
                log.debug(ics.ResolveVariables("CS.elementname") + ": " + e.getKey() + "=" + e.getValue());
            }
        }
    }
}
