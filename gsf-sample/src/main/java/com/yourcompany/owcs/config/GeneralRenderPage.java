/*
 * Copyright 2012 Metastratus Web Solutions Limited.  All Rights Reserved.
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
package com.yourcompany.owcs.config;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.def.AssetAssociationDef;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class renders a standard WRA and loads a specific named association and places it into the model.
 * <p/>
 * It exists as a sample that a company can use to build its own page actions.
 *
 * @author Tony Field
 * @since 2012-08-24
 */
public class GeneralRenderPage implements Action {

    protected static final Log LOG = LogFactory.getLog(GeneralRenderPage.class.getPackage().getName());

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
    protected TemplateAssetAccess assetDao;

    /**
     * Process and handle the request. This method might be responsible for invoking
     * the view as well.
     *
     * @param ics Content Server context.
     */
    public void handleRequest(final ICS ics) {
        // get the asset id corresponding to the ICS variables c, cid
        AssetId id = assetDao.currentId();

        // read the asset into a format that is easy to manipulate
        TemplateAsset asset = assetDao.read(id);

        // this would be a very good place for a security check or something similar!

        // load the current asset into a TemplateAsset object (not actually used in this example)
        model.add("asset", new ScatteredAsset(asset.getDelegate()));

        // put the named associations for this asset into the model as well for convenience
        for (AssetAssociationDef assoc : asset.getAssetTypeDef().getAssociations()) {
            String assocName = assoc.getName();
            if (assoc.isMultiple()) {
                model.add(assocName, asset.getAssociatedAssets(assocName));
            } else {
                model.add(assocName, asset.getAssociatedAsset(assocName));
            }
        }
    }
}
