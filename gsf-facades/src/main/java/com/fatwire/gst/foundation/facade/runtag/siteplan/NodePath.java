/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.siteplan;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadById;
import com.fatwire.gst.foundation.facade.runtag.asset.GetSiteNode;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * <siteplan:nodepath
 * name="node"
 * list="list name"/>
 *
 * @author Tony Field
 * @since Sep 28, 2008
 */
public class NodePath extends AbstractTagRunner
{
    public NodePath() { super("SITEPLAN.NODEPATH"); }

    public void setName(String name) { set("NAME", name); }

    public void setList(String listName) { set("LIST", listName); }

    /**
     * Compute and return the nodepath for the specified input asset.
     * The returned list is in the same order and format as that returned
     * by the siteplan.nodepath tag.
     *
     * @param ics context
     * @param p page asset ID
     * @return nodepath IList.  Includes publication at root.
     */
    public static IList getNodePathForPage(ICS ics, String p)
    {
        return getNodePathForPage(ics, Long.valueOf(p));
    }

    /**
     * Compute and return the nodepath for the specified input asset.
     * The returned list is in the same order and format as that returned
     * by the siteplan.nodepath tag.
     *
     * @param ics context
     * @param page page asset ID
     * @return nodepath IList.  Includes publication at root.
     */
    public static IList getNodePathForPage(ICS ics, AssetId page)
    {
        if("Page".equals(page.getType()))
        {
            return getNodePathForPage(ics, page.getId());
        }
        else
        {
            throw new CSRuntimeException("Input asset ID is not a page: " + page, ftErrors.badparams);
        }
    }

    /**
     * Get the parent page in the site plan tree of the specified page.  If the page cannot be located in the site
     * plan tree, an exception is thrown.  If the page has no parents in the site plan tree, an exception is thrown.
     *
     * @param ics ICS context
     * @param p ID of page asset to inspect
     * @return ID of parent page, never null.
     */
    public static AssetId getParentPage(ICS ics, long p)
    {
        if(p < 1L)
        {
            throw new IllegalArgumentException("Invalid page id specified: " + p);
        }
        if(ics == null)
        {
            throw new IllegalArgumentException("Null ICS not allowed");
        }
        final String LOADED_PAGE_NAME = "__thePage";
        final String LOADED_SITE_PLAN_NODE = "__siteplan";
        final String CURRENT_PAGE_NODE_ID = "__nodeId";
        final String ANCESTOR_LIST = "__ancestorList";

        try
        {
            AssetLoadById assetLoad = new AssetLoadById();
            assetLoad.setAssetId(p);
            assetLoad.setAssetType("Page");
            assetLoad.setName(LOADED_PAGE_NAME);
            assetLoad.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Failed to load page identified by Page:" + p, ics.GetErrno());
            }

            GetSiteNode getSiteNode = new GetSiteNode();
            getSiteNode.setName(LOADED_PAGE_NAME);
            getSiteNode.setOutput(CURRENT_PAGE_NODE_ID);
            getSiteNode.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not get site node for page identified by Page:" + p, ics.GetErrno());
            }

            SitePlanLoad sitePlanLoad = new SitePlanLoad();
            sitePlanLoad.setName(LOADED_SITE_PLAN_NODE);
            sitePlanLoad.setNodeid(ics.GetVar(CURRENT_PAGE_NODE_ID));
            sitePlanLoad.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not load site plan tree for page identified by Page:" + p, ics.GetErrno());
            }

            NodePath nodePath = new NodePath();
            nodePath.setName(LOADED_SITE_PLAN_NODE);
            nodePath.setList(ANCESTOR_LIST);
            nodePath.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not get node path for page identified by Page:" + p, ics.GetErrno());
            }

            // otype/oid are what we care about
            IList ancestorList = ics.GetList(ANCESTOR_LIST);
            if(ancestorList == null || !ancestorList.hasData())
            {
                throw new CSRuntimeException("No ancestors found in site plan tree for page identified by Page:" + p, ics.GetErrno());
            }
            ancestorList.moveTo(1);

            return new AssetIdImpl(IListUtils.getStringValue(ancestorList, "otype"), IListUtils.getLongValue(ancestorList, "oid"));

        }
        finally
        {
            ics.SetObj(LOADED_PAGE_NAME, null); // just to be safe
            ics.RemoveVar(CURRENT_PAGE_NODE_ID);
            ics.SetObj(LOADED_SITE_PLAN_NODE, null);
            ics.RegisterList(ANCESTOR_LIST, null);
        }
    }


    /**
     * Compute and return the nodepath for the specified input asset.
     * The returned list is in the same order and format as that returned
     * by the siteplan.nodepath tag.
     *
     * @param ics context
     * @param p page asset ID
     * @return nodepath IList.  Includes publication at root.
     */
    public static IList getNodePathForPage(ICS ics, long p)
    {
        final String LOADED_PAGE_NAME = "__thePage";
        final String LOADED_SITE_PLAN_TREE = "__siteplan";
        final String CURRENT_PAGE_NODE = "__nodeId";
        final String ANCESTOR_LIST = "__ancestorList";

        try
        {
            AssetLoadById assetLoad = new AssetLoadById();
            assetLoad.setAssetId(p);
            assetLoad.setAssetType("Page");
            assetLoad.setName(LOADED_PAGE_NAME);
            assetLoad.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Failed to load page identified by Page:" + p, ics.GetErrno());
            }

            GetSiteNode getSiteNode = new GetSiteNode();
            getSiteNode.setName(LOADED_PAGE_NAME);
            getSiteNode.setOutput(CURRENT_PAGE_NODE);
            getSiteNode.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not get site node for page identified by Page:" + p, ics.GetErrno());
            }

            SitePlanLoad sitePlanLoad = new SitePlanLoad();
            sitePlanLoad.setName(LOADED_SITE_PLAN_TREE);
            sitePlanLoad.setNodeid(ics.GetVar(CURRENT_PAGE_NODE));
            sitePlanLoad.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not load site plan tree for page identified by Page:" + p, ics.GetErrno());
            }

            NodePath nodePath = new NodePath();
            nodePath.setName(LOADED_SITE_PLAN_TREE);
            nodePath.setList(ANCESTOR_LIST);
            nodePath.execute(ics);
            if(ics.GetErrno() < 0)
            {
                throw new CSRuntimeException("Could not get node path for page identified by Page:" + p, ics.GetErrno());
            }

            // otype/oid are what we care about
            IList ancestorList = ics.GetList(ANCESTOR_LIST);
            if(ancestorList == null || !ancestorList.hasData())
            {
                throw new CSRuntimeException("No ancestors found in site plan tree for page identified by Page:" + p, ics.GetErrno());
            }

            return ancestorList;

        }
        finally
        {
            ics.SetObj(LOADED_PAGE_NAME, null); // just to be safe
            ics.RemoveVar(CURRENT_PAGE_NODE);
            ics.SetObj(LOADED_SITE_PLAN_TREE, null);
            ics.RegisterList(ANCESTOR_LIST, null);
        }
    }
}