package com.fatwire.gst.foundation.facade.assetapi;

import com.fatwire.assetapi.common.SiteAccessException;



/**
 * 
 * RuntimeException adaptor for SiteAccessException.
 * @author Dolf Dijkstra
 * @since Apr 6, 2011
 */
public class SiteAccessRuntimeException extends RuntimeException {

    public SiteAccessRuntimeException(SiteAccessException e) {
        super(e);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3433575664006902819L;

}
