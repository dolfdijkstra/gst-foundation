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

package com.fatwire.gst.foundation.facade.fsii;

import java.util.ArrayList;
import java.util.List;

import com.fatwire.assetapi.data.AssetId;

/**
 * Simple bean containing navigation information like linktext, and url.
 * <p/>
 * Other convenience fields are included like the ID of the page and the subtype
 * of the page asset.
 * <p/>
 * Finally, the immediateChildren property contains a list of immediate children
 * of the current page as stored in the SitePlanTree. This makes this bean an
 * ideal object for working with navigation systems.
 * 
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class PageLinkData {
    private AssetId pageId;
    private String subtype;
    private String linktext;
    private String url;
    List<PageLinkData> immediateChildren;

    public AssetId getPageId() {
        return pageId;
    }

    public void setPageId(AssetId pageId) {
        this.pageId = pageId;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getLinktext() {
        return linktext;
    }

    public void setLinktext(String linktext) {
        this.linktext = linktext;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PageLinkData> getImmediateChildren() {
        return immediateChildren;
    }

    public void setImmediateChildren(List<PageLinkData> immediateChildren) {
        this.immediateChildren = immediateChildren;
    }

    public void addImmediateChild(PageLinkData child) {
        if (this.immediateChildren == null) {
            this.immediateChildren = new ArrayList<PageLinkData>();
        }
        this.immediateChildren.add(child);
    }
}
