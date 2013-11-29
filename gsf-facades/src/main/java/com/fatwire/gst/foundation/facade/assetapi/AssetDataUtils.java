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
import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetDataManager;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

/**
 * Convenient shortcuts for working with AssetData objects
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
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
     * @deprecated inconsistent method signature, replaced by {@link AssetDataUtils#getAssetData(AssetId, String...)}. 
     */
    @Deprecated
    public static AssetData getAssetData(String c, String cid, String... attributes) {
        return getAssetData(AssetIdUtils.createAssetId(c, cid), attributes);
    }

    /**
     * Read all attributes for the given asset id.
     * 
     * @param id must be valid or else an exception is thrown
     * @return asset data, never null.
     * @deprecated use #getAssetData(ICS, AssetId) instead
     */
    @Deprecated
    public static AssetData getAssetData(AssetId id) {
        return getAssetData(ICSFactory.getOrCreateICS(), id);
    }

    /**
     * Read all attributes for the given asset id.
     * 
     * @param ics ICS context
     * @param id must be valid or else an exception is thrown
     * @return asset data, never null.
     */
    public static AssetData getAssetData(ICS ics, AssetId id) {
        AssetDataManager mgr = (AssetDataManager) getSession(ics).getManager(AssetDataManager.class.getName());
        try {
            for (AssetData data : mgr.read(Collections.singletonList(id))) {
                return data; // first one wins
            }
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data: " + e, ftErrors.exceptionerr, e);
        }
        throw new CSRuntimeException("Asset not found: " + id, ftErrors.badparams);
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param id
     * @param attributes
     * @return asset data
     * @deprecated use #getAssetData(ICS, AssetId, String...) instead
     */
    @Deprecated
    public static AssetData getAssetData(AssetId id, String... attributes) {
        return getAssetData(ICSFactory.getOrCreateICS(), id, attributes);
    }

    /**
     * Return the AssetData for the specified asset
     * 
     * @param id
     * @param attributes
     * @return asset data
     */
    public static AssetData getAssetData(ICS ics, AssetId id, String... attributes) {
        AssetDataManager mgr = (AssetDataManager) getSession(ics).getManager(AssetDataManager.class.getName());
        try {
            return mgr.readAttributes(id, Arrays.asList(attributes));
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data: " + e, ftErrors.exceptionerr, e);
        }
    }

    /**
     * This is a convenience method to read AssetData for the current c/cid
     * asset on the ics scope.
     * 
     * @param ics
     * @param attributes
     * @return asset data
     */
    public static AssetData getCurrentAssetData(ICS ics, String... attributes) {
        AssetDataManager mgr = (AssetDataManager) getSession(ics).getManager(AssetDataManager.class.getName());
        try {
            AssetId id = AssetIdUtils.currentId(ics);
            return mgr.readAttributes(id, Arrays.asList(attributes));
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data: " + e, ftErrors.exceptionerr, e);
        }

    }

    /**
     * This is a convenience method to read AssetData for the current c/cid
     * asset on the ics scope.
     * 
     * @param ics
     * @return asset data
     */
    public static AssetData getCurrentAssetData(ICS ics) {
        AssetDataManager mgr = (AssetDataManager) getSession(ics).getManager(AssetDataManager.class.getName());
        AssetId id = AssetIdUtils.currentId(ics);
        try {
            for (AssetData data : mgr.read(Collections.singletonList(id))) {
                return data; // first one wins
            }
        } catch (AssetAccessException e) {
            throw new CSRuntimeException("Failed to read attribute data: " + e, ftErrors.exceptionerr, e);
        }
        throw new CSRuntimeException("Asset not found: " + id, ftErrors.badparams);

    }

    private static Session getSession(ICS ics) {
        return SessionFactory.getSession(ics);
    }

}
