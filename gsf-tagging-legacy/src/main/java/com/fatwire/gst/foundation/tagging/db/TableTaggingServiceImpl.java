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
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.DirectSqlAccessTools;
import com.fatwire.gst.foundation.facade.cm.AddRow;

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

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fatwire.gst.foundation.tagging.TagUtils.asTag;
import static com.fatwire.gst.foundation.tagging.TagUtils.convertTagToCacheDepString;

/**
 * Database-backed implementation of AsseTaggingService
 * 
 * @author Tony Field
 * @since Jul 28, 2010
 * 
 * 
 * @deprecated as of release 12.x, replaced with WCS 12c's native tagging
 * 
 */
public final class TableTaggingServiceImpl implements AssetTaggingService {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.tagging.db.TableTaggingServiceImpl");

    public static String TAGREGISTRY_TABLE = "GSTTagRegistry";
    public static String TABLE_ACL_LIST = ""; // no ACLs because events are
    // anonymous

    private final ICS ics;
    private final DirectSqlAccessTools directSqlAccessTools;

    public TableTaggingServiceImpl(ICS ics) {
        this.ics = ics;
        this.directSqlAccessTools = new DirectSqlAccessTools(ics);
    }

    public void install() {
        TableDef def = new TableDef(TAGREGISTRY_TABLE, TABLE_ACL_LIST, "obj");
        // todo: low priority: define the PK properly (this will work for now)

        def.addColumn("tag", TableColumn.Type.ccvarchar).setLength(255).setNullable(false);
        def.addColumn("assettype", TableColumn.Type.ccvarchar).setLength(255).setNullable(false);
        def.addColumn("assetid", TableColumn.Type.ccbigint).setNullable(false);
        def.addColumn("startdate", TableColumn.Type.ccdatetime).setNullable(true);
        def.addColumn("enddate", TableColumn.Type.ccdatetime).setNullable(true);

        new TableCreator(ics).createTable(def);
    }

