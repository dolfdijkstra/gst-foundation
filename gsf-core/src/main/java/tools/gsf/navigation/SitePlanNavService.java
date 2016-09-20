/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.navigation;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import tools.gsf.facade.assetapi.AssetIdUtils;
import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.facade.runtag.render.LogDep;
import tools.gsf.facade.sql.IListIterable;
import tools.gsf.facade.sql.Row;

import java.util.*;

/**
 * Simple navigation service implementation that loads objects from the Site Plan. Supports populating node data via
 * a dedicated method that can be overridden to load any data that is required, as long as it can be presented
 * as a TemplateAsset.
 *
 * Reads the full site plan tree in one query.
 * @author Tony Field
 * @since 2016-07-06
 */
public abstract class SitePlanNavService implements NavService<AssetNode> {

    private final ICS ics;
    private final TemplateAssetAccess dao;
    private final boolean isHsqldb;

    public SitePlanNavService(ICS ics, TemplateAssetAccess dao) {
        this.ics = ics;
        this.dao = dao;
        this.isHsqldb = "HSQLDB".equals(ics.GetProperty("cs.dbtype"));
    }

    private static final PreparedStmt NAVIGATION_TREE_LOOKUP = new PreparedStmt(
            "WITH tblChildren (nid, nparentid, oid, otype, nrank) AS "
                    + "( "
                    + " select spt.NID, spt.NPARENTID, spt.OID, spt.otype, spt.nrank from SITEPLANTREE spt "
                    + " WHERE spt.OID = ? "
                    + " UNION ALL "
                    + " SELECT spt.NID, spt.NPARENTID, spt.OID, spt.OTYPE, spt.nrank from "
                    + " SITEPLANTREE spt JOIN tblChildren ON spt.NPARENTID = tblChildren.NID where spt.NCODE = 'Placed' "
                    + ") "
                    + " SELECT NID, NPARENTID, OID, OTYPE, NRANK "
                    + " FROM tblChildren order by NRANK ",
            Collections.singletonList("SITEPLANTREE"));
    static {
        NAVIGATION_TREE_LOOKUP.setElement(0, "SITEPLANTREE", "OID");
    }
    private static final PreparedStmt NAVIGATION_TREE_DUMP = new PreparedStmt(
            "select * from SITEPLANTREE where ncode = 'Placed'",
            Collections.singletonList("SITEPLANTREE"));

    public AssetNode loadNav(AssetId sitePlan) {

        if (sitePlan == null) {
            throw new IllegalArgumentException("Null param not allowed");
        }

        // read the site plan tree in one massive query
        StatementParam assetIdParam = NAVIGATION_TREE_LOOKUP.newParam();
        assetIdParam.setLong(0, sitePlan.getId());
        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();
        IList sitePlanTree = isHsqldb
                ? ics.SQL(NAVIGATION_TREE_DUMP, NAVIGATION_TREE_DUMP.newParam(), true)
                : ics.SQL(NAVIGATION_TREE_LOOKUP, assetIdParam, true);

        for (Row row : new IListIterable(sitePlanTree)) {
            SitePlanTreeData nodeInfo = new SitePlanTreeData(row);
            rowMap.put(nodeInfo.nid, nodeInfo);
        }

        // create Node objects
        Map<Long, SimpleAssetNode> nodeMap = new HashMap<>();
        for (long nid : rowMap.keySet()) {
            SimpleAssetNode node = new SimpleAssetNode(rowMap.get(nid).assetId);
            nodeMap.put(nid, node);
        }

        // hook up parent-child relationships
        for (long nid : rowMap.keySet()) {
            SitePlanTreeData sptRow = rowMap.get(nid);
            SimpleAssetNode node = nodeMap.get(nid);
            SimpleAssetNode parent = nodeMap.get(sptRow.nparentid);
            if (parent != null) {
                node.setParent(parent);
                parent.addChild(sptRow.nrank, node); // this ranks them too!
            }
        }

        // populate each asset
        for (SimpleAssetNode node : nodeMap.values()) {
            AssetId id = node.getId();
            LogDep.logDep(ics, id); // record compositional dependency
            TemplateAsset data = populateNodeData(id);
            if (data == null) {
                throw new IllegalStateException("Null node data returned for id "+id);
            }
            node.setAsset(data);
        }

        // return the loaded root node
        for (SimpleAssetNode node : nodeMap.values()) {
            if (node.getId().equals(sitePlan)) {
                return node;
            }
        }

        throw new IllegalStateException("Could not locate root node in processed tree data. Possible bug.");
    }

    /**
     * Method to retrieve data that will be loaded into a node. Implementing classes should take care
     * to be very efficient both for cpu time as well as memory usage.
     * @param id asset ID to load
     * @return asset data in the form of a TemplateAsset, never null
     */
    protected abstract TemplateAsset populateNodeData(AssetId id);

