/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.fsii;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.TagRunnerRuntimeException;
import com.fatwire.gst.foundation.facade.runtag.asset.*;
import com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Miscellaneous utility library used to access data stored
 * in the site plan or assets related to the site plan.
 * <p/>
 * This module is aware of two special types of page subtypes: Aliases and Navigatin Placeholders.  Aliases are
 * pages that are aliases to other pages containing the same information in the site plan tree.  Navigation
 * Placeholders are pages that are typically located at the root of the site plan tree and do not contain any
 * content. They are not content assets but are organizational in nature.
 *
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class SitePlanUtils
{
    private static Log LOG = LogFactory.getLog(SitePlanUtils.class);

    private SitePlanUtils() {}

    /**
     * Return the name of the template assigned to the associated PageDetail asset.
     * Note that the asset type of the page detail is not fixed - any one will work.
     *
     * @param ics context
     * @param p id of page asset
     * @param assocname name of the association between the page and the page detail asset
     * @return
     */
    public static String getPageDetailTemplateForPage(ICS ics, String p, String assocname)
    {
        AssetId kid = Children.getSingleAssociation(ics, "Page", resolvePageAlias(ics, p), assocname);
        return AssetList.getRequiredSingleField(ics, kid.getType(), Long.toString(kid.getId()), "template");
    }

    /**
     * For a given page asset, return the template
     *
     * @param ics context
     * @param p page asset id
     * @return template field
     */
    public static String getTemplateForPage(ICS ics, String p)
    {
        return AssetList.getRequiredSingleField(ics, "Page", p, "template");
    }

    /**
     * Get the specified page detail asset id for the specified page.
     *
     * @param ics context
     * @param p page asset id
     * @param assocname name of the association containing the page detail.
     * @return
     */
    public static AssetId getPageDetailForPage(ICS ics, String p, String assocname)
    {
        return Children.getSingleAssociation(ics, "Page", resolvePageAlias(ics, p), assocname);
    }

    /**
     * Return a bean containing the common information found inside a HEAD html tag.
     * <p/>
     * Does not resolve aliases.
     *
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @param metaKeywordAttribute attribute used to contain meta keywords.  must be multi-valued
     * @param metaDescriptionAttribute attribute used to contain description. expects single-valued
     * @param titleAttribute attribute used to contain title.  expects single-valued.
     * @return HeadTagData object
     */
    public static HeadTagData getStandardHeadData(ICS ics, String c, String cid, String metaKeywordAttribute, String metaDescriptionAttribute, String titleAttribute)
    {
        HeadTagData result = new HeadTagData();

        AssetData data = AssetDataUtils.getAssetData(c, cid, metaKeywordAttribute, metaDescriptionAttribute, titleAttribute, "description", "name");
        result.setTitle(AttributeDataUtils.getWithFallback(data, titleAttribute, "description", "name"));
        result.setDescription(AttributeDataUtils.getWithFallback(data, metaDescriptionAttribute, titleAttribute, "description", "name"));
        String kw = AttributeDataUtils.getMultivaluedAsCommaSepString(data.getAttributeData(metaKeywordAttribute));
        if(kw == null || kw.length() == 0)
        {
            kw = AttributeDataUtils.getWithFallback(data, "name");
        }
        result.setKeywords(kw);
        return result;
    }

    /**
     * Get a PageLinkData object from the site plan tree.  If the linktext is derived from an associated asset,
     * specify the association name.  If the linktet is derived from the page asset itself, leave detailAssocName
     * as null.  Either way, the linktext comes from the appropriate asset's linktextAttr.
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very convenient to pass a navigation
     * placeholder into this function in order to return all children under a specific placeholder.
     * <p/>
     * Aliases are resolved.
     *
     * @param ics context
     * @param p page
     * @param assocname association name for pageDetail if applicable. May be null
     * @param linktextAttr attribute in associated pagedetail if specified, otherwise attribute in page asset
     * @param tname Template name to link to. Required if links are to work but can be omitted if links are not desired.
     * @param wrapper Wrapper SiteEntry name. Null is allowed.
     * @param withChildren populate the result with data for children as well
     * @return PageLinkData
     */
    public static PageLinkData getSitePlanAsLinks(ICS ics, final String p, String assocname, String linktextAttr, String tname, String wrapper, boolean withChildren)
    {
        PageLinkData pageLinkData = new PageLinkData();

        pageLinkData.setPageId(new AssetIdImpl("Page", Long.valueOf(p)));

        // set the subtype
        pageLinkData.setSubtype(GetSubtype.getSubtype(ics, pageLinkData.getPageId()));

        // Get the linktext
        final boolean isNavigationPlaceholder = PAGE_SUBTYPE_NAVIGATION_PLACEHOLDER.equals(pageLinkData.getSubtype());
        if(assocname == null)
        {
            AssetData data = AssetDataUtils.getAssetData("Page", resolvePageAlias(ics, p), linktextAttr);
            pageLinkData.setLinktext(AttributeDataUtils.getWithFallback(data, linktextAttr));
        }
        else
        {
            LOG.debug("About to load " + assocname + " for Page:" + p);
            try
            {
                AssetId kid = Children.getSingleAssociation(ics, "Page", resolvePageAlias(ics, p), assocname);
                AssetData data = AssetDataUtils.getAssetData(kid, linktextAttr);
                pageLinkData.setLinktext(AttributeDataUtils.getWithFallback(data, linktextAttr));
            }
            catch(TagRunnerRuntimeException e)
            {
                if(e.getErrno() == -111)
                {
                    // nav placeholders get some slack.
                    if (!isNavigationPlaceholder)
                    {
                        throw new CSRuntimeException("Missing required association: " + assocname + " for Page:" + resolvePageAlias(ics, p), -111);
                    }

                }
                else
                {
                    throw e;
                }
            }

        }

        // Do not populate links for Navigation Placeholders.
        if(!isNavigationPlaceholder && tname != null)
        {
            // Get the link
            if(tname != null)
            {
                // do the link
                GetTemplateUrl gtu = new GetTemplateUrl(ics, "Page", p, tname, wrapper, "SitePlanUtils");
                gtu.setArgument("p", p); // should this include the alias target?
                ics.RemoveVar("gspal-url");
                gtu.setOutstr("gspal-url");
                gtu.execute(ics);
                pageLinkData.setUrl(ics.GetVar("gspal-url"));
                ics.RemoveVar("gspal-url");
            }
        }

        // Get the children
        if(withChildren)
        {
            for(AssetId kid : ListPages.getChildPages(ics, pageLinkData.getPageId().getId()))
            {
                // recurse
                PageLinkData kidData = getSitePlanAsLinks(ics, Long.toString(kid.getId()), assocname, linktextAttr, tname, wrapper, withChildren);
                pageLinkData.addImmediateChild(kidData);
            }
        }

        return pageLinkData;
    }

    /**
     * Name of the page subtype indicating that this page is NOT rendered on the site but
     * is instead merely used to group navigation components on the site.
     */
    public static final String PAGE_SUBTYPE_NAVIGATION_PLACEHOLDER = "NavigationPlaceholder";

    /**
     * Name of the page subtype indicating that this page is
     * just an alias to another page.
     */
    public static final String PAGE_SUBTYPE_ALIAS = "Alias";

    /**
     * Name of the association in an alias page that contains the
     * target page.
     */
    public static final String PAGE_SUBTYPE_ASSOCIATION_NAME = "TargetPage";

    /**
     * Given an input page, find out if it is an alias or not, and if it is,
     * return the ID of the target page.  If not an alias, the original
     * input is returned
     *
     * @param ics context
     * @param p input page id
     * @return resolved alias page id
     */
    public static String resolvePageAlias(ICS ics, final String p)
    {
        String subtype = GetSubtype.getSubtype(ics, "Page", p);
        if(PAGE_SUBTYPE_ALIAS.equals(subtype))
        {
            AssetId target = Children.getSingleAssociation(ics, "Page", p, PAGE_SUBTYPE_ASSOCIATION_NAME);
            return Long.toString(target.getId());
        }
        return p;
    }
}
