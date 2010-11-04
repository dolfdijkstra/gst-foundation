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
import java.util.HashSet;
import java.util.List;

import COM.FutureTense.Cache.CacheHelper;
import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.tagging.AssetTaggingService;
import com.fatwire.gst.foundation.tagging.Tag;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.fatwire.gst.foundation.tagging.TagUtils.asTag;
import static com.fatwire.gst.foundation.tagging.TagUtils.convertTagToCacheDepString;

/**
 * Database-backed implementation of AsseTaggingService
 *
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class TableTaggingServiceImpl implements AssetTaggingService {

    private static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.tagging");

    public static String TAGREGISTRY_TABLE = "GSTTagRegistry";
    public static String TABLE_ACL_LIST = ""; // no ACLs becasue events are anonymous

    private final ICS ics;

    public TableTaggingServiceImpl(ICS ics) {
        this.ics = ics;
    }

    public void install() {
        TableDef def = new TableDef(TAGREGISTRY_TABLE, TABLE_ACL_LIST, "obj"); // todo: define the PK properly (this will work for now)

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
        List<String> ids = new ArrayList<String>();
        for (Tag tag : tags) {
            ids.add(convertTagToCacheDepString(tag));
        }
        CacheManager cm = new CacheManager(ics);
        cm.setPagesByID(ics, ids.toArray(new String[ids.size()]));
        cm.flushCSEngine(ics, CacheHelper._both);
        cm.flushSSEngines(ics);
    }

    public void addAsset(AssetId id) {
        TaggedAsset asset = loadTaggedAsset(id);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Adding tagged asset to tag registry: " + asset);
        }
        for (Tag tag : loadTaggedAsset(id).getTags()) {
            FTValList vl = new FTValList();
            vl.setValString("ftcmd", "addrow");
            vl.setValString("tablename", TAGREGISTRY_TABLE);
            vl.setValString("id", ics.genID(true));
            vl.setValString("tag", tag.getTag());
            vl.setValString("assettype", id.getType());
            vl.setValString("assetid", Long.toString(id.getId()));
            if (asset.getStartDate() != null) vl.setValString("startdate", Util.formatJdbcDate(asset.getStartDate()));
            if (asset.getEndDate() != null) vl.setValString("enddate", Util.formatJdbcDate(asset.getEndDate()));
            if (!ics.CatalogManager(vl) || ics.GetErrno() < 0) {
                throw new CSRuntimeException("Failure adding tag to tag registry", ics.GetErrno());
            }
        }
    }

    public void updateAsset(AssetId id) {
        // todo: optimize
        deleteAsset(id);
        addAsset(id);
    }

    public void deleteAsset(AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Removing tagged asset from tag registry:" + id);
        }
        // todo: fail gracefully
        SqlHelper.execute(ics, TAGREGISTRY_TABLE, "delete from " + TAGREGISTRY_TABLE + " where assettype = '" + id.getType() + "' and assetid = " + id.getId());
    }

    public Collection<Tag> getTags(AssetId id) {
        // this method records all the compositional dependencies that we need for this method (this is critical)
        return loadTaggedAsset(id).getTags();
    }

    public Collection<Tag> getTags(Collection<AssetId> ids) {
        HashSet<Tag> tags = new HashSet<Tag>();
        // todo: IMPORTANT: This should be optimized so that we don't kill the database
        if (ids.size() > 5)
            LOG.warn("Fetching tags serially for " + ids.size() + " assets.  TableTaggingServiceImpl isn't yet optimized to handle this very nicely");
        for (AssetId id : ids) {
            tags.addAll(getTags(id));
        }
        return tags;
    }

    /**
     * Retrieve the tags for the tagged asset.  This method records a compositional dependency on
     * both the input asset AND the tags themselves.
     *
     * @param id asset id
     * @return tagged asset
     */
    private TaggedAsset loadTaggedAsset(AssetId id) {
        LogDep.logDep(ics, id);
        AssetData data = AssetDataUtils.getAssetData(id, "startdate", "enddate", "gsttag");
        Date startDate = AttributeDataUtils.asDate(data.getAttributeData("startdate"));
        Date endDate = AttributeDataUtils.asDate(data.getAttributeData("enddate"));
        TaggedAsset ret = new TaggedAsset(id, startDate, endDate);
        for (String tag : AttributeDataUtils.getAndSplitString(data.getAttributeData("gsttag"), ",")) {
            Tag oTag = asTag(tag);
            recordCacheDependency(oTag);
            ret.addTag(oTag);
        }
        if (LOG.isTraceEnabled()) LOG.trace("Loaded tagged asset " + ret);
        return ret;
    }

    public Collection<AssetId> lookupTaggedAssets(Tag tag) {
        recordCacheDependency(tag);
        final StatementParam param = REGISTRY_SELECT.newParam();
        param.setString(0, tag.getTag());
        List<AssetId> ids = new ArrayList<AssetId>();
        for (final Row asset : SqlHelper.select(ics, REGISTRY_SELECT, param)) {
            AssetId id = new AssetIdImpl(asset.getString("assettype"), asset.getLong("assetid"));
            LogDep.logDep(ics, id);
            if (FilterAssetsByDate.isValidOnDate(ics, id, null)) {
                ids.add(id);
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Asset " + id + " tagged with " + tag + " is not active based on startdate/enddate");
            }
        }
        return ids;
    }

    public boolean isTagged(AssetId id) {
        // todo: optimize?
        try {
            loadTaggedAsset(id);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt("SELECT tag, assettype, assetid, startdate, enddate " + "FROM " + TAGREGISTRY_TABLE + " WHERE tag=? ORDER BY startdate,enddate", Collections.singletonList(TAGREGISTRY_TABLE));

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

        public String toString() {
            return id.toString() + "|startdate:" + startDate + "|enddate:" + endDate + "|tags:" + tags;
        }
    }
}
