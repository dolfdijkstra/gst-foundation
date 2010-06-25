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

package com.fatwire.gst.foundation.facade.assetapi;

import java.util.Arrays;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * Convenient shortcuts for working with AssetData objects
 * 
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class AssetDataUtils {
    private AssetDataUtils() {
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param c
     * @param cid
     * @param attributes
     * @return asset data
     */
    public static AssetData getAssetData(String c, String cid, String... attributes) {
        return getAssetData(new AssetIdImpl(c, Long.valueOf(cid)), attributes);
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param id
     * @param attributes
     * @return asset data
     */
    public static AssetData getAssetData(AssetId id, String... attributes) {
        Session ses = SessionFactory.getSession();
        AssetDataManager mgr = (AssetDataManager) ses.getManager(AssetDataManager.class.getName());
        try {
            return mgr.readAttributes(id, Arrays.asList(attributes));
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data", ftErrors.exceptionerr, e);
        }
    }
}
