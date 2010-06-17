package com.fatwire.gst.foundation.pageref;

import java.util.Iterator;
import java.util.Map;

import COM.FutureTense.Common.E;
import COM.FutureTense.Export.ReferenceException;
import COM.FutureTense.Interfaces.FTVAL;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

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
    Map<String, FTVAL> toMap(Map map) {
        return (Map<String, FTVAL>) map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.xcelerate.publish.PageRef#setParameters(java.util.Map,
     * COM.FutureTense.Interfaces.ICS)
     */
    @Override
    public void setParameters(Map map, ICS ics) throws ReferenceException {
        // the map is an FTValList

        // we have to manipulate the map
        // we get c and cid
        // we need path of the asset
        for (Iterator<E> i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            log.debug(ics.ResolveVariables("CS.elementname") + ": "
                    + e.getKey() + "=" + e.getValue());
        }
//        log.debug(ics.ResolveVariables("CS.elementname") + ": "
//                + getSatelliteContext());
//        log.debug(ics.ResolveVariables("CS.elementname") + ": " + getAppType());

        if (true/*
                 * getSatelliteContext() == SatelliteContext.SATELLITE_SERVER &&
                 * getAppType() == AppType.CONTENT_SERVER
                 */) {
            // if CS leave alone, work only if apptype is contentserver
            // how do we detect that this is for us
            //off all the stuff we pass in, we get c/cid/pagename (from site,c and tname)/wrapperpage and the arguments 
            String c = (String) map.get("c" );
            String cid = (String) map.get("cid");
            String pagename = (String)map.get(ftMessage.PageName);
            String wrapperPage = (String)map.get("WRAPPERPAGE");
            String p = (String) map.get("p" );
        }
        super.setParameters(map, ics);
    }
}
