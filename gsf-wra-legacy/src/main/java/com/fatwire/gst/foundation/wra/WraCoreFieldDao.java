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

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;

/**
 * Dao for dealing with core fields in a WRA This DAO is not aware of Aliases.
 * To work with Alias assets, see {@link AliasCoreFieldDao}.}
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 21, 2010
 */
public interface WraCoreFieldDao {

    public static String[] WRA_ATTRIBUTE_NAMES = { "metatitle", "metadescription", "metakeyword", "h1title",
            "linktext", "path", "template", "id", "name", "subtype", "startdate", "enddate", "status" };
    public static String[] VANITY_ATTRIBUTE_NAMES = { "path", "template", "id", "name", "subtype", "startdate",
            "enddate", "status" };

    /**
     * Method to test whether or not an asset is web-referenceable.
     * 
     * @param id asset ID to check
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(AssetId id);

    /**
     * Method to test whether or not an asset is web-referenceable.
     *
     * @param candidate asset to check for web-referenceable support.
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(TemplateAsset candidate);

    /**
     * Method to test whether or not an asset is web-referenceable. 
     *
     * @param candidate asset to check for web-referenceable support.
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(WebReferenceableAsset candidate);

    /**
     * Method to test if an asset has a vanity url, that is has a path field.
     * 
     * @param id asset ID to check
     * @return true if the asset has a vanity url, false if it is not
     */
    public boolean isVanityAsset(AssetId id);

    /**
     * Return a web referenceable asset bean given an input id. Required fields
     * must be set or an exception is thrown.
     * 
     * @param id asset id
     * @return WebReferenceableAsset, never null
     * @see #isWebReferenceable
     */
    public WebReferenceableAsset getWra(AssetId id);

    /**
     * Locate the page that contains the specified Web-Referenceable Asset.
     * <p>
     * A WRA is supposed to just be placed on one page (in the unnamed
     * association block), and this method locates it. If it is not found, 0L is
     * returned.
     * <p>
     * If multiple matches are found, a warning is logged and the first one is
     * returned.
     * 
     * @param wraAssetIdWithSite id of the web-referenceable asset. Site is
     *            included
     * @return page asset ID or 0L.
     */
    public long findP(AssetIdWithSite wraAssetIdWithSite);

    /**
     * @param c current asset
     * @param cid content id
     * @return the name of the site that this asset belongs to.
     */
    public String resolveSite(String c, String cid);

    /**
     * @param id asset id
     * @return the VanityAsset for the id, null is not found or not a VanityAsset
     */
    public VanityAsset getVanityWra(AssetId id);

}
