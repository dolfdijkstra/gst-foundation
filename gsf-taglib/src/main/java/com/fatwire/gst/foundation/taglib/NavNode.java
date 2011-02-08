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

import java.util.LinkedList;
import java.util.List;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;

public class NavNode {
    private AssetId page;
    private int level;
    private String pagesubtype;
    private String pagename;
    private AssetId id;
    private WebReferenceableAsset wra;
    private String url;
    private String linktext;
    private List<NavNode> children = new LinkedList<NavNode>();

    public void addChild(NavNode child) {
        if (child != null)
            children.add(child);
    }

    /**
     * @return the page
     */
    public AssetId getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(AssetId page) {
        this.page = page;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the pagesubtype
     */
    public String getPagesubtype() {
        return pagesubtype;
    }

    /**
     * @param pagesubtype the pagesubtype to set
     */
    public void setPagesubtype(String pagesubtype) {
        this.pagesubtype = pagesubtype;
    }

    /**
     * @return the pagename
     */
    public String getPagename() {
        return pagename;
    }

    /**
     * @param pagename the pagename to set
     */
    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    /**
     * @return the id
     */
    public AssetId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(AssetId id) {
        this.id = id;
    }

    /**
     * @return the children
     */
    public List<NavNode> getChildren() {
        return children;
    }

    /**
     * @return the wra
     */
    public WebReferenceableAsset getWra() {
        return wra;
    }

    /**
     * @param wra the wra to set
     */
    public void setWra(WebReferenceableAsset wra) {
        this.wra = wra;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setLinktext(String linktext) {
        this.linktext = linktext;
    }

    public String getLinktext() {
        return linktext;
    }

}
