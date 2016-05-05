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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for working with Asset IDs
 * 
 * @author Tony Field
 * @since Mar 9, 2011
 */
public final class AssetIdUtils {
    private AssetIdUtils() {
    }

    /**
     * Convert a string in the form assettype:assetid into an AssetId object.
     * 
     * @param s string in the form assettype:assetid
     * @return AsseId object
     * @throws IllegalArgumentException if input is bad
     * 
     */
    public static AssetId fromString(String s) {
        if (s == null)
            throw new IllegalArgumentException("Invalid null input.");
        int colon = s.indexOf(":");
        if (colon < 1)
            throw new IllegalArgumentException("Invalid input: " + s);
        if (colon == s.length())
            throw new IllegalArgumentException("Invalid input: " + s);
        String type = s.substring(0, colon);
        String sId = s.substring(colon + 1);
        try {
            return new AssetIdImpl(type, Long.valueOf(sId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input: " + s, e);
        }
    }

    /**
     * Converts an AssetId into a String of the form assettype:id.
     * 
     * @param id asset id
     * @return the assetid as a String in the form of assettype:id.
     */
    public static String toString(AssetId id) {
        if (id == null)
            throw new IllegalArgumentException("Invalid null input.");
        return id.getType() + ":" + Long.toString(id.getId());
    }

    /**
     * Creates an AssetId out of 'c' and 'cid' variables on ICS context.
     * 
     * @param ics Content Server context object
     * @return AssetId from c and cid
     * @throws IllegalArgumentException if c or cid is blank.
     */
    public static AssetId currentId(ICS ics) {
        String c = ics.GetVar("c");
        String cid = ics.GetVar("cid");
        if (StringUtils.isBlank(c)) {
            throw new IllegalArgumentException(
                    "CS variable 'c' is not found, cannot make an AssetId of current ICS context");
        }
        if (StringUtils.isBlank(cid)) {
            throw new IllegalArgumentException(
                    "CS variable 'cid' is not found, cannot make an AssetId of current ICS context");
        }
        return new AssetIdImpl(c, Long.parseLong(cid));
    }

    /**
     * Creates an AssetId out of 'p' variable on ICS context with type 'Page'.
     * 
     * @param ics Content Server context object
     * @return AssetId with id from 'p'.
     * @throws IllegalArgumentException if p is blank.
     */
    public static AssetId currentPageId(ICS ics) {
        String p = ics.GetVar("p");

        if (StringUtils.isBlank(p)) {
            throw new IllegalArgumentException(
                    "CS variable 'p' is not found, cannot make an AssetId of current ICS context");
        }
        return new AssetIdImpl("Page", Long.parseLong(p));
    }

    /**
     * Creates an AssetId from c and cid parameters.
     * 
     * @param c the assetype
     * @param cid the assetid, must be a String representing a long value
     * @return AssetId with c and cid paramters
     * @throws IllegalArgumentException if c or cid is blank.
     */
    public static AssetId createAssetId(String c, String cid) {
        if (StringUtils.isBlank(c)) {
            throw new IllegalArgumentException("'c' is blank, cannot make an AssetId of current ICS context");
        }
        if (StringUtils.isBlank(cid)) {
            throw new IllegalArgumentException("'cid' is blank, cannot make an AssetId of current ICS context");
        }
        return new AssetIdImpl(c, Long.parseLong(cid));
    }

    /**
     * Creates an AssetId from c and cid parameters.
     * 
     * @param c the assetype.
     * @param cid the assetid.
     * @return AssetId with c and cid paramters.
     * @throws IllegalArgumentException is c is blank
     */
    public static AssetId createAssetId(String c, long cid) {
        if (StringUtils.isBlank(c)) {
            throw new IllegalArgumentException("'c' is blank, cannot make an AssetId of current ICS context");
        }
        return new AssetIdImpl(c, cid);
    }

}
