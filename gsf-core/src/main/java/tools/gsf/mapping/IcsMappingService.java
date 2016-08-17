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
package tools.gsf.mapping;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import org.apache.commons.lang3.StringUtils;
import tools.gsf.facade.assetapi.AssetAccessTemplate;
import tools.gsf.facade.assetapi.AssetIdWithSite;
import tools.gsf.facade.sql.Row;
import tools.gsf.facade.sql.SqlHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MappingService implementation making use to sql queries to perform fast lookup of mapping values.
 *
 * @author Dolf Dijkstra
 * @author Tony Field
 * @since Apr 13, 2011
 */
public final class IcsMappingService implements MappingService {

    private final static PreparedStmt template = new PreparedStmt("SELECT * FROM Template_Map WHERE cs_ownerid=? AND cs_siteid=?",
            Arrays.asList("Template", "Template_Map"));
    private final static PreparedStmt element = new PreparedStmt("SELECT * FROM CSElement_Map WHERE cs_ownerid=? AND cs_siteid=?",
            Arrays.asList("CSElement", "CSElement_Map"));
    private static final PreparedStmt lookup_sitecatalog = new PreparedStmt("select * from SiteCatalog where pagename = ?",
            Arrays.asList("SiteEntry","Template")); // we will only query pagenames that map to these, we promise
    private static final PreparedStmt lookup_template = new PreparedStmt("select * from Template where rootelement = ?",
            Collections.singletonList("Template")); // we will only query pagenames that map to these, we promise
    private static final PreparedStmt lookup_cselement = new PreparedStmt("select * from CSElement where rootelement = ?",
            Collections.singletonList("CSElement")); // we will only query pagenames that map to these, we promise
    static {
        template.setElement(0, "Template_Map", "cs_ownerid");
        template.setElement(1, "Template_Map", "cs_siteid");
        element.setElement(0, "CSElement_Map", "cs_ownerid");
        element.setElement(1, "CSElement_Map", "cs_siteid");
        lookup_sitecatalog.setElement(0, "SiteCatalog", "pagename");
        lookup_template.setElement(0, "Template", "rootelement");
        lookup_cselement.setElement(0, "CSElement", "rootelement");
    }

    private final ICS ics;
    private final AssetAccessTemplate aat;

    /**
     * @param ics Content Server context object
     */
    public IcsMappingService(final ICS ics, final AssetAccessTemplate aat) {
        this.ics = ics;
        this.aat = aat;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.gsf.mapping.MappingService#readMapping(com.fatwire
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
        final Map<String, MappingValue> map = new HashMap<>();
        for (final Row row : SqlHelper.select(ics, stmt, param)) {
            final String key = row.getString("cs_key");

            final MappingValue k = new MappingValue(MappingValue.Type.valueOf(row.getString("cs_type").toLowerCase()),
                    row.getString("cs_value"));
            map.put(key, k);
        }
        return map;
    }

    @Override
    public AssetIdWithSite resolveMapped(String eid, String tid, String site) {
        if (site != null) {
            if (eid != null) {
                return new AssetIdWithSite("CSElement", Long.valueOf(eid), site);
            }
            if (tid != null) {
                return new AssetIdWithSite("Template", Long.valueOf(tid), site);
            }
        }
        return null;
    }

    @Override
    public AssetIdWithSite resolveMapped(String pagename) {
        // note: we can only map templates or CSElements that are rootelements of pages. We cannot resolve
        // the mapped code in other cases unfortunately.
        MappingPageData pageData = readPageData(pagename);
        if (pageData.isSiteEntry()) {
            long eid = lookupCSElement(pageData.rootelement);
            if (eid > -1) {
                String site = ics.GetVar("site");
                if (StringUtils.isBlank(site)) {
                    site = pageData.getSiteResarg();
                }
                if (StringUtils.isNotBlank(site)) {
                    return new AssetIdWithSite("CSElement", eid, site);
                }
            }
        } else {
            long tid = lookupTemplate(pageData.rootelement);
            if (tid > 0) {
                String site = ics.GetVar("site");
                if (StringUtils.isBlank(site)) {
                    site = lookupSiteForTemplate(pageData);
                }
                if (StringUtils.isNotBlank(site)) {
                    return new AssetIdWithSite("Template", tid, site);
                }
            }
        }
        return null;
    }

    private MappingPageData readPageData(String pagename) {
        StatementParam param = lookup_sitecatalog.newParam();
        param.setString(0, pagename);
        Row row = SqlHelper.selectSingle(ics, lookup_sitecatalog, param);
        return new MappingPageData(row);
    }

    private long lookupCSElement(String rootelement) {
        StatementParam param = lookup_cselement.newParam();
        param.setString(0, rootelement);
        Row row = SqlHelper.selectSingle(ics, lookup_template, param);
        return row == null ? -1L : row.getLong("id");
    }

    private long lookupTemplate(String rootelement) {
        StatementParam param = lookup_template.newParam();
        param.setString(0, rootelement);
        Row row = SqlHelper.selectSingle(ics, lookup_template, param);
        return row == null ? -1L : row.getLong("id");
    }

    private String lookupSiteForTemplate(MappingPageData pageData) {
        // always siteName/type/tname or siteName/tname
        return StringUtils.substringBefore(pageData.pagename, "/");
    }

    private static class MappingPageData {
        private final String pagename;
        private final Map<String,String> resargs;
        private final String rootelement;
        // could add more here if we cared but this is a private class so no need to worry now

        private MappingPageData(Row row) {
            this.pagename = row.getString("pagename");
            this.resargs = new HashMap<>();
            Utilities.getParams(row.getString("resargs1"), resargs, false);
            Utilities.getParams(row.getString("resargs2"), resargs, false);
            this.rootelement = row.getString("rootelement");
        }

        private boolean isSiteEntry() {
            return resargs.containsKey("seid");
        }

        private String getSiteResarg() {
            return resargs.get("site");
        }
    }
}
