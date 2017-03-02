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
package tools.gsf.samples.navigation.siteplan;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.impl.SitePlanNavService;
import tools.gsf.navigation.impl.TrivialAssetNodeImpl;

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
public final class LightweightSitePlanNavService extends SitePlanNavService<TrivialAssetNodeImpl> {

    private final TemplateAssetAccess dao;

    public LightweightSitePlanNavService(ICS ics, TemplateAssetAccess dao) {
        super(ics);
        this.dao = dao;
    }

    protected TrivialAssetNodeImpl createAssetNode(AssetId assetId) {
    	// NOTE: here you could instantiate your own AssetNode implementation. That class could have
    	//       its own methods and could extend any class you wanted (yes, even HashMap ;-)  ). 
    	//       You could even return subtype-specific implementations (for instance, via a
    	//       TrivialAssetNodeFactory component).     	
    	return new TrivialAssetNodeImpl(this.dao, assetId);
    }

}