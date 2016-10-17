/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
