package com.fatwire.gst.foundation.facade.runtag.asset;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunnerRuntimeException;

//import com.openmarket.xcelerate.interfaces.IAsset;

/**
 * Loads an asset by id
 * @author Dolf Dijkstra
 */

public class AssetLoadById extends AbstractAssetLoad
{

    public AssetLoadById()
    {
        super();

    }

    public void setAssetId(final long id)
    {
        this.set("OBJECTID", id);
    }

    @Override
    protected void postExecute(ICS ics)
    {
        super.postExecute(ics);
        // errno is not set on failed asset.loads. :(
        // TODO: CONVERT TO USING PUBLIC APIS (TONY FIELD, JUNE 9, 2008)
//		Object o = ics.GetObj(list.getValString("NAME"));
//		if (o instanceof IAsset) {
//			IAsset asset = (IAsset) o;
//			Long id = asset.GetObjectId();
//			if (id == null) {
//				ics.SetErrno(-10006);
//				return;
//			}
//			if (!id.equals(new Long(list.getValString("OBJECTID")))) {
//				ics.SetErrno(-10006);
//			}
//		} else {
//			ics.SetErrno(-10005);
//		}
    }

    protected void handleError(ICS ics)
    {
        throw new TagRunnerRuntimeException("Loading asset by ID failed for asset ID: " + list.get("OBJECTID"), ics.GetErrno(), list, ics.getComplexError(), ics.GetVar("pagename"), ics.ResolveVariables("CS.elementname"));

    }

}
