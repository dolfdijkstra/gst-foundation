package com.fatwire.gst.foundation.pageref;

import java.util.Map;
import java.util.Set;

import COM.FutureTense.Export.ReferenceException;
import COM.FutureTense.Interfaces.ICS;

import com.openmarket.xcelerate.publish.PageRef;

/**
 * 
 * This is the WebReferenceable assets PageRef class. This overrides the default
 * PageRef, so we can manipulate the arguments that go into the assembler while
 * we have database access.
 * 
 * <p>
 * In Assembler we want Definition
 * </p>
 * <p>
 * <ll>
 * <li>PageRef extends Reference implements IPageRef</li>
 * <li>Reference implements IReference</li>
 * <li>interface IPageRef extends IReference</li>
 * <li>interface IReference extends Definition</li>
 * </ll>
 * </p>
 * 
 * 
 * @author Dolf Dijkstra
 * @since Jun 17, 2010
 * @see COM.FutureTense.Export.Reference
 * @see COM.FutureTense.Interfaces.IReference
 * @see com.fatwire.cs.core.uri.Definition
 */

public class WraPageReference extends PageRef {

    @SuppressWarnings("unchecked")
    Map<String, String> toMap(Map map) {
        return (Map<String, String>) map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.xcelerate.publish.PageRef#setParameters(java.util.Map,
     * COM.FutureTense.Interfaces.ICS)
     */
    @Override
    public void setParameters(Map m, ICS ics) throws ReferenceException {
        Map<String, String> map = toMap(m);

        // we have to manipulate the map

        // we get c and cid
        // we need path of the asset
        for (Map.Entry<String, String> e : map.entrySet()) {
            log.debug(e.getKey() + "=" + e.getValue());
        }
        if (true/*
                 * getSatelliteContext() == SatelliteContext.SATELLITE_SERVER &&
                 * getAppType() == AppType.CONTENT_SERVER
                 */) {
            // if CS leave alone, work only if apptype is contentserver
            // how do we detect that this is for us
            String c = map.get("c");
            String cid = map.get("cid");

        }
        super.setParameters(map, ics);
    }
}
