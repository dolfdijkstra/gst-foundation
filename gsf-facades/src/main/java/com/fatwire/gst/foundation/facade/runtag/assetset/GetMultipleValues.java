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

package com.fatwire.gst.foundation.facade.runtag.assetset;

import java.util.List;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.fatwire.gst.foundation.facade.runtag.listobject.AddRow;
import com.fatwire.gst.foundation.facade.runtag.listobject.Create;
import com.fatwire.gst.foundation.facade.runtag.listobject.ToList;

/**
 * <assetset:getmultiplevalues name="myassetset" prefix="ValueList"
 * list="listout" immediateonly="false" byasset="true"/>
 * 
 * @author Tony Field
 * @since Oct 24, 2008
 */
public final class GetMultipleValues extends AbstractTagRunner {
    public GetMultipleValues() {
        super("ASSETSET.GETMULTIPLEVALUES");
    }

    public void setName(String name) {
        set("NAME", name);
    }

    public void setPrefix(String prefix) {
        set("PREFIX", prefix);
    }

    public void setImmediateOnly(boolean b) {
        set("IMMEDIATEONLY", b ? "true" : "false");
    }

    public void setByAsset(boolean b) {
        set("BYASSET", b ? "true" : "false");
    }

    public void setList(String inputSortListName) {
        set("LIST", inputSortListName);
    }

    public static void getMultipleValues(ICS ics, AssetId id, String prefix, String attrType, List<String> columns) {
        getMultipleValues(ics, id, null, null, false, false, prefix, attrType, columns);
    }

    public static void getMultipleValues(ICS ics, AssetId id, String deptype, String locale, boolean byAsset,
            boolean immediateOnly, String prefix, String attrType, List<String> columns) {
        // create asset set
        SetAsset setAsset = new SetAsset();
        final String assetSetName = "__AssetSet" + ics.genID(true);
        setAsset.setName(assetSetName);
        setAsset.setType(id.getType());
        setAsset.setId(Long.toString(id.getId()));
        if (deptype != null) {
            setAsset.setDeptype(deptype);
        }
        if (locale != null) {
            setAsset.setLocale(locale);
        }
        setAsset.execute(ics);

        // sort list cols first
        Create createSortList = new Create();
        final String sortListObjectName = "__SortList" + ics.genID(true);
        createSortList.setName(sortListObjectName);
        createSortList.setColumns("attributetypename,attributename,direction");
        createSortList.execute(ics);

        // add rows to sort list
        for (String col : columns) {
            AddRow addRow = new AddRow();
            addRow.setName(sortListObjectName);
            addRow.setColumnValue("attributetypename", attrType);
            addRow.setColumnValue("attributename", col);
            addRow.setColumnValue("direction", "ascending");
            addRow.execute(ics);
        }

        final String sortListIListName = "__sortListIList" + ics.genID(true);
        ToList toList = new ToList();
        toList.setName(sortListObjectName);
        toList.setListVarName(sortListIListName);
        toList.execute(ics);

        // get values
        GetMultipleValues gmv = new GetMultipleValues();
        gmv.setName(assetSetName);
        gmv.setByAsset(byAsset);
        gmv.setImmediateOnly(immediateOnly);
        gmv.setList(sortListIListName);
        gmv.setPrefix(prefix);
        gmv.execute(ics);
    }

}
