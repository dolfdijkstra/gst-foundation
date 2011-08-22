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
package com.fatwire.gst.foundation.wra;

import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * @since June 2011
 */
public class SqlVanityCoreFieldDao implements VanityCoreFieldDao {
    private final ICS ics;

    /**
     * @param ics
     */
    public SqlVanityCoreFieldDao(ICS ics) {
        super();
        this.ics = ics;
    }

    public VanityCoreAssetFields getCoreAssetFields(AssetIdWithSite id) {
        LogDep.logDep(ics, id.getAssetId());
        final PreparedStmt basicFields = new PreparedStmt(
                "SELECT id,name,description,subtype,status,path,template,startdate,enddate FROM " + id.getType()
                        + " WHERE id = ?", Collections.singletonList(id.getType()));
        basicFields.setElement(0, id.getType(), "id");

        final StatementParam param = basicFields.newParam();
        param.setLong(0, id.getId());
        final Row row = SqlHelper.selectSingle(ics, basicFields, param);

        final VanityCoreAssetFields coreFields = new VanityCoreAssetFields(id, row.getString("template"));
        coreFields.setName(row.getString("name"));
        coreFields.setDescription(row.getString("description"));
        coreFields.setSubtype(row.getString("subtype"));
        coreFields.setPath(row.getString("path"));

        coreFields.setStartDate(row.getDate("startdate"));
        coreFields.setEndDate(row.getDate("enddate"));
        return coreFields;
    }

}
