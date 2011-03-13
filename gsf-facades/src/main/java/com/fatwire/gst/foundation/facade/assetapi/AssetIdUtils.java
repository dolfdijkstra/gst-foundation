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

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * Utility class for working with Asset IDs
 *
 * @author Tony Field
 * @since Mar 9, 2011
 */
public final class AssetIdUtils {
    private AssetIdUtils() {}

    /**
     * Convert a string in the form assettype:assetid into an AssetId object.
     * @param s string in the form assettype:assetid
     * @return AsseId object
     * @throws IllegalArgumentException if input is bad
     *
     */
    public static AssetId fromString(String s) {
        if (s == null) throw new IllegalArgumentException("Inalid input: "+s);
        int colon = s.indexOf(":");
        if (colon < 1) throw new IllegalArgumentException("Inalid input: "+s);
        if (colon == s.length()) throw new IllegalArgumentException("Inalid input: "+s);
        String type = s.substring(0,colon);
        String sId = s.substring(colon+1);
        try {return new AssetIdImpl(type,Long.valueOf(sId)); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid input: "+s, e);
        }
    }
}
