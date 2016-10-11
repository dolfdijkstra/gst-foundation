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
 * Service that exposes hierarchical navigation structures in the form of <code>Node</code>s.
 *
 * The service also allows an object present in or related to the navigation structures to access its breadcrumb.
 * An object's breadcrumb is defined as the <em>preferred</em> path through the navigation structures from the
 * root of the entire site to the specified object.
 *
 * In specifying "preferred" it therefore follows that an object that is present or related to more than one location
 * in the navigation structures (and can therefore be reached by traversing the navs in more than one way) may only
 * have one single breadcrumb path.
 *
 * @author Tony Field
 * @since 2016-06-28
 */
public interface NavService<N extends Node, S, P> {

    /**
     * Load the nodes corresponding to the nav structure specified.
     *
     * @param id an identifier that specifies which nav structure should be loaded.
     * @return a list of the nodes at the root of the nav structure specified. Never null.
     * @throws IllegalArgumentException if the structure specified is invalid.
     */
    List<N> loadNav(S id);

    /**
     * Return the preferred breadcrumb path from the root of the site to the object specified.
     *
     * An object's breadcrumb is defined as the <em>preferred</em> path through the navigation structures from the
     * root of the entire site to the specified object.
     *
     * In specifying "preferred" it therefore follows that an object that is present or related to more than one location
     * in the navigation structures (and can therefore be reached by traversing the navs in more than one way) may only
     * have one single breadcrumb path.
     *
     * @param obj object participating in the navigation structure
     * @return list of nodes, starting with the root node of the site and ending with the node corresponding to
     * the specified object.
     * @throws IllegalArgumentException if the specified object is not present or related to an object in the nav
     * structures of this site.
     */
    List<N> getBreadcrumb(P obj);

}
