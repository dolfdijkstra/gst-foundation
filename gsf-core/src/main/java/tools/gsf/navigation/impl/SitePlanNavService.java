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
package tools.gsf.navigation.impl;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import tools.gsf.facade.assetapi.AssetIdUtils;
import tools.gsf.facade.runtag.render.LogDep;
import tools.gsf.facade.sql.Row;
import tools.gsf.facade.sql.SqlHelper;
import tools.gsf.navigation.AssetNode;
import tools.gsf.navigation.NavService;

import java.util.*;

/**
 * Simple navigation service implementation that loads objects from the Site Plan. Supports populating node data via
 * a dedicated method that can be overridden to load any data that is required, as long as it can be presented
 * as a TemplateAsset.
 *
 * Reads the full site plan tree in one query, but only reads asset data for nodes that it is going to actually return.
 *
 * @author Tony Field
 * @since 2016-07-06
 */
public abstract class SitePlanNavService<ANODE extends AssetNode<ANODE>> implements NavService<ANODE, AssetId, AssetId> {

    private final ICS ics;
    private final Map<AssetId, List<ANODE>> nodesById = new HashMap<>();

    private static final PreparedStmt NAVIGATION_TREE_DUMP = new PreparedStmt(
            "select * from SITEPLANTREE where ncode = 'Placed'",
            Arrays.asList("page", "siteplantree", "Page", "SitePlanTree", "PAGE", "SITEPLANTREE"));
    
    protected abstract ANODE createAssetNode(AssetId assetId);
    
    protected ICS getIcs() {
    	return this.ics;
    }
    