    private static class SitePlanTreeData {
        final long nid;
        final long nparentid;
        final int nrank;
        final AssetId assetId;

        SitePlanTreeData(Row row) {
            nid = row.getLong("nid");
            nparentid = row.getLong("nparentid");
            nrank = row.getInt("nrank");
            assetId = AssetIdUtils.createAssetId(row.getString("otype"), row.getLong("oid"));
        }

        @Override
        public String toString() {
            return "SitePlanTreeData{" +
                    "nid=" + nid +
                    ", nparentid=" + nparentid +
                    ", nrank=" + nrank +
                    ", assetId=" + assetId +
                    '}';
        }
    }

    private static final PreparedStmt BREADCRUMBS_LOOKUP = new PreparedStmt("WITH tblChildren (nid, nparentid, oid, otype, nrank) AS "
            + "( "
            + " select spt.NID, spt.NPARENTID, spt.OID, spt.otype, spt.nrank from SITEPLANTREE spt "
            + " WHERE spt.OID = ? "
            + " UNION ALL "
            + " SELECT spt.NID, spt.NPARENTID, spt.OID, spt.OTYPE, spt.nrank from "
            + " SITEPLANTREE spt JOIN tblChildren ON spt.NID = tblChildren.NPARENTID where spt.OTYPE = 'Page' "
            + ") "
            + " SELECT NID, NPARENTID, OID, OTYPE, NRANK "
            + " FROM tblChildren ",
            Collections.singletonList("SITEPLANTREE"));
    static {
        BREADCRUMBS_LOOKUP.setElement(0, "SITEPLANTREE", "OID");
    }

    public List<AssetNode> getBreadcrumb(AssetId id) {

        if (id == null) {
            throw new IllegalArgumentException("Cannot calculate breadcrumb of a null asset");
        }

        return isHsqldb ? _breadcrumbByFullScan(id) : _breadcrumbByQuery(id);
    }
    
    private List<AssetNode> _getAllAncestors(AssetNode node) {
        List<AssetNode> ancestors = new ArrayList<>();
        do {
            ancestors.add(node);
            node = node.getParent();
        } while (node != null);
        Collections.reverse(ancestors);
        return ancestors;
    }


    private List<AssetNode> _breadcrumbByFullScan(AssetId id) {

        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();
        StatementParam dumpParam = NAVIGATION_TREE_DUMP.newParam();
        for (Row row : new IListIterable(ics.SQL(NAVIGATION_TREE_DUMP, dumpParam, true))) {
            SitePlanTreeData nodeInfo = new SitePlanTreeData(row);
            rowMap.put(nodeInfo.nid, nodeInfo);
        }

        // create Node objects
        Map<Long, SimpleAssetNode> nodeMap = new HashMap<>();
        for (long nid : rowMap.keySet()) {
            SimpleAssetNode node = new SimpleAssetNode(rowMap.get(nid).assetId);
            nodeMap.put(nid, node);
        }

        // hook up parent-child relationships
        for (long nid : rowMap.keySet()) {
            SitePlanTreeData sptRow = rowMap.get(nid);
            SimpleAssetNode node = nodeMap.get(nid);
            SimpleAssetNode parent = nodeMap.get(sptRow.nparentid);
            if (parent != null) {
                node.setParent(parent);
                parent.addChild(sptRow.nrank, node); // this ranks them too!
            }
        }

        // find my node
        AssetNode myNode = nodeMap.values().stream().filter(n -> n.getId().equals(id)).findFirst().get();
        if (myNode == null) throw new IllegalArgumentException("Could not find breadcrumb for "+id);

        // return the breadcrumb for it in the form of assetids
        return this._getAllAncestors(myNode);
    }

    private List<AssetNode> _breadcrumbByQuery(AssetId id) {
		List<AssetNode> results = new ArrayList<AssetNode>();
        StatementParam breadcrumbParam = BREADCRUMBS_LOOKUP.newParam();
        breadcrumbParam.setLong(0, id.getId());

        for (Row row : new IListIterable(ics.SQL(BREADCRUMBS_LOOKUP, breadcrumbParam, true))) {
            AssetId currentAssetId = AssetIdUtils.createAssetId(row.getString("otype"), row.getLong("oid"));
            SimpleAssetNode currentNode = new SimpleAssetNode(currentAssetId);

            // populate the asset
            LogDep.logDep(ics, currentAssetId); // record compositional dependency
            TemplateAsset data = populateNodeData(currentAssetId);
            if (data == null) {
                throw new IllegalStateException("Null node data returned for id "+id);
            }
            currentNode.setAsset(data);

            if (! results.isEmpty())
            	((SimpleAssetNode) results.get(0)).setParent(currentNode);
            results.add(0, currentNode);
        }
        
        return results;
    }
    
}