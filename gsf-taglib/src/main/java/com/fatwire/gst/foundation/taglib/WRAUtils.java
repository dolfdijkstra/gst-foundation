package com.fatwire.gst.foundation.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.wra.Alias;
import com.fatwire.gst.foundation.facade.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.facade.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.facade.wra.WraCoreFieldDao;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class WRAUtils {
    private final Log LOG = LogFactory.getLog(WRAUtils.class);
    private ICS ics;
    private WraCoreFieldDao wraDao;
    private AliasCoreFieldDao aliasDao;

    public WRAUtils(ICS ics) {
        this.ics = ics;
        wraDao = new WraCoreFieldDao();
        aliasDao = new AliasCoreFieldDao();
    }

    /**
     * @param id id of web-referenceable asset
     * @return WebReferenceableAsset bean containing fields for Web-Referencable asset
     */
    public WebReferenceableAsset getWra(AssetId id) {
    	try {
    		return wraDao.getWra(id);
    	} catch (RuntimeException e) {
    		LOG.warn("Asset " + id + " is not a web-referenceable asset.");
    		return null;
    	}
    }

    /**
     * @param id id of alias asset
     * @return Alias bean containing fields for Alias asset
     */
    public Alias getAlias(AssetId id) {
    	try {
    		return aliasDao.getAlias(id);
    	} catch (RuntimeException e) {
    		LOG.warn("Asset " + id + " is not an alias asset.");
    		return null;
    	}
    }
}
