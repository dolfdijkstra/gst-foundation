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

package com.fatwire.gst.foundation.facade.runtag.asset;

import java.util.ArrayList;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * AssetChildren
 * <p>
 * <ASSET.CHILDREN NAME="assetName" [TYPE="parent assettype"]
 * [ASSETID="parent assetid"] LIST="listName" [CODE="NameOfAssociation"]
 * [OBJECTTYPE="typeOfObject"] [OBJECTID="objectID"] [ORDER="nrank"]/>
 * 
 * @author Tony Field
 * @since Sep 28, 2008
 */
public class Children extends AbstractTagRunner {
    public Children() {
        super("ASSET.CHILDREN");
    }

    public void setName(String name) {
        set("NAME", name);
    }

    public void setType(String type) {
        set("TYPE", type);
    }

    public void setAssetId(String parentId) {
        set("ASSETID", parentId);
    }

    public void setList(String list) {
        set("LIST", list);
    }

    public void setCode(String code) {
        set("CODE", code);
    }

    public void setObjectType(String otype) {
        set("OBJECTTYPE", otype);
    }

    public void setObjectId(String oid) {
        set("OBJECTID", oid);
    }

    public void setOrder(String order) {
        set("ORDER", order);
    }

    @Override
    protected void handleError(ICS ics) {
        if (ics.GetErrno() == -111)
            return;
        super.handleError(ics);
    }

    /**
     * Look up the single valued named association for a specified asset. If no
     * associated asset is found an exception is thrown.
     * 
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @param code association name
     * @return id of the associated asset
     */
    public static AssetId getSingleAssociation(ICS ics, String c, String cid, String code) {
        ics.RegisterList("spu-kids", null);
        Children ac = new Children();
        ac.setType(c);
        ac.setAssetId(cid);
        ac.setCode(code);
        ac.setList("spu-kids");
        ac.execute(ics);
        IList spukids = ics.GetList("spu-kids");
        ics.RegisterList("spu-kids", null);
        if (spukids != null && spukids.hasData()) {
            if (spukids.numRows() > 1) {
                throw new IllegalStateException("Too many kids found associated to: " + c + ":" + cid
                        + " for association:" + code + ".  expected 1 but got " + spukids.numRows());
            }
            spukids.moveTo(1);
            return new AssetIdImpl(IListUtils.getStringValue(spukids, "otype"), IListUtils.getLongValue(spukids, "oid"));
        } else {
            throw new IllegalStateException("No kids found associated to: " + c + ":" + cid + " for association:"
                    + code + ".  expected 1 but got none.");
        }
    }

    /**
     * Look up the single valued named association for a specified asset. If no
     * associated asset is found null is returned
     * 
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @param code association name
     * @return id of the associated asset
     */
    public static AssetId getOptionalSingleAssociation(ICS ics, String c, String cid, String code) {
        ics.RegisterList("spu-kids", null);
        Children ac = new Children();
        ac.setType(c);
        ac.setAssetId(cid);
        ac.setCode(code);
        ac.setList("spu-kids");
        ac.execute(ics);
        IList spukids = ics.GetList("spu-kids");
        ics.RegisterList("spu-kids", null);
        if (spukids != null && spukids.hasData()) {
            if (spukids.numRows() > 1) {
                throw new IllegalStateException("Too many kids found associated to: " + c + ":" + cid
                        + " for association:" + code + ".  expected 0 or 1 but got " + spukids.numRows());
            }
            spukids.moveTo(1);
            return new AssetIdImpl(IListUtils.getStringValue(spukids, "otype"), IListUtils.getLongValue(spukids, "oid"));
        } else {
            return null;
        }
    }

    /**
     * Look up the multi-valued named association for a specified asset. If no
     * associated asset is found an empty list is returned.
     * 
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @param code association name
     * @return ids of the associated asset
     */
    public static List<AssetId> getOptionalMultivaluedAssociation(ICS ics, String c, String cid, String code) {
        ics.RegisterList("spu-kids", null);
        Children ac = new Children();
        ac.setType(c);
        ac.setAssetId(cid);
        ac.setCode(code);
        ac.setList("spu-kids");
        ac.execute(ics);
        IList spukids = ics.GetList("spu-kids");
        ics.RegisterList("spu-kids", null);
        List<AssetId> result = new ArrayList<AssetId>();
        if (spukids != null && spukids.hasData()) {
            for (IList row : new IterableIListWrapper(spukids)) {
                result.add(new AssetIdImpl(IListUtils.getStringValue(row, "otype"), IListUtils.getLongValue(row, "oid")));

            }
        }
        return result;
    }

    /**
     * Look up the multi-valued named association for a specified asset. If no
     * associated asset is found an exception is thrown.
     * 
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @param code association name
     * @return ids of the associated asset
     */
    public static List<AssetId> getMultivaluedAssociation(ICS ics, String c, String cid, String code) {
        List<AssetId> result = getOptionalMultivaluedAssociation(ics, c, cid, code);
        if (result.size() == 0) {
            throw new IllegalStateException("No kids found associated to: " + c + ":" + cid + " for association:"
                    + code + ".  expected at least one but got none.");
        }
        return result;
    }
}
