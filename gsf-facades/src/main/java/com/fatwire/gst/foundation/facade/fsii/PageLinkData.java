/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.fsii;

import com.fatwire.assetapi.data.AssetId;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple bean containing navigation information like linktext, and url.
 * <p/>
 * Other convenience fields are included like the ID of the page and the subtype of the page asset.
 * <p/>
 * Finally, the immediateChildren property contains a list of immediate children of the current
 * page as stored in the SitePlanTree.  This makes this bean an ideal object for working with navigation
 * systems.
 *
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class PageLinkData
{
    private AssetId pageId;
    private String subtype;
    private String linktext;
    private String url;
    List<PageLinkData> immediateChildren;

    public AssetId getPageId()
    {
        return pageId;
    }

    public void setPageId(AssetId pageId)
    {
        this.pageId = pageId;
    }

    public String getSubtype()
    {
        return subtype;
    }

    public void setSubtype(String subtype)
    {
        this.subtype = subtype;
    }

    public String getLinktext()
    {
        return linktext;
    }

    public void setLinktext(String linktext)
    {
        this.linktext = linktext;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<PageLinkData> getImmediateChildren()
    {
        return immediateChildren;
    }

    public void setImmediateChildren(List<PageLinkData> immediateChildren)
    {
        this.immediateChildren = immediateChildren;
    }

    public void addImmediateChild(PageLinkData child)
    {
        if(this.immediateChildren == null)
        {
            this.immediateChildren = new ArrayList<PageLinkData>();
        }
        this.immediateChildren.add(child);
    }
}
