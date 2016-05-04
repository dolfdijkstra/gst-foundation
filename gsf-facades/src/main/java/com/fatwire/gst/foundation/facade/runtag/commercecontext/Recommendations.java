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

package com.fatwire.gst.foundation.facade.runtag.commercecontext;

import java.util.Collection;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;

/**
 * Helper to execute the GetRecommendations tag in a assetapi/java friendly way.
 * 
 * @author Dolf.Dijkstra
 * @since Apr 28, 2011
 */
public final class Recommendations {
    private Recommendations() {
    }

    /**
     * Easy-to-use utility method to return recommendations for the specified
     * asset.
     * 
     * @param ics context
     * @param id AssetId of recommendation to return
     * @param max max count
     * @return Collection<AssetId> containing recommendations
     */
    public static Collection<AssetId> getRecommendations(ICS ics, AssetId id, int max) {
        GetRecommendations gr = new GetRecommendations();
        gr.setCollectionId(id.getId());
        gr.setMaxCount(max);
        String list = IListUtils.generateRandomListName();
        gr.setListVarName(list);
        try {
            gr.execute(ics);
            IList result = ics.GetList(list);
            return IListUtils.toAssetIdCollection(result);
        } finally {
            ics.RegisterList(list, null); // unregister
        }

    }

    /**
     * Easy-to-use utility method to return recommendations for the specified
     * asset.
     * 
     * @param ics context
     * @param collection the name of the recommendation
     * @param max max count
     * @return Collection<AssetId> containing recommendations
     * @see IterableIListWrapper
     */
    public static Collection<AssetId> getRecommendations(ICS ics, String collection, int max) {
        GetRecommendations gr = new GetRecommendations();
        gr.setCollection(collection);
        gr.setMaxCount(max);
        String list = IListUtils.generateRandomListName();
        gr.setListVarName(list);
        try {
            gr.execute(ics);
            IList result = ics.GetList(list);
            return IListUtils.toAssetIdCollection(result);
        } finally {
            ics.RegisterList(list, null); // unregister
        }

    }

    /**
     * Get the context-based recommendations from the input Collection.
     * 
     * @param ics Content Server context object
     * @param collection
     * @param input
     * @return the collection with recommended assets
     */
    public static Collection<AssetId> getRecommendations(ICS ics, String collection, Collection<AssetId> input) {
        GetRecommendations gr = new GetRecommendations();
        String inputList = IListUtils.generateRandomListName();
        ics.RegisterList(inputList, new AssetIdIList(inputList, input));
        gr.setList(inputList);
        gr.setCollection(collection);
        String list = IListUtils.generateRandomListName();
        gr.setListVarName(list);
        try {
            gr.execute(ics);
            IList result = ics.GetList(list);
            return IListUtils.toAssetIdCollection(result);
        } finally {
            ics.RegisterList(inputList, null); // unregister
            ics.RegisterList(list, null); // unregister

        }
    }

}
