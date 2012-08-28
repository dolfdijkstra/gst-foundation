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

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.mda.Dimension;
import com.fatwire.mda.DimensionSetInstance;

/**
 * @author Dolf Dijkstra
 * @since Apr 19, 2011
 */
public interface LocaleService {

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     * 
     * @param c asset type of asset to look up
     * @param cid asset id of asset to look up
     * @param site name of site
     * @param preferredLocaleDimensionId id of locale desired
     * @return AssetId of translation asset.
     */
    public AssetId findTranslation(final String c, final String cid, final String site,
            final String preferredLocaleDimensionId);

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     * 
     * @param id id of asset to look up
     * @param site name of site
     * @param preferredDimension id of locale desired
     * 
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter.
     */

    public AssetId findTranslation(final AssetId id, final String site, final long preferredDimension);

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set for the site has been configured that returns the
     * asset other than the preferred locale, that is considered to be fine and
     * not really the problem of the end user. In other words, a dimension set
     * may dictate that a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     * 
     * @param id id of asset to look up
     * @param preferredDimension id of locale desired
     * @param dimensionSetId dimension set to use to find the translation
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter.
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final long dimensionSetId);

    /**
     * Look up the translation for the asset specified, in the locale specified.
     * <p/>
     * If the desired translation is not available, null will be returned.
     * <p/>
     * If a dimension set has been configured that returns the asset other than
     * the preferred locale, that is considered to be fine and not really the
     * problem of the end user. In other words, a dimension set may dictate that
     * a "backup" language can be returned to the user.
     * <p/>
     * Null, however, is a valid option.
     * 
     * @param id id of asset to look up
     * @param preferredDimension id of locale desired
     * @param dimensionSetName the name of the dimension set to use to find the
     *            translation
     * @return AssetId of translation asset, or null if none is returned by the
     *         dimension set filter. The id parameters is returned if the asset
     *         does not have a locale or if the locale is already of the
     *         preferredDimension
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final String dimensionSetName);

    /**
     * @param id
     * @param preferredDimension
     * @param dimset
     * @return assetif of translated asset.
     * @throws IllegalStateException
     */

    public AssetId findTranslation(final AssetId id, final long preferredDimension, final DimensionSetInstance dimset);

    /**
     * Looksup the DimensionSetInstance for the given name.
     * 
     * @param name
     * @return the DimensionSetInstance by the provided name
     */

    public DimensionSetInstance getDimensionSet(final String name);

    /**
     * Return the dimension of the input asset that corresponds to its locale.
     * If the asset does not have a locale set, returns null
     * 
     * @param id asset
     * @return locale dimension or null
     */

    public Dimension getLocaleForAsset(final AssetId id);

    /**
     * Get the id of the dimension asset for the name specified
     * 
     * @param name dimension name, or locale like en_US
     * @return dimension id, -1 is not found.
     */

    public long getDimensionIdForName(final String name);

    /**
     * Get the AssetId of the dimension asset for the name specified
     * 
     * @param name dimension name, or locale like en_US
     * @return dimension id, null if not found
     */

    public AssetId getDimensionAssetIdForName(final String name);

    /**
     * Get the AssetId of the dimension asset for the name specified
     * 
     * @param name dimension name, or locale like en_US
     * @return dimension id, null if not found
     */

    public Dimension getDimensionForName(final String name);

    /**
     * Shorthand function to get the name given a dimension ID specified.
     * 
     * @param dimensionid ID of a locale. Note the dimension group is not
     *            verified
     * @return dimension name, or locale name, like en_CA, null if not found
     */

    public String getNameForDimensionId(final long dimensionid);

}