    public boolean isInstalled() {
        return SqlHelper.tableExists(ics, TAGREGISTRY_TABLE);
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
        // Only add assets that are tagged.

        TaggedAsset asset = loadTaggedAsset(id);
        if (isTagged(asset)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Adding tagged asset to tag registry: " + asset);
            }
            for (Tag tag : asset.getTags()) {

                AddRow r = new AddRow(TAGREGISTRY_TABLE);

                r.set("id", ics.genID(true));
                r.set("tag", tag.getTag());
                r.set("assettype", id.getType());
                r.set("assetid", id.getId());
                r.set("startdate", asset.getStartDate());
                r.set("enddate", asset.getEndDate());
                r.execute(ics);
            }
        }
    }

    public void updateAsset(AssetId id) {
        // todo: low priority: optimize
        deleteAsset(id);
        addAsset(id);
    }

    public void deleteAsset(AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Attempting to remove asset from tag registry:" + id);
        }
        SqlHelper.execute(
                ics,
                TAGREGISTRY_TABLE,
                "DELETE FROM " + TAGREGISTRY_TABLE + " WHERE assettype = '" + id.getType() + "' and assetid = "
                        + id.getId());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Deleted tagged asset " + id + " from tag registry (or asset was never there in the first place)");
        }
    }

    public Collection<Tag> getTags(AssetId id) {
        // this method records all the compositional dependencies that we need
        // for this method (this is critical)
        return loadTaggedAsset(id).getTags();
    }

    public Collection<Tag> getTags(Collection<AssetId> ids) {
        HashSet<Tag> tags = new HashSet<Tag>();
        // todo: medium priority: IMPORTANT: This should be optimized so that we
        // don't kill the db
        if (ids.size() > 5)
            LOG.warn("Fetching tags serially for " + ids.size()
                    + " assets.  TableTaggingServiceImpl isn't yet optimized to handle this very nicely");
        for (AssetId id : ids) {
            tags.addAll(getTags(id));
        }
        return tags;
    }

    /**
     * Retrieve the tags for the tagged asset. This method records a
     * compositional dependency on both the input asset AND the tags themselves.
     * 
     * @param id asset id
     * @return tagged asset
     */
    private TaggedAsset loadTaggedAsset(AssetId id) {
        LogDep.logDep(ics, id);

        // Temporarily disable usage of asset APIs in this use case due to a bug
        // in which asset listeners
        // cause a deadlock when the asset API is used.

        final TaggedAsset ret;
        final String gsttagAttrVal;
        if (directSqlAccessTools.isFlex(id)) {
            PreparedStmt basicFields = new PreparedStmt("SELECT id,startdate,enddate" + " FROM " + id.getType()
                    + " WHERE id = ?", Collections.singletonList(id.getType()));
            basicFields.setElement(0, id.getType(), "id");

            StatementParam param = basicFields.newParam();
            param.setLong(0, id.getId());
            Row row = SqlHelper.selectSingle(ics, basicFields, param);

            Date start = StringUtils.isBlank(row.getString("startdate")) ? null : row.getDate("startdate");
            Date end = StringUtils.isBlank(row.getString("enddate")) ? null : row.getDate("enddate");
            ret = new TaggedAsset(id, start, end);
            // todo: medium: optimize as this is very inefficient for flex
            // assets
            gsttagAttrVal = directSqlAccessTools.getFlexAttributeValue(id, "gsttag");

        } else {

            PreparedStmt basicFields = new PreparedStmt("SELECT * FROM " + id.getType() + " WHERE ID = ?",
                    Collections.singletonList(id.getType()));
            basicFields.setElement(0, id.getType(), "id");

            StatementParam param = basicFields.newParam();
            param.setLong(0, id.getId());
            Row row = SqlHelper.selectSingle(ics, basicFields, param);

            Date start = StringUtils.isBlank(row.getString("startdate")) ? null : row.getDate("startdate");
            Date end = StringUtils.isBlank(row.getString("enddate")) ? null : row.getDate("enddate");
            ret = new TaggedAsset(id, start, end);
            String s = "";
            try {
                if (row.isField("gsttag")) {
                    s = row.getString("gsttag");
                }
            } catch (Exception e) {
                LOG.trace("Could not get gsttag data from basic asset.  Maybe this is just because "
                        + "there is no gsttag column - which is just fine.", e);
            }
            gsttagAttrVal = s;
        }

        if (StringUtils.isNotBlank(gsttagAttrVal)) {
            for (String tag : gsttagAttrVal.split(",")) {
                Tag oTag = asTag(tag);
                recordCacheDependency(oTag);
                ret.addTag(oTag);
            }
        }

        // End temporary deadlock workaround

        if (LOG.isTraceEnabled())
            LOG.trace("Loaded tagged asset " + ret);
        return ret;
    }

    private AssetMapper<TaggedAsset> mapper = new AssetMapper<TaggedAsset>() {

        public TaggedAsset map(AssetData data) {
            Date startDate = AttributeDataUtils.asDate(data.getAttributeData("startdate"));
            Date endDate = AttributeDataUtils.asDate(data.getAttributeData("enddate"));
            TaggedAsset ret = new TaggedAsset(data.getAssetId(), startDate, endDate);
            for (String tag : AttributeDataUtils.getAndSplitString(data.getAttributeData("gsttag"), ",")) {
                Tag oTag = asTag(tag);

                ret.addTag(oTag);
            }

            return ret;
        }

    };

    /**
     * Retrieve the tags for the tagged asset. This method records a
     * compositional dependency on both the input asset AND the tags themselves.
     * 
     * @param id asset id
     * @return tagged asset
     */

    @SuppressWarnings("unused")
    private TaggedAsset loadTaggedAssetNew(AssetId id) {
        LogDep.logDep(ics, id);
        AssetAccessTemplate aat = new AssetAccessTemplate(ics);
        TaggedAsset ret = aat.readAsset(id, mapper, "startdate", "enddate", "gsttag");

        for (Tag tag : ret.getTags()) {
            recordCacheDependency(tag);
        }
        if (LOG.isTraceEnabled())
            LOG.trace("Loaded tagged asset " + ret);
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
        TaggedAsset ta = loadTaggedAsset(id);
        return isTagged(ta);
    }

    public boolean isTagged(TaggedAsset ta) {
        if (ta == null)
            return false;
        try {
            if (ta.getTags().size() > 0) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("isTagged loaded the asset and found that " + ta.getId() + " is a tagged asset.");
                }
                return true;
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("isTagged loaded the asset and found that " + ta.getId() + " is not a tagged asset.");
                }
                return false;
            }
        } catch (RuntimeException e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(
                        "isTagged found that " + ta.getId() + " is not a tagged asset.  We found an exception: " + e.toString(),
                        e);
            }
            return false;
        }
    }

    private static final PreparedStmt REGISTRY_SELECT = new PreparedStmt(
            "SELECT tag, assettype, assetid, startdate, enddate " + "FROM " + TAGREGISTRY_TABLE
                    + " WHERE tag=? ORDER BY startdate,enddate", Collections.singletonList(TAGREGISTRY_TABLE));

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
