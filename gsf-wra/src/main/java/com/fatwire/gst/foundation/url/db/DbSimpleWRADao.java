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
package com.fatwire.gst.foundation.url.db;

import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.logging.LogUtil;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.wra.SimpleWRADao;
import com.fatwire.gst.foundation.wra.SimpleWra;

import org.apache.commons.logging.Log;

/**
 * @author Dolf Dijkstra
 * @since November 1, 2011
 * 
 */
public class DbSimpleWRADao implements SimpleWRADao {

    private static final Log LOG = LogUtil.getLog(DbSimpleWRADao.class);

    private final ICS ics;

    public DbSimpleWRADao(final ICS ics) {
        super();
        this.ics = ics;
    }

    @Override
    public SimpleWra getWra(final AssetId id) {

        final PreparedStmt ASSET_SQL = new PreparedStmt("SELECT id, path, template, startdate, enddate FROM "
                + id.getType() + " WHERE id=? AND status !='VO'", Collections.singletonList(id.getType()));

        ASSET_SQL.setElement(0, id.getType(), "id");
        final StatementParam param = ASSET_SQL.newParam();
        param.setLong(0, id.getId());
        final Row row = SqlHelper.selectSingle(ics, ASSET_SQL, param);
        if (row == null) {
            return null;
        }
        return new SimpleWra(row, id);

    }

    private static final String ASSETPUBLICATION_QRY = "SELECT p.name from Publication p, AssetPublication ap "
            + "WHERE ap.assettype = ? " + "AND ap.assetid = ? " + "AND ap.pubid=p.id";
    static final PreparedStmt AP_STMT = new PreparedStmt(ASSETPUBLICATION_QRY,
            Collections.singletonList("AssetPublication")); // todo: low
                                                            // priority:
    // determine
    // why publication
    // cannot fit there.

    static {
        AP_STMT.setElement(0, "AssetPublication", "assettype");
        AP_STMT.setElement(1, "AssetPublication", "assetid");
    }

    @Override
    public String resolveSite(final AssetId id) {
        final StatementParam param = AP_STMT.newParam();
        param.setString(0, id.getType());
        param.setLong(1, id.getId());
        String result = null;
        for (final Row pubid : SqlHelper.select(ics, AP_STMT, param)) {
            if (result != null) {
                LOG.warn("Found asset "
                        + id
                        + " in more than one publication. It should not be shared; aliases are to be used for cross-site sharing.  Controller will use first site found: "
                        + result);
            } else {
                result = pubid.getString("name");
            }
        }
        return result;
    }

}
