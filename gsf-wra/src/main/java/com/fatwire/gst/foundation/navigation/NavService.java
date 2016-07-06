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
package com.fatwire.gst.foundation.navigation;

import com.fatwire.assetapi.data.AssetId;

/**
 * Navigation service used to access site plan-based nav structures in WebCenter Sites. Not all data driving this
 * structure needs to come from the Site Plan, but it is designed to work with structures rooted in it.
 *
 * @author Tony Field
 * @since 2016-06-28
 */
public interface NavService<NODE extends Node> {

    /**
     * Set the site to be used by this navigation service. Resetting the site is not permitted
     * @param site site name
     * @throws UnsupportedOperationException as reset is not supported
     */
    void setSite(String site) throws UnsupportedOperationException;

    /**
     * Load the navigation structure based on an object in the site plan.
     * Equivalent to #loadNav(sitePlan, null)
     *
     * @param sitePlan asset ID of the object in the site plan tree. The type of this object is not specified.
     *                 All nodes below this object will be returned, but the specified object will not be.
     * @return Site plan node.
     */
    NODE loadNav(AssetId sitePlan);

    /**
     * Load the navigation structure based on the specified object in the site plan, and employing
     * the params specified to supplement the load operation.
     * @param sitePlan asset ID of the object in the site plan tree. The type of this object is not specified.
     *                 All nodes below this object will be returned, but the specified object will not be.
     * @param params optional param array that can be used by loading code to provide more functionality to the load
     *               operation
     * @return Site plan node.
     */
    NODE loadNav(AssetId sitePlan, Object... params);
}
