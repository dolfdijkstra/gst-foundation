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
 * Backdoor asset API utility that assists with retrieving asset data without using legitimate APIs.
 * Warning: Using this class will bypass security, revision tracking, approval, and compositional
 * dependency management subsystems and should only be used with extreme caution.
 *
 * User: Tony Field
 * Date: 2011-05-07
 */
public final class DirectSqlAccessTools {
    private final ICS ics;
    public DirectSqlAccessTools(ICS ics) {
        this.ics = ics;
    }

    private static final PreparedStmt IS_FLEX = new PreparedStmt("select assettype,logic from AssetType where assettype = ? and logic = 'com.openmarket.assetframework.complexasset.ComplexAsset'", Collections.singletonList("AssetType"));

    static {
        IS_FLEX.setElement(0, "AssetType", "assettype");
    }
    public boolean isFlex(AssetId id) {
        StatementParam param = IS_FLEX.newParam();
        param.setString(0, id.getType());
        return SqlHelper.select(ics, IS_FLEX, param).iterator().hasNext();
    }

    private static final PreparedStmt FLEX_ATTR_TYPE = new PreparedStmt("select assetattr from FlexAssetTypes where assettype = ?", Collections.singletonList("FlexAssetTypes"));

    static {
        FLEX_ATTR_TYPE.setElement(0, "FlexAssetTypes", "assettype");
    }

    public String getFlexAttributeType(AssetId id) {
        if (!isFlex(id)) throw new IllegalArgumentException("Asset " + id + " is not a flex asset!");
        StatementParam param = FLEX_ATTR_TYPE.newParam();
        param.setString(0, id.getType());
        return SqlHelper.selectSingle(ics, FLEX_ATTR_TYPE, param).getString("assetattr");
    }

    public String getFlexAttributeValue(AssetId id, String attrName) {
        // todo: medium: fix as this is very inefficient
        String attrType = getFlexAttributeType(id);
        PreparedStmt flexFields = new PreparedStmt("select attr.name as name, cmungo.stringvalue as stringvalue " +
                "from " + attrType + " attr, " + id.getType() + "_Mungo cmungo " +
                "where cmungo.ownerid = ? " +
                "and cmungo.cs_attrid = attr.id " +
                "and attr.name = ?",
                Arrays.asList(attrType, id.getType() + "_Mungo"));
        flexFields.setElement(0, id.getType() + "_Mungo", "cs_ownerid");
        flexFields.setElement(1, attrType, "name");
        StatementParam param = flexFields.newParam();
        param.setLong(0, id.getId());
        param.setString(1, attrName);
        Row r = SqlHelper.selectSingle(ics, flexFields, param);
        if (r == null) return null;
        else return r.getString("stringvalue");
    }
}
