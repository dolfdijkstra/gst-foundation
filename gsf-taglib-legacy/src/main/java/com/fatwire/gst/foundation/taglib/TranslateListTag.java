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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * simple tag for translating an asset that's in the form of an ASSETID,ASSETTYPE IList.
 *
 * @author Tony Field
 * @since 11-09-20
 * 
 * 
 * @deprecated as of release 12.x
 * 
 */
public final class TranslateListTag extends MultilingualGsfSimpleTag {

    private String inlist = null;
    private String outlist = null;

    public void setInlist(String s) {
        this.inlist = s;
    }

    public void setOutlist(String s) {
        outlist = s;
    }

    public void doTag() throws JspException, IOException {
        LOG.trace("gsf:translate-list start");

        final ICS ics = getICS();

        List<AssetId> toFilterList = _getInputList();
        String outputListName = _getOutputListName();

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

        // register the IList in ICS
        IList resultIList = new AssetIdIList(outputListName, result);
        ics.RegisterList(outputListName, resultIList);
        // bonus!
        getJspContext().setAttribute(outputListName, result);

        super.doTag();
        LOG.trace("gsf:translate-list end");
    }

    private List<AssetId> _getInputList() throws JspException {
        if (inlist == null || inlist.length() == 0)
            throw new JspException("No inlist specified in gsf:translate-list tag");
        IList in = getICS().GetList(inlist);
        List<AssetId> result = new ArrayList<AssetId>();
        for (Row row : new IListIterable(in)) {
            AssetId id = AssetIdUtils.createAssetId(row.getString("assettype"), row.getString("assetid"));
            result.add(id);
        }
        if (result.size() == 0) {
            LOG.debug("Input list does not contain any items in gsf:translate-list tag");
        }
        return result;
    }

    private String _getOutputListName() {
        if (outlist == null) return inlist;
        return outlist;
    }
}
