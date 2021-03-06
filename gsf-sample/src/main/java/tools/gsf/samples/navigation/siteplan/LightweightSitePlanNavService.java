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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.DefaultBuildersFactory;

import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.siteplan.SitePlanNavService;

/**
 * Simple implementation of the SitePlanNavService that simply populates the
 * node assets with the name and template of the page asset from the site plan
 * tree.
 *
 * Alternate implementations of this could perform complex logic in the
 * #createAssetNode(AssetId) method to figure out what data should be
 * loaded into each object.
 *
 * @author Tony Field
 * @since 2016-07-11
 */
public final class LightweightSitePlanNavService extends SitePlanNavService<MySampleAssetNode> {
	
	private static final Logger LOG = LoggerFactory.getLogger(LightweightSitePlanNavService.class);
	
	private ICS ics;
	
	public LightweightSitePlanNavService(ICS ics, TemplateAssetAccess dao) {
		super(ics, dao);
		this.ics = ics;
		LOG.debug("Initialized instance of LightweightSitePlanNavService with: ics = {} / dao = {} / no sitename specified (defaulting to {})", ics, dao, this.getSitename());
	}
	
    protected MySampleAssetNode createAssetNode(AssetId assetId) {
    	// NOTE: here you could instantiate your own AssetNode implementation. That class
    	//       could have its own methods and could extend any class you wanted (yes, 
    	//       even HashMap ;-)  ). 
    	LOG.debug("Starting LightweightSitePlanNavService.createAssetnode for asset id: {}", assetId);

    	// You can either instantiate the BuildersFactory here as per below OR you can 
    	// have it passed onto this NavService's constructor (for example: by obtaining
    	// it through your project-specific IcsBackedFactory implementation).
    	com.fatwire.assetapi.data.BuildersFactory buildersFactory = new DefaultBuildersFactory(this.ics);

    	// You own 100% of your AssetNode implementation, so you can pass into its
    	// constructor anything you need for it to do what it has to do.
    	return new MySampleAssetNode(buildersFactory, this.getTemplateAssetAccess(), assetId, this.getSitename());
    }

}