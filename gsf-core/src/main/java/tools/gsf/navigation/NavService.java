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

import com.fatwire.assetapi.data.AssetId;

import java.util.List;

/**
 * Navigation service used to access site plan-based nav structures in WebCenter Sites. Not all data driving this
 * structure needs to come from the Site Plan, but it is designed to work with structures rooted in it.
 *
 * @author Tony Field
 * @since 2016-06-28
 */
public interface NavService<NODE extends Node> {

    /**
     * Load the navigation structure based on an object in the site plan.
     *
     * @param assetInSitePlan asset ID of the object in the site plan tree. The type of this object is not specified.
     *                        This object, as well as all nodes below this object will be returned.
     * @return Site plan node.
     */
    NODE loadNav(AssetId assetInSitePlan);

    /**
     * Return the breadcrumb path from the root of the site plan to the specified asset in the navigation
     * structure.
     *
     * @param id The id of the asset whose path to the root of the navigation structure will be traced.
     * @return A list of assets starting with the root of the navigation structure up to the specified asset.
     */
    List<AssetId> getBreadcrumb(AssetId id);

}
