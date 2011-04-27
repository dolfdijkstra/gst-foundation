/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.mapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class IcsMappingService implements MappingService {

    static PreparedStmt template = new PreparedStmt("SELECT * FROM Template_Map WHERE cs_ownerid=? AND cs_siteid=?",
            Arrays.asList("Template", "Template_Map"));
    static PreparedStmt element = new PreparedStmt("SELECT * FROM CSElement_Map WHERE cs_ownerid=? AND cs_siteid=?",
            Arrays.asList("CSElement", "CSElement_Map"));

    static {
        template.setElement(0, "Template_Map", "cs_ownerid");
        template.setElement(1, "Template_Map", "cs_siteid");
        element.setElement(0, "CSElement_Map", "cs_ownerid");
        element.setElement(1, "CSElement_Map", "cs_siteid");

    }

    final ICS ics;
    final AssetAccessTemplate aat;

    /**
     * @param ics
     */
    public IcsMappingService(final ICS ics) {
        super();
        this.ics = ics;
        aat = new AssetAccessTemplate(ics);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.mapping.MappingService#readMapping(com.fatwire
     * .gst.foundation.controller.AssetIdWithSite)
     */
    public Map<String, MappingValue> readMapping(final AssetIdWithSite id) {
        if ("Template".equals(id.getType())) {
            return readIt(id, template);
        } else if ("CSElement".equals(id.getType())) {
            return readIt(id, element);
        } else {
            throw new IllegalArgumentException("Cannot handle " + id.getType());
        }

    }

    private Map<String, MappingValue> readIt(final AssetIdWithSite id, final PreparedStmt stmt) {
        final StatementParam param = stmt.newParam();
        param.setLong(0, id.getId());
        param.setLong(1, aat.readSiteInfo(id.getSite()).getId());
        final Map<String, MappingValue> map = new HashMap<String, MappingValue>();
        for (final Row row : SqlHelper.select(ics, stmt, param)) {
            final String key = row.getString("cs_key");

            final MappingValue k = new MappingValue(MappingValue.Type.valueOf(row.getString("cs_type").toLowerCase()),
                    row.getString("cs_value"));
            map.put(key, k);
        }
        return map;
    }
}
