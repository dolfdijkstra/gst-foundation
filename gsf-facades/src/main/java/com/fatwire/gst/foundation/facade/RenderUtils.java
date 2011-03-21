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

package com.fatwire.gst.foundation.facade;

import com.fatwire.gst.foundation.facade.runtag.render.LogDep;

import static COM.FutureTense.Interfaces.Utilities.goodString;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

public class RenderUtils {

    private RenderUtils() {
    }

    /**
     * Checks if the pagelet should be cached. Takes into consideration if
     * current pagelet is rendered for Satellite Server.
     * 
     * @param ics
     * @param pname the pagename
     * @return
     */
    public static boolean isCacheable(final ICS ics, final String pname) {
        return CacheManager.clientIsSS(ics) ? ics.getPageData(pname).getSSCacheInfo().shouldCache() : ics.getPageData(
                pname).getCSCacheInfo().shouldCache();
    }

    /**
     * Records the compositions dependancies for SiteEntry,CSElement and
     * Template.
     * 
     * @param ics
     */
    public static void recordBaseCompositionalDependencies(final ICS ics) {

        if (isCacheable(ics, ics.GetVar(ftMessage.PageName))) {
            if (goodString(ics.GetVar("seid"))) {
                LogDep.logDep(ics, "SiteEntry", ics.GetVar("seid"));
            }
            if (goodString(ics.GetVar("eid"))) {
                LogDep.logDep(ics, "CSElement", ics.GetVar("eid"));
            }
            if (goodString(ics.GetVar("tid"))) {
                LogDep.logDep(ics, "Template", ics.GetVar("tid"));
            }

        }
    }

}
