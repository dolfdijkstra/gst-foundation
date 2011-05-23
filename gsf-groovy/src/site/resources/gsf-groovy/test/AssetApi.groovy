package test;

import COM.FutureTense.Interfaces.ICS

import com.fatwire.assetapi.common.AssetAccessException
import com.fatwire.assetapi.data.AssetData
import com.fatwire.assetapi.data.AssetId
import com.fatwire.gst.foundation.DebugHelper
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils
import com.openmarket.xcelerate.asset.AssetIdImpl

/**
 * @author Dolf Dijkstra
 *
 */
public class AssetApi implements Action {

    @Override
    public void handleRequest(ICS ics) {
        AssetId id= AssetIdUtils.currentId(ics);

        long t = java.lang.System.nanoTime()
        AssetData data =AssetDataUtils.getAssetData(ics,id)
        long t2 = java.lang.System.nanoTime()

        ics.StreamText("Reading asset took " +DebugHelper.nanoToHuman(t2-t) + ".<br/>");
        String s;
        try {
            s = DebugHelper.printAsset(data);
            ics.StreamText("<pre>");
            ics.StreamText(s);
            ics.StreamText("</pre>");
        } catch (AssetAccessException e) {
            e.printStackTrace();
        }
    }
}
