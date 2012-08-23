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
package gtcommon.locale

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import COM.FutureTense.Interfaces.ICS

import com.fatwire.assetapi.data.AssetId
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.controller.annotation.Mapping
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate
import com.fatwire.gst.foundation.facade.mda.LocaleService
import com.fatwire.gst.foundation.mapping.AssetName


class ExecuteFilter implements Action {

    @InjectForRequest public LocaleService localeService;

    @Mapping("GlobalDimSet") public AssetName dimSetName

    static Log LOG = LogFactory.getLog("com.fatwire.logging.cs.firstsite");
    @Override
    public void handleRequest(ICS ics) {
       
        /* 1. make sure we are even dealing with a localized site */
        if (ics.GetVar("locale") != null){

            /* 2. check the locale of the current asset to see if it needs to be filtered
             If the input asset does not even have a locale, then it is okay to render
             it as it is an international or multilingual asset. */

            long locale = Long.parseLong(ics.GetVar("locale"))
            AssetId current = AssetIdUtils.currentId(ics)

            if(current == null){
                if (LOG.isDebugEnabled())
                    LOG.debug("About to filter and look up translation for asset "+ics.GetVar("c")+":"+ics.GetVar("cid")+" because it either doesnot have a locale associated with it or it is already in the requested locale")
            }else{
                AssetId translated=localeService.findTranslation (current, locale, dimSetName.getName());

                /* 5. re-set c and cid. Explicitly clear first in case nothing passes the filter.*/
                if (translated != null){
                    ics.RemoveVar ("c");
                    ics.RemoveVar ("cid");
                    ics.SetVar ("c", translated.getType())
                    ics.SetVar ("cid", Long.toString (translated.getId()))
                }
                LOG.debug("Filter complete.  Filter returned asset "+ics.GetVar("c")+":"+ics.GetVar("cid")+".")
            }
        }else{
            LOG.debug("Do not need to filter or look up translation for asset "+ics.GetVar("c")+":"+ics.GetVar("cid")+" because no preferred locale was specified.")
        }
    }
}
