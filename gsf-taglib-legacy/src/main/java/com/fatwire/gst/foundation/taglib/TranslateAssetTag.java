/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.taglib;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * simple tag for translating an asset
 *
 * @author Tony Field
 * @since 11-11-22
 */
public final class TranslateAssetTag extends MultilingualGsfSimpleTag {

    private AssetId assetId = null;

    private String output = null;

    public void setId(AssetId id) {
        this.assetId = id;
    }

    public void setOutput(String s) {
        output = s;
    }

    public void doTag() throws JspException, IOException {
        LOG.trace("gsf:translate-asset start");

        final ICS ics = getICS();

        List<AssetId> toFilterList = Collections.singletonList(assetId);

        Collection<AssetId> result;

        DimensionFilterInstance filter = getDimensionFilter();

        if (filter == null) {
            LOG.debug("Unable to locate dimension filter. Not filtering assets.  Returning input list");
            result = toFilterList;
        } else {
            result = filter.filterAssets(toFilterList);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Filtered " + toFilterList + " using " + filter + " and got " + result);
            }
        }

        AssetId finalOutput = result == null || result.size() == 0 ? assetId : result.iterator().next();

        // register the result
        ics.SetVar(output + ":c", finalOutput.getType());
        ics.SetVar(output + ":cid", Long.toString(finalOutput.getId()));
        getJspContext().setAttribute(output, finalOutput);

        super.doTag();
        LOG.trace("gsf:translate-asset end");
    }
}
