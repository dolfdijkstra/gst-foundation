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

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Retrieves and lists the assets that match the recommendation constraints
 * passed to the tag.
 * <p>
 * Syntax
 * <p>
 * <COMMERCECONTEXT.GETRECOMMENDATION COLLECTION="recommendationname"
 * [LIST="inputlist"] [VALUE="rating"] [MAXCOUNT="assetcount"]
 * LISTVARNAME="assetlist" [FILTER="true|false"]/>
 * <p>
 * This tag returns a list containing up to the specified number of recommended
 * assets. The recommendations, and the returned order of them, are based on the
 * details of the referenced recommendation asset. This tag automatically
 * calculates segment affinity and promotion affinity for the current visitor,
 * if they have not yet been calculated. For information about creating
 * recommendations, including context-based recommendations, see the CS
 * Developer's Guide.
 * <p>
 * NOTE: This tag also causes compositional dependencies to be recorded for all
 * assets that contribute to the returned lists, and may, under the right
 * conditions, have the same effect as a RENDER.UNKNOWNDEPS tag.
 * 
 * @author Tony Field
 * @since Apr 12, 2010
 */
public final class GetRecommendations extends AbstractTagRunner {
    public GetRecommendations() {
        super("COMMERCECONTEXT.GETRECOMMENDATIONS");
    }

    /**
     * @param collection Input parameter. Name of the recommendation. The sort
     *            and selection criteria defined in the recommendation are used
     *            to create the list of possible assets. You can constrain this
     *            list by using the MAXCOUNT argument, defined below. If there
     *            are any promotions in place that override this recommendation,
     *            it substitutes the name of the promotion, instead.
     */
    public void setCollection(String collection) {
        this.set("COLLECTION", collection);
    }

    /**
     * @param collectionid ID of the recommendation. The sort and selection
     *            criteria defined in the recommendation are used to create the
     *            list of possible assets. You can constrain this list by using
     *            the MAXCOUNT argument, defined below. If there are any
     *            promotions in place that override this recommendation, it
     *            substitutes the name of the promotion, instead.
     */
    public void setCollectionId(String collectionid) {
        this.set("COLLECTIONID", collectionid);
    }

    /**
     * @param collectionid ID of the recommendation. The sort and selection
     *            criteria defined in the recommendation are used to create the
     *            list of possible assets. You can constrain this list by using
     *            the MAXCOUNT argument, defined below. If there are any
     *            promotions in place that override this recommendation, it
     *            substitutes the name of the promotion, instead.
     */
    public void setCollectionId(long collectionid) {
        this.setCollectionId(Long.toString(collectionid));
    }

    /**
     * @param inputList Input parameter. name of the list of assets you want to
     *            be used as the input for the calculation. This argument is
     *            applicable only if the recommendation named by COLLECTION is a
     *            context-based recommendation. Columns are assettype and
     *            assetid.
     */
    public void setList(String inputList) {
        this.set("LIST", inputList);
    }

    /**
     * @param rating Input parameter. Default rating for assets that do not have
     *            one. If you do not declare a value, unrated assets are
     *            assigned a default rating of 50 on a scale of 1-100.
     */
    public void setValue(int rating) {
        this.set("VALUE", Integer.toString(rating));
    }

    /**
     * @param assetcount Input parameter. Maximum number of assets to return.
     *            Use this value to constrain the list of recommended assets.
     */
    public void setMaxCount(int assetcount) {
        this.set("MAXCOUNT", Integer.toString(assetcount));
    }

    /**
     * @param assetlist Input and output parameter. As input, name you want to
     *            assign to the list of assets returned on output. Its columns
     *            are: assettype and assetid.
     */
    public void setListVarName(String assetlist) {
        this.set("LISTVARNAME", assetlist);
    }

    /**
     * @param filter Input parameter. A Boolean value: true specifies that no
     *            assets in the input list can be returned as output; false
     *            (default) allows input assets to be returned as output.
     */
    public void setFilter(boolean filter) {
        this.set("FILTER", Boolean.toString(filter).toLowerCase());
    }

    /**
     * Easy-to-use utility method to return recommendations for the specified
     * asset.
     * 
     * @param ics context
     * @param recId id of recommendation to return
     * @param max max count
     * @return IList containing recommendations
     * @see IterableIListWrapper
     * @deprecated replaced by {@link Recommendations#getRecommendations(ICS, String, int)}. 
     */
    @Deprecated
    public static IList getRecommendations(ICS ics, long recId, int max) {
        GetRecommendations gr = new GetRecommendations();
        gr.setCollectionId(recId);
        gr.setMaxCount(max);
        String list = IListUtils.generateRandomListName();
        gr.setListVarName(list);
        gr.execute(ics);
        IList result = ics.GetList(list);
        ics.RegisterList(list, null); // unregister
        return result;
    }

}
