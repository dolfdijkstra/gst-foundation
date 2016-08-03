/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.wra.navigation;

import java.util.Collection;

import com.fatwire.assetapi.query.Query;

/**
 * @author Dolf Dijkstra
 * @since August 2012
 * 
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation (coming soon)
 *
 */
public interface NavigationService {

    /**
     * 
     * @param depth the maximum number of levels to retrieve
     * @return the NavigationNodes for the page by this name.
     */
    Collection<NavigationNode> getRootNodesForSite(int depth);

    /**
     * @param site name of the site
     * @param depth the maximum number of levels to retrieve
     * @return the NavigationNodes for the page by this name.
     */
    Collection<NavigationNode> getRootNodesForSite(String site, int depth);

    /**
     * @param site name of the site
     * @param depth the maximum number of levels to retrieve
     * @param linkAttribute link attribute string 
     * @return a collection of nodes
     */
    Collection<NavigationNode> getRootNodesForSite(String site, int depth, String linkAttribute);

    /**
     * @param pagename the name of the Page asset
     * @param depth the maximum number of levels to retreive
     * @return the NavigationNode for the page by this name.
     */
    NavigationNode getNodeByName(String pagename, int depth);

    /**
     * @param site name of the site
     * @param pagename the name of the Page asset
     * @param depth the maximum number of levels to retrieve
     * @return the NavigationNode for the page by this name.
     */
    NavigationNode getNodeByName(String site, String pagename, int depth);

    /**
     * @param site name of the site
     * @param pagename the name of the Page asset
     * @param depth the maximum number of levels to retrieve
     * @param linkAttribute link attribute string
     * 
     * @return the NavigationNode for the page by this name.
     */
    NavigationNode getNodeByName(String site, String pagename, int depth, String linkAttribute);

    /**
     * Retrieves the NavigationNodes for the Page with the name
     * <tt>pagename</tt> and in the current site.
     * 
     * @param pagename the name of the Page asset
     * @param depth the maximum number of levels to retrieve
     * @param linkAttribute link attribute string
     * 
     * @return the NavigationNode for the page by this name.
     */
    NavigationNode getNodeByName(String pagename, int depth, String linkAttribute);

    /**
     * @param query the asset query, needs to return Page assets
     * @param depth the maximum number of levels to retrieve
     * @param linkAttribute link attribute string
     * @return the NavigationNode for the page by this name.
     */
    NavigationNode getNodeByQuery(Query query, int depth, String linkAttribute);

}
