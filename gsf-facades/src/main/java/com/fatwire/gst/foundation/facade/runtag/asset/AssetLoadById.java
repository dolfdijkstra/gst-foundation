/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.runtag.asset;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunnerRuntimeException;

//import com.openmarket.xcelerate.interfaces.IAsset;

/**
 * Loads an asset by id
 * 
 * @author Dolf Dijkstra
 */

public class AssetLoadById extends AbstractAssetLoad {

    public AssetLoadById() {
        super();

    }

    public void setAssetId(final long id) {
        this.set("OBJECTID", id);
    }

    @Override
    protected void postExecute(ICS ics) {
        super.postExecute(ics);
        // errno is not set on failed asset.loads. :(
        // TODO: CONVERT TO USING PUBLIC APIS (TONY FIELD, JUNE 9, 2008)
        // Object o = ics.GetObj(list.getValString("NAME"));
        // if (o instanceof IAsset) {
        // IAsset asset = (IAsset) o;
        // Long id = asset.GetObjectId();
        // if (id == null) {
        // ics.SetErrno(-10006);
        // return;
        // }
        // if (!id.equals(new Long(list.getValString("OBJECTID")))) {
        // ics.SetErrno(-10006);
        // }
        // } else {
        // ics.SetErrno(-10005);
        // }
    }

    protected void handleError(ICS ics) {
        throw new TagRunnerRuntimeException("Loading asset by ID failed for asset ID: " + list.get("OBJECTID"),
                ics.GetErrno(), list, ics.getComplexError(), ics.GetVar("pagename"),
                ics.ResolveVariables("CS.elementname"));

    }

}
