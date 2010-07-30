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
package com.fatwire.gst.foundation.tagging.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import COM.FutureTense.Cache.CacheHelper;
import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.tagging.AssetTaggingService;
import com.fatwire.gst.foundation.tagging.Tag;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import static com.fatwire.gst.foundation.facade.sql.SqlHelper.quote;
import static com.fatwire.gst.foundation.tagging.TagUtils.asTag;
import static com.fatwire.gst.foundation.tagging.TagUtils.convertTagToCacheDepString;

/**
 * Database-backed implementation of AsseTaggingService
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class TableTaggingServiceImpl implements AssetTaggingService {

    public static String TAGREGISTRY_TABLE = "GSTTagRegistry";

    private final ICS ics;

    public TableTaggingServiceImpl(ICS ics) {
        this.ics = ics;
    }

    public void install() {
        TableDef def = new TableDef(TAGREGISTRY_TABLE, ftMessage.Browser, ftMessage.no);

        def.addColumn(new TableColumn("tag", TableColumn.Type.ccvarchar).setLength(255).setNullable(false));
        def.addColumn(new TableColumn("assettype", TableColumn.Type.ccvarchar).setLength(255).setNullable(false));
        def.addColumn(new TableColumn("assetid", TableColumn.Type.ccbigint).setNullable(false));
        def.addColumn(new TableColumn("startdate", TableColumn.Type.ccdatetime).setNullable(true));
        def.addColumn(new TableColumn("enddate", TableColumn.Type.ccdatetime).setNullable(true));

        new TableCreator(ics).createTable(def);
    }

    public void recordCacheDependency(Tag tag) {
        CacheManager.RecordItem(ics, convertTagToCacheDepString(tag));
    }

    public void clearCacheForTag(Collection<Tag> tags) {
        CacheManager cm = new CacheManager(ics);
        List<String> ids = new ArrayList<String>();
        for (Tag tag : tags) {
            ids.add(convertTagToCacheDepString(tag));
        }
        cm.setPagesByID(ics, ids.toArray(new String[ids.size()]));
        cm.flushCSEngine(ics, CacheHelper._both);
        cm.flushSSEngines(ics);
    }

    public void addAsset(AssetId id) {
        TaggedAsset asset = loadTaggedAsset(id);
        for (Tag tag : loadTaggedAsset(id).getTags()) {
            final String sTag = quote(tag.getTag());
            final String sAssetType = quote(id.getType());
            final String sStartDate = asset.getStartDate() == null ? "null" : quote(Util.formatJdbcDate(asset.getStartDate()));
            final String sEndDate = asset.getEndDate() == null ? "" : quote(Util.formatJdbcDate(asset.getEndDate()));
            String qry = "insert into " + TAGREGISTRY_TABLE + " (tag, assettype, assetid, startdate, enddate) VALUES " + "(" + sTag + "," + sAssetType + "," + id.getId() + "," + sStartDate + "," + sEndDate + ")";
            SqlHelper.execute(ics, TAGREGISTRY_TABLE, qry);
        }
    }

    public void updateAsset(AssetId id) {
        // todo: optimize
        deleteAsset(id);
        addAsset(id);
    }

    public void deleteAsset(AssetId id) {
        // todo: fail gracefully
        SqlHelper.execute(ics, TAGREGISTRY_TABLE, "delete from " + TAGREGISTRY_TABLE + " where assettype = '" + id.getType() + "' and assetid = " + id.getId());
    }

    public Collection<Tag> getTags(AssetId id) {
        return loadTaggedAsset(id).getTags();
    }

    private TaggedAsset loadTaggedAsset(AssetId id) {
        AssetData data = AssetDataUtils.getAssetData(id, "startdate", "enddate", "gsttag");
        Date startDate = AttributeDataUtils.asDate(data.getAttributeData("startdate"));
        Date endDate = AttributeDataUtils.asDate(data.getAttributeData("enddate"));

        TaggedAsset ret = new TaggedAsset(id, startDate, endDate);
        for (String tag : AttributeDataUtils.getAndSplitString(data.getAttributeData("gsttag"), ",")) {
            ret.addTag(asTag(tag));
        }
        return ret;
    }

    public Collection<AssetId> lookupTaggedAssets(Tag tag) {
        final StatementParam param = REGISTRY_SELECT.newParam();
        param.setString(0, tag.getTag());
        final Date now = new Date();
        List<AssetId> ids = new ArrayList<AssetId>();
        for (final Row asset : SqlHelper.select(ics, REGISTRY_SELECT, param)) {
            if (FilterAssetsByDate.isDateWithinRange(asset.getString("startdate"), null, asset.getString("enddate"))) {
                ids.add(new AssetIdImpl(asset.getString("assettype"), asset.getLong("assetid")));
            } else {
                // asset is not within the valid date range... do nothing. (maybe someday it would be nice to log a msg)
            }
        }
        return ids;
    }

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt("SELECT assettype, assetid, startdate, enddate " + "FROM " + TAGREGISTRY_TABLE + " WHERE tag=? ORDER BY startdate,enddate", Collections.singletonList(TAGREGISTRY_TABLE));

    static {
        REGISTRY_SELECT.setElement(0, TAGREGISTRY_TABLE, "tag");
    }

    private static class TaggedAsset {
        final AssetId id;
        final Date startDate;
        final Date endDate;
        final Collection<Tag> tags;

        TaggedAsset(AssetId id, Date startDate, Date endDate) {
            this.id = id;
            this.startDate = startDate;
            this.endDate = endDate;
            tags = new ArrayList<Tag>();
        }

        public AssetId getId() {
            return id;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public Collection<Tag> getTags() {
            return tags;
        }

        private void addTag(Tag tag) {
            tags.add(tag);
        }
    }
}
