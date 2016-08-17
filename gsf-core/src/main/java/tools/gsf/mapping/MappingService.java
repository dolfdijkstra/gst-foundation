/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package tools.gsf.mapping;

import tools.gsf.facade.assetapi.AssetIdWithSite;

import java.util.Map;


/**
 * Service the read the mappings for an asset.
 *
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public interface MappingService {

    /**
     * Determine the asset (CSElement or Template) that has mapped values contained
     * within it, given the input pagename. Note that this is UNABLE to resolve a
     * mapping that applies to an element that is called as an element from within the
     * root element of another page.
     * @param pagename the pagename, whose rootelement is either the mapped template or mapped cselement.
     * @return the asset id and site, if found, or null
     */
    AssetIdWithSite resolveMapped(String pagename);

    /**
     * Determine the asset (CSElement or Template) that has mapped values contained
     * within it, given the input eid and tid variables. Note that it is expected that
     * at most one of these two will be set at a time, and never both.
     * @param eid the eid resdetails value
     * @param tid the tid resdetails value
     * @param site the site variable value
     * @return the asset id and site, if found, or null
     */
    AssetIdWithSite resolveMapped(String eid, String tid, String site);

    /**
     * Reads the mappings for the asset and the site.
     *
     * @param id the asset that holds the mapping.
     * @return the mappings for the asset.
     */
    Map<String, MappingValue> readMapping(AssetIdWithSite id);

}
