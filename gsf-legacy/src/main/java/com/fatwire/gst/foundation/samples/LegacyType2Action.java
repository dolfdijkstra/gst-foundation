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

package com.fatwire.gst.foundation.samples;

import COM.FutureTense.Interfaces.ICS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.controller.annotation.Mapping;
import com.fatwire.gst.foundation.controller.annotation.Bind;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;

/**
 * 
 * This class' only purpose is to save time when testing legacy (and deprecated) Actions
 * in WCS 12c. We don't want to create an entire (gsf-sample-legacy) module just for this,
 * let alone rewire the std MAVEN build strategy to fit this in.
 * 
 * In future releases, we will be implementing brand new samples for the CORE features / classes,
 * but those will go into the gsf-sample module. 
 * 
 * Once all action-related classes are physically wiped out from the GSF project's codebase,
 * we will get rid of this class, too.
 * 
 * @author fvillalba
 *
 */
public class LegacyType2Action implements Action {
	
	private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.samples.LegacyType2Action");

	/**
     * Inject an ICS into the action for convenience
     */
    @InjectForRequest
    public ICS ics;
 
    /**
     * Provide a place to put the data that will be passed into the page context for use with expression language
     */
    @InjectForRequest
    public Model model;
 
    /**
     * Provide a DAO that allows an asset to be easily mapped
     */
    @InjectForRequest
    protected TemplateAssetAccess templateAssetAccess;
    
    /**
     * Bind rendermode to local variable
     */
    @Bind(value="rendermode")
    protected String myRenderMode;
    
    /**
     * Bind map key to local variable
     */
    @Mapping(value="myMappedAsset")
    protected String mappedAsset;
 
    public void handleRequest(ICS ics) {
        // get the asset id corresponding to the ICS variables c, cid
        AssetId id = templateAssetAccess.currentId();
        
        TemplateAsset asset = templateAssetAccess.read(id);
		model.add("legacyGsfAsset", new ScatteredAsset(asset.getDelegate()));
		
		LOG.info("Value copied from ICS variable rendermode onto local variable myRenderMode = " + myRenderMode);
		
		LOG.info("Value copied from Map Key myMappedAsset onto local variable mappedAsset = " + mappedAsset);
    }
}