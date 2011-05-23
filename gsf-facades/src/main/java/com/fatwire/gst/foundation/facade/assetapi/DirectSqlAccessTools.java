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

package com.fatwire.gst.foundation.facade.assetapi;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import java.util.Arrays;
import java.util.Collections;

/**
 * Backdoor asset API utility that assists with retrieving asset data without
 * using legitimate APIs. Warning: Using this class will bypass security,
 * revision tracking, approval, and compositional dependency management
 * subsystems and should only be used with extreme caution.
 * 
 * User: Tony Field Date: 2011-05-07
 */
public final class DirectSqlAccessTools {
    private final ICS ics;

    public DirectSqlAccessTools(ICS ics) {
        this.ics = ics;
    }

    private static final PreparedStmt IS_FLEX = new PreparedStmt(
            "SELECT assettype,logic FROM AssetType WHERE assettype = ? AND logic = 'com.openmarket.assetframework.complexasset.ComplexAsset'",
            Collections.singletonList("AssetType"));

    static {
        IS_FLEX.setElement(0, "AssetType", "assettype");
    }

    public boolean isFlex(AssetId id) {
        StatementParam param = IS_FLEX.newParam();
        param.setString(0, id.getType());
        return SqlHelper.select(ics, IS_FLEX, param).iterator().hasNext();
    }

    private static final PreparedStmt FLEX_ATTR_TYPE = new PreparedStmt(
            "SELECT assetattr FROM FlexAssetTypes WHERE assettype = ?", Collections.singletonList("FlexAssetTypes"));

    static {
        FLEX_ATTR_TYPE.setElement(0, "FlexAssetTypes", "assettype");
    }

    public String getFlexAttributeType(AssetId id) {
        if (!isFlex(id))
            throw new IllegalArgumentException("Asset " + id + " is not a flex asset!");
        StatementParam param = FLEX_ATTR_TYPE.newParam();
        param.setString(0, id.getType());
        return SqlHelper.selectSingle(ics, FLEX_ATTR_TYPE, param).getString("assetattr");
    }

    public String getFlexAttributeValue(AssetId id, String attrName) {
        // todo: medium: fix as this is very inefficient
        String attrType = getFlexAttributeType(id);
        PreparedStmt flexFields = new PreparedStmt("SELECT attr.name AS name, cmungo.stringvalue AS stringvalue "
                + "FROM " + attrType + " attr, " + id.getType() + "_Mungo cmungo " + "WHERE cmungo.cs_ownerid = ? "
                + "AND cmungo.cs_attrid = attr.id " + "AND attr.name = ?", Arrays.asList(attrType, id.getType()
                + "_Mungo"));
        flexFields.setElement(0, id.getType() + "_Mungo", "cs_ownerid");
        flexFields.setElement(1, attrType, "name");
        StatementParam param = flexFields.newParam();
        param.setLong(0, id.getId());
        param.setString(1, attrName);
        Row r = SqlHelper.selectSingle(ics, flexFields, param);
        if (r == null)
            return null;
        else
            return r.getString("stringvalue");
    }
}
