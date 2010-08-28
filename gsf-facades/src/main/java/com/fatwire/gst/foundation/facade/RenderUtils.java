package com.fatwire.gst.foundation.facade;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

public class RenderUtils {

    private RenderUtils() {
    }

    /**
     * Checks if the pagelet should be cached. Takes into consideration if
     * current pagelet is rendered for Satellite Server.
     * 
     * @param ics
     * @param pname
     *            the pagename
     * @return
     */
    public static boolean isCacheable(final ICS ics, final String pname) {
        return CacheManager.clientIsSS(ics) ? ics.getPageData(pname).getSSCacheInfo().shouldCache() : ics.getPageData(
                pname).getCSCacheInfo().shouldCache();
    }

}
