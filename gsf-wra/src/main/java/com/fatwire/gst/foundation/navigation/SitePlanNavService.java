package com.fatwire.gst.foundation.navigation;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple navigation service implementation that loads objects from the Site Plan. Supports populating node data via
 * a dedicated method that can be overridden to load any data that is required, as long as it can be presented
 * as a TemplateAsset.
 *
 * Reads the full site plan tree in one query.
 * @author Tony Field
 * @since 2016-07-06
 */
public class SitePlanNavService implements NavService {

    private final ICS ics;
    private final TemplateAssetAccess dao;

    public SitePlanNavService(ICS ics, TemplateAssetAccess dao) {
        this.ics = ics;
        this.dao = dao;
    }

    public static final PreparedStmt NAVIGATION_TREE_LOOKUP = new PreparedStmt(
            "WITH tblChildren (nid, nparentid, oid, otype, nrank) AS "
                    + "( "
                    + " select spt.NID, spt.NPARENTID, spt.OID, spt.otype, spt.nrank from SITEPLANTREE spt "
                    + " WHERE spt.OID = ? "
                    + " UNION ALL "
                    + " SELECT spt.NID, spt.NPARENTID, spt.OID, spt.OTYPE, spt.nrank from "
                    + " SITEPLANTREE spt JOIN tblChildren ON spt.NPARENTID = tblChildren.NID where spt.NCODE = 'Placed' "
                    + ") " + " SELECT NID, NPARENTID, OID, OTYPE, NRANK "
                    + " FROM tblChildren order by NRANK ",
            Arrays.asList("SITEPLANTREE"));

    static {
        NAVIGATION_TREE_LOOKUP.setElement(0, "SITEPLANTREE", "OID");
    }

    public AssetNode loadNav(AssetId sitePlan) {

        if (sitePlan == null) {
            throw new IllegalArgumentException("Null param not allowed");
        }

        // read the site plan tree in one massive query
        StatementParam assetIdParam = NAVIGATION_TREE_LOOKUP.newParam();
        assetIdParam.setLong(0, sitePlan.getId());
        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();
        for (Row row : new IListIterable(ics.SQL(NAVIGATION_TREE_LOOKUP, assetIdParam, true))) {
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

        throw new IllegalStateException("Could not lcoate root node in processed tree data. Possible bug.");
    }

    /**
     * Method to retrieve data that will be loaded into a node. Implementing classes should take care
     * to be very efficient both for cpu time as well as memory usage.
     * @param id asset ID to load
     * @return asset data in the form of a TemplateAsset, never null
     */
    protected TemplateAsset populateNodeData(AssetId id) {
        return dao.read(id, "name", "template");
    }

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

    }

}
