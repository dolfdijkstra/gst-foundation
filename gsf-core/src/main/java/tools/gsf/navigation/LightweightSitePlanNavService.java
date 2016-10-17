/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.navigation;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;

/**
 * Simple implementation of the SitePlanNavService that simply populates the
 * node assets with the name and template of the page asset from the site plan
 * tree.
 *
 * Alternate implementations of this could perform complex logic in the
 * #populateNodeData(AssetId) method to figure out what data should be
 * loaded into this object.
 *
 * @author Tony Field
 * @since 2016-07-11
 */
public final class LightweightSitePlanNavService extends SitePlanNavService {

    private final TemplateAssetAccess dao;

    public LightweightSitePlanNavService(ICS ics, TemplateAssetAccess dao) {
        super(ics);
        this.dao = dao;
    }

    /**
     * Method to retrieve data that will be loaded into a node. Implementing classes should take care
     * to be very efficient both for cpu time as well as memory usage.
     * @param id asset ID to load
     * @return asset data in the form of a TemplateAsset, never null
     */
    protected TemplateAsset getNodeData(AssetId id) {
        return dao.read(id, "name", "template");
    }
}
