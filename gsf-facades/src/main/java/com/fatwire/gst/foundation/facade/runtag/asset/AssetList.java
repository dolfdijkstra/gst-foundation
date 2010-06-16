/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Exposes ASSET.LIST
 * <asset:list
 * type="assetType"
 * list="nameOfList"
 * [order="order"]
 * [pubid="siteId"]
 * [excludevoided="trueOrFalse"]
 * [field[n]="fieldName"]
 * [value[n]="fieldValue"]>
 * <asset:argument name="fieldName" value="fieldValue"/>
 * </asset:list>
 *
 * @author Tony Field
 * @since Sep 28, 2008
 */
public class AssetList extends AbstractTagRunner
{
    public AssetList()
    {
        super("ASSET.LIST");
    }

    public void setType(String type) { this.set("TYPE", type); }

    public void setList(String list) { this.set("LIST", list); }

    public void setOrder(String order) { this.set("ORDER", order); }

    public void setPubid(String pubid) { this.set("PUBID", pubid); }

    public void setExcludeVoided(boolean b) { this.set("EXCLUDEVOIDED", b ? "TRUE" : "FALSE"); }

    private int index = 1;

    public void setField(String name, String value)
    {
        this.set("FIELD" + index, name);
        this.set("VALUE" + index, value);
        index++;
    }

    /**
     * Return true if the asset exists in the database, and false if it does not.
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @return true if the asset exists, false if it does not
     */
    public static boolean assetExists(ICS ics, String c, String cid)
    {
        if (c == null || c.length() == 0) return false;
        if (cid == null || cid.length() == 0) return false;
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("id", cid);
        al.setType(c);
        al.setExcludeVoided(false);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        return spugtfa != null && spugtfa.hasData();
    }

    /**
     * Get a single field from a specified asset.  Only one result must be returned from this search
     * or an exception will be thrown.  Pubid is optional.
     *
     * @param ics
     * @param c
     * @param cid
     * @param field
     * @return single field value
     */
    public static String getRequiredSingleField(ICS ics, String c, String cid, String field)
    {
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("id", cid);
        al.setType(c);
        al.setExcludeVoided(true);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        if(spugtfa != null && spugtfa.hasData())
        {
            if(spugtfa.numRows() > 1)
            {
                throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": multiple matches found:" + spugtfa.numRows());
            }
            spugtfa.moveTo(1);
            String ret = IListUtils.getStringValue(spugtfa, field);
            if(ret == null || ret.length() == 0)
            {
                throw new IllegalStateException("No " + field + " found for asset " + c + ":" + cid);
            }
            return ret;
        }
        else
        {
            throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": no data found.");
        }
    }

    public static IList listSingleAsset(ICS ics, String c, String cid)
    {
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("id", cid);
        al.setType(c);
        al.setExcludeVoided(true);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        if(spugtfa != null && spugtfa.hasData())
        {
            if(spugtfa.numRows() > 1)
            {
                throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": multiple matches found:" + spugtfa.numRows());
            }
            return new IterableIListWrapper(spugtfa).iterator().next();
        }
        else
        {
            throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": no data found.");
        }
    }

}
