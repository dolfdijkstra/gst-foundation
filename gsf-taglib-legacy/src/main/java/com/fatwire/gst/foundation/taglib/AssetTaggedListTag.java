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

import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;
import com.fatwire.gst.foundation.tagging.AssetTaggingService;
import com.fatwire.gst.foundation.tagging.TagUtils;
import com.fatwire.gst.foundation.tagging.db.TableTaggingServiceImpl;

/**
 * Tagged list tag support This tag uses ICS.SQL(PreparedStmt, boolean) to query
 * the GSTTagRegistry and retrieve the assets that point to the specified tag.
 * Input tagname - the name of the tag outlist - name of output list Output The
 * name of an IList object to be placed in the list pool. It contains two
 * columns: ASSETTYPE, ASSETID. Null is never returned, but the returned list
 * can be empty. A java method is provided in order for the same logic to be
 * called from java. *
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Aug 13, 2010
 * 
 * 
 * @deprecated as of release 12.x
 * 
 */
public final class AssetTaggedListTag extends GsfSimpleTag {

    private String assettype = null;
    private String assetid = null;
    private String outlist = null;

    public AssetTaggedListTag() {

    }

    public void setAssettype(final String assettype) {
        this.assettype = assettype;
    }

    public void setAssetid(final String assetid) {
        this.assetid = assetid;
    }

    public void setOutlist(final String outlist) {
        this.outlist = outlist;
    }

    public void release() {

        assettype = null;
        assetid = null;
        outlist = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        final ICS ics = getICS();
        final AssetTaggingService svc = new TableTaggingServiceImpl(ics);
        final Collection<AssetId> ids = svc.lookupTaggedAssets(TagUtils.asTag("asset-" + assetid + ":" + assettype));
        ics.RegisterList(outlist, new AssetIdIList(outlist, ids));
        getJspContext().setAttribute(outlist, ids);
        super.doTag();
    }

}
