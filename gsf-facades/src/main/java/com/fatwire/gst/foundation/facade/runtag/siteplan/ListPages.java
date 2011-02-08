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

package com.fatwire.gst.foundation.facade.runtag.siteplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadById;
import com.fatwire.gst.foundation.facade.runtag.asset.GetSiteNode;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * <SITEPLAN.LISTPAGES NAME="thePubNode" PLACEDLIST="placedPages" LEVEL="1"/>
 * 
 * @author Tony Field
 * @since Jul 14, 2009
 */
public final class ListPages extends AbstractTagRunner {
    public ListPages() {
        super("SITEPLAN.LISTPAGES");
    }

    public void setName(String name) {
        set("NAME", name);
    }

    public void setPlacedList(String placedPages) {
        set("PLACEDLIST", placedPages);
    }

    public void setLevel(int level) {
        set("LEVEL", Integer.toString(level));
    }

    /**
     * Return the immediate children of the specified page in the site plan
     * tree. If the page is not present in the site plan tree, an exception is
     * thrown. If no child pages are found an empty list is returned.
     * 
     * @param ics ICS context
     * @param p ID of the the page whose children will be looked up
     * @return list of children of the input page, never null
     */
    public static List<AssetId> getChildPages(ICS ics, long p) {
        final String LOADED_PAGE_NAME = "__thePage";
        final String LOADED_SITE_PLAN_NODE = "__siteplan";
        final String CURRENT_PAGE_NODE_ID = "__nodeId";
        final String PLACED_LIST = "__placedList";

        try {
            AssetLoadById assetLoad = new AssetLoadById();
            assetLoad.setAssetId(p);
            assetLoad.setAssetType("Page");
            assetLoad.setName(LOADED_PAGE_NAME);
            assetLoad.execute(ics);
            if (ics.GetErrno() < 0) {
                throw new CSRuntimeException("Failed to load page identified by Page:" + p, ics.GetErrno());
            }

            GetSiteNode getSiteNode = new GetSiteNode();
            getSiteNode.setName(LOADED_PAGE_NAME);
            getSiteNode.setOutput(CURRENT_PAGE_NODE_ID);
            getSiteNode.execute(ics);
            if (ics.GetErrno() < 0) {
                throw new CSRuntimeException("Could not get site node for page identified by Page:" + p, ics.GetErrno());
            }

            SitePlanLoad sitePlanLoad = new SitePlanLoad();
            sitePlanLoad.setName(LOADED_SITE_PLAN_NODE);
            sitePlanLoad.setNodeid(ics.GetVar(CURRENT_PAGE_NODE_ID));
            sitePlanLoad.execute(ics);
            if (ics.GetErrno() < 0) {
                throw new CSRuntimeException("Could not load site plan tree for page identified by Page:" + p,
                        ics.GetErrno());
            }

            ListPages listPages = new ListPages();
            listPages.setName(LOADED_SITE_PLAN_NODE);
            listPages.setPlacedList(PLACED_LIST);
            listPages.setLevel(1);
            listPages.execute(ics);
            if (ics.GetErrno() < 0) {
                throw new CSRuntimeException("Could not get list child pages for Page:" + p, ics.GetErrno());
            }

            // otype/oid are what we care about
            IList placedList = ics.GetList(PLACED_LIST);
            if (ics.GetErrno() < 0 || placedList == null || !placedList.hasData()) {
                ics.ClearErrno();
                return Collections.emptyList();
            }
            List<AssetId> list = new ArrayList<AssetId>();
            for (IList row : new IterableIListWrapper(placedList)) {
                AssetId id = new AssetIdImpl(IListUtils.getStringValue(row, "AssetType"), IListUtils.getLongValue(row,
                        "Id"));
                list.add(id);
            }
            return list;
        } finally {
            ics.SetObj(LOADED_PAGE_NAME, null); // just to be safe
            ics.RemoveVar(CURRENT_PAGE_NODE_ID);
            ics.SetObj(LOADED_SITE_PLAN_NODE, null);
            ics.RegisterList(PLACED_LIST, null);
        }
    }
}
