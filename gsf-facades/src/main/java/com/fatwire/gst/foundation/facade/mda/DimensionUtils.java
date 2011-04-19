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

package com.fatwire.gst.foundation.facade.mda;

import java.util.Collection;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionManager;
import com.fatwire.mda.DimensionableAssetManager;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

/**
 * Miscellaneous utilities for working with dimensions
 * 
 * @author Tony Field
 * @since Jun 8, 2009
 */
public final class DimensionUtils {
    /**
     * Shorthand function for returning the DimensionableAssetManager given an
     * ICS context.
     * 
     * @param ics context
     * @return dimensionable asset manager
     */
    public static DimensionableAssetManager getDAM(ICS ics) {
        Session session = SessionFactory.getSession(ics);
        return (DimensionableAssetManager) session.getManager(DimensionableAssetManager.class.getName());
    }

    /**
     * Shorthand function for returning the DimensionManager given an ICS
     * context
     * 
     * @param ics context
     * @return Dimension Manager
     */
    public static DimensionManager getDM(ICS ics) {
        Session session = SessionFactory.getSession(ics);
        return (DimensionManager) session.getManager(DimensionManager.class.getName());
    }

    /**
     * Return the dimension of the input asset that corresponds to its locale.
     * If the asset does not have a locale set, returns null
     * 
     * @param ics context
     * @param id asset
     * @return locale dimension or null
     */
    public static Dimension getLocaleAsDimension(ICS ics, AssetId id) {
        return getLocaleAsDimension(getDAM(ics), id);

    }

    /**
     * Return the dimension of the input asset that corresponds to its locale.
     * If the asset does not have a locale set, returns null
     * 
     * @param ics context
     * @param id asset
     * @return locale dimension or null
     */
    public static Dimension getLocaleAsDimension(DimensionableAssetManager dam, AssetId id) {
        Collection<Dimension> dims = dam.getDimensionsForAsset(id);
        for (Dimension dim : dims) {
            if ("locale".equalsIgnoreCase(dim.getGroup())) {
                return dim;
            }
        }
        return null;
    }

    /**
     * Get the id of the dimension asset for the name specified
     * 
     * @param ics context
     * @param name dimension name, or locale
     * @return dimension id
     */
    public static long getDimensionIdForName(ICS ics, String name) {
        AssetId id = getDimensionAssetIdForName(ics, name);
        return id == null ? null : id.getId();
    }

    /**
     * Get the AssetId of the dimension asset for the name specified
     * 
     * @param ics context
     * @param name dimension name, or locale
     * @return dimension id
     */
    public static AssetId getDimensionAssetIdForName(ICS ics, String name) {
        Dimension dim = getDimensionForName(ics, name);
        return dim == null ? null : dim.getId();
    }

    /**
     * Get the AssetId of the dimension asset for the name specified
     * 
     * @param ics context
     * @param name dimension name, or locale
     * @return dimension id
     */
    public static Dimension getDimensionForName(ICS ics, String name) {
        return getDM(ics).loadDimension(name);

    }

    /**
     * Shorthand function to get the name given a dimension ID specified.
     * 
     * @param ics context
     * @param dimensionid ID of a locale. Note the dimension group is not
     *            verified
     * @return dimension name, or locale name, like en_CA.
     */
    public static String getNameForDimensionId(ICS ics, long dimensionid) {
        return getDM(ics).loadDimension(dimensionid).getName();
    }
}
