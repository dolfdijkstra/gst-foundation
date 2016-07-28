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

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import tools.gsf.facade.sql.IListUtils;

/**
 * {@literal Exposes ASSET.LIST &lt;asset:list type="assetType" list="nameOfList"
 * [order="order"] [pubid="siteId"] [excludevoided="trueOrFalse"]
 * [field[n]="fieldName"] [value[n]="fieldValue"]> <asset:argument
 * name="fieldName" value="fieldValue"/> </asset:list>;}
 *
 * @author Tony Field
 * @since Sep 28, 2008
 */
public class AssetList extends AbstractTagRunner {
    public AssetList() {
        super("ASSET.LIST");
    }

    public void setType(String type) {
        this.set("TYPE", type);
    }

    public void setList(String list) {
        this.set("LIST", list);
    }

    public void setOrder(String order) {
        this.set("ORDER", order);
    }

    public void setPubid(String pubid) {
        this.set("PUBID", pubid);
    }

    public void setExcludeVoided(boolean b) {
        this.set("EXCLUDEVOIDED", b ? "TRUE" : "FALSE");
    }

    private int index = 1;

    public void setField(String name, String value) {
        this.set("FIELD" + index, name);
        this.set("VALUE" + index, value);
        index++;
    }

    /**
     * Return true if the asset exists in the database, and false if it does
     * not.
     *
     * @param ics context
     * @param c asset type
     * @param cid asset id
     * @return true if the asset exists, false if it does not
     */
    public static boolean assetExists(ICS ics, String c, String cid) {
        if (c == null || c.length() == 0)
            return false;
        if (cid == null || cid.length() == 0)
            return false;
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

    public static boolean assetExistsByName(ICS ics, String c, String name) {
        if (c == null || c.length() == 0) return false;

        if (!SqlHelper.tableExists(ics, c)) return false;

        if (name == null || name.length() == 0) return false;
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("name", name);
        al.setType(c);
        al.setExcludeVoided(false);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        return spugtfa != null && spugtfa.hasData();
    }

    /**
     * Look up asset id by name
     * @param ics context
     * @param c type
     * @param name name field value
     * @return asset id or null if not found.
     */
    public static AssetId lookupAssetId(ICS ics, String c, String name) {
        if (c == null || c.length() == 0) throw new IllegalArgumentException("No such asset type: " + c);

        if (!SqlHelper.tableExists(ics, c)) throw new IllegalArgumentException("No such asset type: " + c);

        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Cannot look up asset by name with an actual name.  Type: " + c);
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("name", name);
        al.setType(c);
        al.setExcludeVoided(false);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        if (spugtfa != null && spugtfa.hasData()) {
            for (Row listRow : new IListIterable(spugtfa)) {
                return AssetIdUtils.createAssetId(c, listRow.getString("id"));
            }
        }
        return null;
    }

    /**
     * Get a single field from a specified asset. Only one result must be
     * returned from this search or an exception will be thrown. Pubid is
     * optional.
     *
     * @param ics Content Server context object
     * @param c current asset
     * @param cid content id
     * @param field field to get
     * @return single field value
     */
    public static String getRequiredSingleField(ICS ics, String c, String cid, String field) {
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("id", cid);
        al.setType(c);
        al.setExcludeVoided(true);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        if (spugtfa != null && spugtfa.hasData()) {
            if (spugtfa.numRows() > 1) {
                throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": multiple matches found:"
                        + spugtfa.numRows());
            }
            spugtfa.moveTo(1);
            String ret = IListUtils.getStringValue(spugtfa, field);
            if (ret == null || ret.length() == 0) {
                throw new IllegalStateException("No " + field + " found for asset " + c + ":" + cid);
            }
            return ret;
        } else {
            throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": no data found.");
        }
    }

    public static IList listSingleAsset(ICS ics, String c, String cid) {
        ics.RegisterList("spu-gtfa", null);
        AssetList al = new AssetList();
        al.setField("id", cid);
        al.setType(c);
        al.setExcludeVoided(true);
        al.setList("spu-gtfa");
        al.execute(ics);
        IList spugtfa = ics.GetList("spu-gtfa");
        ics.RegisterList("spu-gtfa", null);
        if (spugtfa != null && spugtfa.hasData()) {
            if (spugtfa.numRows() > 1) {
                throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": multiple matches found:"
                        + spugtfa.numRows());
            }
            return new IterableIListWrapper(spugtfa).iterator().next();
        } else {
            throw new IllegalStateException("ASSET.LIST failed for " + c + ":" + cid + ": no data found.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner#handleError
     * (COM.FutureTense.Interfaces.ICS)
     */
    @Override
    protected void handleError(ICS ics) {
        if (ics.GetErrno() == -101)
            return;
        super.handleError(ics);
    }

}
