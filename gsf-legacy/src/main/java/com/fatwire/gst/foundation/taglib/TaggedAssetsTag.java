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

import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.tagging.AssetTaggingService;
import com.fatwire.gst.foundation.tagging.TagUtils;
import com.fatwire.gst.foundation.tagging.db.TableTaggingServiceImpl;

/**
 * Tagged list tag support This JSP tag retrieves a list of assets that are
 * 'tagged' with the same name.
 * 
 * @author Dolf Dijkstra
 * @since Feb 14, 2011
 * @see AssetTaggingService
 * 
 * @deprecated as of release 12.x
 * 
 */
public final class TaggedAssetsTag extends GsfSimpleTag {
    private String tag = null;
    private String list = null;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        final ICS ics = getICS();
        final AssetTaggingService svc = new TableTaggingServiceImpl(ics);
        getJspContext().setAttribute(list, svc.lookupTaggedAssets(TagUtils.asTag(tag)));
        super.doTag();
    }

    /**
     * @param list the list to set
     */
    public void setList(final String list) {
        this.list = list;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

}