    public SitePlanNavService(ICS ics) {

        this.ics = ics;

        // read the site plan tree in one massive query
        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();

        for (Row row : SqlHelper.select(ics, NAVIGATION_TREE_DUMP, NAVIGATION_TREE_DUMP.newParam())) {
            SitePlanTreeData nodeInfo = new SitePlanTreeData(row);
            rowMap.put(nodeInfo.nid, nodeInfo);
        }

        // create Node objects
        Map<Long, ANODE> nidNodeMap = new HashMap<Long, ANODE>();
        for (long nid : rowMap.keySet()) {
        	ANODE node = createAssetNode(rowMap.get(nid).assetId);
            
            // Log a dependency with every node (asset) we populate
            LogDep.logDep(ics, node.getId());
            
            nidNodeMap.put(nid, node);
        }

        // hook up parent-child relationships
        for (long nid : rowMap.keySet()) {
            SitePlanTreeData sptRow = rowMap.get(nid);
            ANODE node = nidNodeMap.get(nid);
            ANODE parent = nidNodeMap.get(sptRow.nparentid);
            if (parent != null) {
                node.setParent(parent);
                parent.addChild(node, sptRow.nrank);
            }

            // Stash for later. Probably won't have many duplicates so optimize
            AssetId assetId = node.getId();
            List<ANODE> a1 = nodesById.get(assetId);
            if (a1 == null) {
                a1 = Collections.singletonList(node);
                nodesById.put(assetId, a1);
            } else {
            	// NOT SURE WHAT THIS ELSE IS FOR, BUT LET'S KEEP IT UNTIL TONY EXPLAINS
            	a1.add(node);
            }
            
            // Stash for later. Probably won't have many duplicates so optimize and don't create too many lists
            /* OLD VERSION -- WHAT IS THIS ELSE FOR ???????????????????????
            AssetId assetId = node.getId();
            N[] a1 = nodesById.get(assetId);
            if (a1 == null) {
                a1 = new N[1];
                a1[0] = node;
                nodesById.put(assetId, a1);
            } else {
                N[] a2 = new N[a1.length+1];
                System.arraycopy(a1, 0, a2, 0, a1.length);
                a2[a1.length] = node;
                nodesById.put(assetId, a2);
            }
            */

        }
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

    public List<ANODE> getNav(AssetId sitePlan) {
        if (sitePlan == null) {
            throw new IllegalArgumentException("Null param not allowed");
        }

        // find the requested structure
        List<ANODE> spNodes = nodesById.get(sitePlan);
        if (spNodes == null) throw new IllegalArgumentException("Could not locate nav structure corresponding to "+sitePlan);
        if (spNodes.size() > 1) throw new IllegalStateException("Cannot have more than one site plan node with the same id in the tree");
        ANODE requestedRoot = spNodes.get(0); // never null

        // populate asset data into the structure requested
        //_populateNodes(requestedRoot); // <-- Nodes are already populated by the time they are instantiated

        // return the loaded children of the structure root
        return (List<ANODE>) requestedRoot.getChildren();
    }

    /*
    private void _populateNodes(AssetNode... emptyNodes) {

        // gather the empty nodes we care about
        Collection<AssetNode> nodesToPopulate = new HashSet<>();
        for (AssetNode unpopulatedNode : emptyNodes) {
            nodesToPopulate.add(unpopulatedNode);
            nodesToPopulate.addAll(_getDescendents(unpopulatedNode));
            nodesToPopulate.addAll(_getAncestors(unpopulatedNode));
        }

        // fill 'em up
        for (AssetNode node : nodesToPopulate) {
            AssetId id = node.getId();
            LogDep.logDep(ics, id);
            TemplateAsset data = getNodeData(id);
            if (data == null) {
                throw new IllegalStateException("Null node data returned for id " + id);
            }
            SimpleAssetNode san = _asSimpleAssetNode(node);
            san.setAsset(data);
        }
    }
    */

    /*
    private Collection<AssetNode> _getDescendents(AssetNode n) {
        Set<AssetNode> descendents = new HashSet<>();
        for (AssetNode kid : n.getChildren()) {
            descendents.add(kid);
            descendents.addAll(_getDescendents(kid));
        }
        return descendents;
    }
    */

    /*
    private Collection<AssetNode> _getAncestors(AssetNode node) {
        Set<AssetNode> ancestors = new HashSet<>();
        do {
            ancestors.add(node);
            node = node.getParent();
        } while (node != null);
        return ancestors;
    }
    */

    /**
     * We can't modify AssetNode objects, but we can modify SimpleAssetNodes. We do have a map of SimpleAssetNode
     * objects that we can look through though, so look through all of them and find the handle to the SimpleAssetNodes
     * corresponding to the input.
     * @param node asset node
     * @return asset node as simple asset node
     */
    /*
    private SimpleAssetNode _asSimpleAssetNode(AssetNode node) {
        for (SimpleAssetNode san : nodesById.get(node.getId())) {
            if (san.equals(node))
                return san;
        }
        throw new IllegalStateException("Could not find SimpleAsseNode corresponding to AssetNode: "+node);
    }
    */

    /**
     * Method to retrieve data that will be loaded into a node. Implementing classes should take care
     * to be very efficient both for cpu time as well as memory usage.
     * @param id asset ID to load
     * @return asset data in the form of a TemplateAsset, never null
     */
    //protected abstract TemplateAsset getNodeData(AssetId id);

    
    public List<ANODE> getBreadcrumb(AssetId id) {

        if (id == null) {
            throw new IllegalArgumentException("Cannot calculate breadcrumb of a null asset");
        }

        Collection<List<ANODE>> breadcrumbs = new ArrayList<>();
        for (ANODE node : nodesById.get(id)) {
            breadcrumbs.add(getBreadcrumbForNode(node));
        }

        List<ANODE> breadcrumb = chooseBreadcrumb(breadcrumbs);

        //_populateNodes(breadcrumb.toArray(new AssetNode[breadcrumb.size()])); // <-- Nodes are already populated by the time they are instantiated

        return breadcrumb;
    }

    /**
     * Get the breadcrumb corresponding to the specified node.
     *
     * Default implementation simply uses the specified node's parents.
     *
     * @param node the node whose breadcrumb needs to be calculated
     * @return the breadcrumb
     */
    protected List<ANODE> getBreadcrumbForNode(ANODE node) {
        List<ANODE> ancestors = new ArrayList<>();
        do {
            ancestors.add(node);
            node = node.getParent();
        } while (node != null);
        Collections.reverse(ancestors);
        return ancestors;
    }

    /**
     * Pick which breadcrumb to return if more than one path has been found.
     *
     * Default implementation simply returns the first one returned by the specified collection's iterator.
     *
     * @param options candidate breadcrumbs
     * @return the breadcrumb to use.
     */
    protected List<ANODE> chooseBreadcrumb(Collection<List<ANODE>> options) {
        return options.iterator().next();
    }
    
}