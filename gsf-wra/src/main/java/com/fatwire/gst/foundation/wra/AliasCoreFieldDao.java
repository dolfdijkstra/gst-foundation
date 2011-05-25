/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.wra;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;

/**
 * Dao for dealing with core fields in an alias. Aliases may override fields in
 * their target WRA if it points to another asset. Aliases may also refer to
 * external URLs.
 * 
 * @author Tony Field
 * @since Jul 21, 2010
 */
public interface AliasCoreFieldDao {

    /**
     * Return an AssetData object containing the core fields found in an alias
     * asset.
     * <p/>
     * Also includes selected metadata fields:
     * <ul>
     * <li>id</li>
     * <li>name</li>
     * <li>subtype</li>
     * <li>startdate</li>
     * <li>enddate</li>
     * <li>status</li>
     * </ul>
     * 
     * @param id id of alias asset
     * @return AssetData containing core fields for Alias asset
     */
  //  public AssetData getAsAssetData(AssetId id);

    /**
     * Method to test whether or not an asset is an Alias. todo: low priority:
     * optimize as this will be called at runtime
     * 
     * @param id asset ID to check
     * @return true if the asset is a valid Alias asset, false if it is not
     */
//    public boolean isAlias(AssetId id);

    /**
     * Return an alias asset bean given an input id. Required fields must be set
     * or an exception is thrown.
     * 
     * @param id asset id
     * @return Alias
     * @see #isAlias
     */
    public Alias getAlias(AssetId id);

}
