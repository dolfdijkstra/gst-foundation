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
package tools.gsf.navigation.siteplan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import tools.gsf.facade.assetapi.AssetIdUtils;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.facade.runtag.render.LogDep;
import tools.gsf.facade.sql.Row;
import tools.gsf.facade.sql.SqlHelper;
import tools.gsf.navigation.AssetNode;
import tools.gsf.navigation.NavService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple navigation service implementation that loads objects from the Site Plan.
 * 
 * Reads the full site plan tree in one query. It does not filter assets by site.
 * 
 * Nodes are instantiated via a dedicated method where you can load any data that
 * is required; you get to (define and) use your own AssetNode implementation.
 *
 * 
 * @author Tony Field
 * @since 2016-07-06
 */
public abstract class SitePlanNavService<ANODE extends AssetNode<ANODE>> implements NavService<ANODE, AssetId, AssetId> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SitePlanNavService.class);

    private final ICS ics;
    private final TemplateAssetAccess dao;
    private final Map<AssetId, List<ANODE>> nodesById = new HashMap<>();

    private static final PreparedStmt NAVIGATION_TREE_DUMP = new PreparedStmt(
            "select * from SITEPLANTREE where ncode = 'Placed' and otype != 'SiteNavigation'",
            Arrays.asList("page", "siteplantree", "Page", "SitePlanTree", "PAGE", "SITEPLANTREE"));
    
    protected abstract ANODE createAssetNode(AssetId assetId);
    
    protected TemplateAssetAccess getTemplateAssetAccess() {
    	return this.dao;
    }
    
    public SitePlanNavService(ICS ics, TemplateAssetAccess dao) {
        this.ics = ics;
        this.dao = dao;

        // read the site plan tree in one massive query
        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();

        LOG.debug("Executing SitePlan query for gathering data for nav service...");
        
        for (Row row : SqlHelper.select(ics, NAVIGATION_TREE_DUMP, NAVIGATION_TREE_DUMP.newParam())) {
            SitePlanTreeData nodeInfo = new SitePlanTreeData(row);
            LOG.debug("Processing SitePlan row: {}", row);
            rowMap.put(nodeInfo.nid, nodeInfo);
            LOG.debug("Added row {} to SitePlan rows map under key {}", row, nodeInfo.nid);
        }

        // create Node objects
        Map<Long, ANODE> nidNodeMap = new HashMap<Long, ANODE>();
        for (long nid : rowMap.keySet()) {
        	LOG.debug("Will invoke createAssetNode for asset id {}", rowMap.get(nid).assetId);
        	ANODE node = createAssetNode(rowMap.get(nid).assetId);
        	LOG.debug("AssetNode created for asset {}: {}", rowMap.get(nid).assetId, node);
            
            // Log a dependency with every node (asset) we populate
            LogDep.logDep(ics, node.getId());
        	LOG.debug("Logged dependency for asset {} inside nav service...", rowMap.get(nid).assetId);
            
            nidNodeMap.put(nid, node);
            LOG.debug("Added node {} to nodes map under key {}", node, nid);
        }

        // hook up parent-child relationships
        for (long nid : rowMap.keySet()) {
            SitePlanTreeData sptRow = rowMap.get(nid);
            LOG.debug("Processing parent-child relationships for SitePlanTree row {}", sptRow);
            ANODE node = nidNodeMap.get(nid);
            ANODE parent = nidNodeMap.get(sptRow.nparentid);
            LOG.debug("SPT row {} concerning asset {} refers to parent {}", nid, node.getId(), sptRow.nparentid);
            if (parent != null) {
            	LOG.debug("Found parent node {} for asset {}", parent, node.getId());
                node.setParent(parent);
                parent.addChild(node, sptRow.nrank);
            } else {
            	LOG.debug("No parent node found for asset {} using nid {}", node.getId(), sptRow.nparentid);
            }

            // Stash for later. Probably won't have many duplicates so optimize
            AssetId assetId = node.getId();
            List<ANODE> a1 = nodesById.get(assetId);
            if (a1 == null) {
            	a1 = Stream.of(node).collect(Collectors.toList());
                nodesById.put(assetId, a1);
            } else {
            	a1.add(node);
            }
            
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

        // return the loaded children of the structure root
        return (List<ANODE>) requestedRoot.getChildren();
    }
    
    public List<ANODE> getBreadcrumb(AssetId id) {

        if (id == null) {
            throw new IllegalArgumentException("Cannot calculate breadcrumb of a null asset");
        }

        Collection<List<ANODE>> breadcrumbs = new ArrayList<>();
        for (ANODE node : nodesById.get(id)) {
            breadcrumbs.add(getBreadcrumbForNode(node));
        }

        List<ANODE> breadcrumb = chooseBreadcrumb(breadcrumbs);

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