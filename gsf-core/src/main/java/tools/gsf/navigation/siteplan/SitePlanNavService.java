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
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;

import tools.gsf.facade.assetapi.AssetIdUtils;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.facade.runtag.render.LogDep;
import tools.gsf.facade.sql.IListIterable;
import tools.gsf.facade.sql.Row;
import tools.gsf.facade.sql.SqlHelper;
import tools.gsf.navigation.AssetNode;
import tools.gsf.navigation.ConfigurableNode;
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
 * This NavService implementation REQUIRES that the SitePlan models your navigational
 * structures after the following design:
 * 
 * SitePlan root node (SiteNavigation asset)
 *        |
 *        ----------- Nav Structure "A" Placeholder (Page asset)
 *        |                     |
 *        |                     |----------- Webpage A.1 (Page asset)
 *        |                     |                  |
 *        |                     |                  |----------- Webpage A.1.1 (Page asset)
 *        |                     |                                    |----------- (...) (Page assets)
 *        |                     |
 *        |                     |----------- Webpage A.2 (Page asset)
 *        |                     |                  |
 *        |                     |                  |----------- (...) (Page assets)
 *        |                     |
 *        |                     |----------- Webpage A.3 (Page asset)
 *        |                     |                  |
 *        |                     |                  |----------- (...) (Page assets)
 *        |
 *        ----------- Nav Structure "B" Placeholder (Page asset)
 *        |                     |
 *        |                     |----------- Webpage B.1 (Page asset)
 *        |                     |                  |
 *        |                     |                  |----------- (...) (Page assets)
 *        |                     |                  
 *        |                     |----------- Webpage B.2 (Page asset)
 *        |                     |                  |
 *        |                     |                  |----------- (...) (Page assets)
 *        |                     |                                    
 *        |                     |----------- Webpage A.3 (Page asset)
 *        |                                        |
 *        |                                        |----------- (...) (Page assets)
 *        |
 *        ----------- (...) (Other nav structures)
 *
 * ... where:
 * 
 * - SiteNavigation nodes will always be excluded from a Webpage node's definitive breadcrumb.
 * - SiteNavigation nodes are not part of any nav structure.
 * - Nav Structure Placeholder nodes will always be excluded from a Webpage node's definitive breadcrumb.
 * - Nav Structure Placeholder are not part of a nav structure, they are just the entry point to it.
 *
 *
 *
 *
 * 
 * @author Tony Field
 * @since 2016-07-06
 */
public abstract class SitePlanNavService<N extends AssetNode<N> & ConfigurableNode<N>> implements NavService<N, AssetId, AssetId> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SitePlanNavService.class);
	
	private boolean _initialized;

    private final ICS ics;
    private final TemplateAssetAccess dao;
    private final String sitename;
	private final Long pubId;
    private final Map<AssetId, List<N>> nodesById = new HashMap<>();

    // Sure, we could join with PUBLICATION on the NAVIGATION_TREE_DUMP, but
    // this way we leverage cache further.
    private final static PreparedStmt FIND_PUBID = new PreparedStmt(
            "select id from Publication where name = ?",
            Arrays.asList("Publication"));

    private final static PreparedStmt NAVIGATION_TREE_DUMP = new PreparedStmt(
            "select spt.* from SITEPLANTREE spt, ASSETPUBLICATION ap " +
            "where " +
            "spt.ncode = 'Placed' and " +
            "spt.otype = ap.assettype and " +
            "spt.oid = ap.assetid and " +
            "((ap.pubid = ?) OR (ap.pubid = 0)) " + // Pages cannot be shared across sites in 12c, but let's play it safe, just in case that changes
            "order by spt.nparentid, spt.nrank",
            Arrays.asList("page", "siteplantree", "assetpublication", "Page", "SitePlanTree", "AssetPublication", "PAGE", "SITEPLANTREE", "ASSETPUBLICATION"));
    
    static {
    	FIND_PUBID.setElement(0, "Publication", "name");
    	NAVIGATION_TREE_DUMP.setElement(0, "ASSETPUBLICATION", "pubid");
    }
    
    private final Long _getPubId(String sitename) {
        final StatementParam params = FIND_PUBID.newParam();
        params.setString(0, sitename);

        LOG.debug("Executing query to find out pubid for site name {}", sitename);
                
        IListIterable results = SqlHelper.select(this.ics, FIND_PUBID, params);
        if (results.size() == 0) {
        	LOG.warn("There is no Publication whose name is '{}'", sitename);
        	return null;
        } else if (results.size() > 1) {
        	throw new IllegalStateException("There cannot be 2+ sites (publications) in WCS with the same name, yet there seem to be " + results.size() + " sites whose name is '" + sitename + "'");
        } else {
        	Row row = results.iterator().next();
        	return row.getLong("id");
        }
    }
    
    /**
     * For a given (Page) AssetId, creates an object whose type matches this method's return type
     * (Generic), which will be added to the nav structure produced by this NavService implementation.
     * 
     * @param assetId The ID of the asset you want to create an AssetNode instance for 
     * @return An object whose type matches this method's return type (Generic type N)
     */
    protected abstract N createAssetNode(AssetId assetId);
    
    protected TemplateAssetAccess getTemplateAssetAccess() {
    	return this.dao;
    }
    
    protected String getSitename() {
    	return this.sitename;
    }
    
    public SitePlanNavService(ICS ics, TemplateAssetAccess dao) {
    	this(ics, dao, null);
    }
    
    public SitePlanNavService(ICS ics, TemplateAssetAccess dao, String theSitename) {
        this.ics = ics;
        this.dao = dao;

        if (!Utilities.goodString(theSitename)) {
    		// Fallback to ICS variable "site", if such
        	theSitename = ics.GetVar("site");
        	if (!Utilities.goodString(theSitename)) {
        		throw new IllegalStateException("Missing argument sitename. Nav structure cannot be built unless you specify the name of the site (publication) it is for.");
        	}
    	}
		Long aPubId = _getPubId(theSitename);
		if (aPubId == null) {
			throw new IllegalStateException("Cannot determine pubid for site '" + theSitename + "'. Nav structure cannot be built unless you specify the (valid) name of the site (publication) it is for.");
		}
        
		this.pubId = aPubId;
		this.sitename = theSitename;
    }

    private synchronized void _initializeNavStructure() {
    	if (!this._initialized) {
	        StatementParam params = NAVIGATION_TREE_DUMP.newParam();
	        params.setLong(0, pubId);
	
	        // read the site plan tree in one massive query
	        Map<Long, SitePlanTreeData> rowMap = new HashMap<>();
	        Map<Long, List<SitePlanTreeData>> childrenMap = new HashMap<>();
	
	        LOG.debug("Executing SitePlan query for gathering data for nav service...");
	                
	        for (Row row : SqlHelper.select(ics, NAVIGATION_TREE_DUMP, params)) {
	            SitePlanTreeData nodeInfo = new SitePlanTreeData(row);
	            LOG.debug("Processing SitePlan row: {}", nodeInfo);
	            rowMap.put(nodeInfo.nid, nodeInfo);
	            LOG.debug("Added row {} to SitePlan rows map under key {}", nodeInfo, nodeInfo.nid);
	            List<SitePlanTreeData> children = childrenMap.get(nodeInfo.nparentid);
	            if (children == null) {
	            	// Initialize the list of children for the current row's parent
	            	children = new ArrayList<SitePlanTreeData>();
	            	childrenMap.put(nodeInfo.nparentid, children);
	            }
	            // Add the current row to its parent's list of children  
	            children.add(nodeInfo);
	            LOG.debug("Added SPT row {} to the list of children of SPT (parent) row {}. That list now looks like this: {}", nodeInfo, nodeInfo.nparentid, children);
	        }
	
	        // create Node objects
	        Map<Long, N> nidNodeMap = new HashMap<Long, N>();
	        for (long nid : rowMap.keySet()) {
	        	LOG.debug("Will invoke createAssetNode for asset id {}", rowMap.get(nid).assetId);
	        	N node = createAssetNode(rowMap.get(nid).assetId);
	        	LOG.debug("AssetNode created for asset {}: {}", rowMap.get(nid).assetId, node);
	            
	            // Log a dependency with every node (asset) we populate
	            LogDep.logDep(ics, node.getId());
	        	LOG.debug("Logged dependency for asset {} inside nav service...", rowMap.get(nid).assetId);
	            
	            nidNodeMap.put(nid, node);
	            LOG.debug("Added node {} to nodes map under key {}", node, nid);
	            
	            // Stash for later. Probably won't have many duplicates so optimize
	            AssetId assetId = node.getId();
	            List<N> a1 = nodesById.get(assetId);
	            if (a1 == null) {
	            	a1 = Stream.of(node).collect(Collectors.toList());
	                nodesById.put(assetId, a1);
	            } else {
	            	a1.add(node);
	            }
	            
	            LOG.debug("nodesById map: {}", nodesById);
	
	        }
	
	        // hook up parent-child relationships, starting from the list
	        // of nodes with a parent
	        for (long nparentid : childrenMap.keySet()) {
	        	LOG.debug("Processing parent-child relationships for SitePlanTree row with nid = {}", nparentid);
	        	N parent = nidNodeMap.get(nparentid);
	        	if (parent != null) {
	        		LOG.debug("AssetNode for nid {} is: {}", nparentid, parent);
	        		List<SitePlanTreeData> children = childrenMap.get(nparentid);
	        		if (children != null) {
	        			LOG.debug("List of children for SPT whose nid = {} is: {}", nparentid, children);
	        			for (SitePlanTreeData childRow : children) {
		        			N child = nidNodeMap.get(childRow.nid);
		        			if (child != null) {
		        				parent.addChild(child);
		        				child.setParent(parent);
		        				LOG.debug("Bound together parent node {} and child node {} as per SPT entry {}", parent, child, childRow);  
		        			} else {
		        				LOG.warn("There could be a problem here... we registered SPT row {} as a child of parent row {} but we did not instantiate a node for that child?", childRow, nparentid);
		        			}
		        		}
	        		} else {
	        			LOG.warn("Not sure how we ended up with a a parent node in the children nodes map with no children whatsoever.");
	        		}
	        	} else {
	        		LOG.warn("We have a list of children for SPT entry whose nid = {}. However, there is not a node matching that SPT entry's asset ({}). This is a bit weird, but also legitimate (for instance, the query excludes SiteNavigation assets)", nparentid, rowMap.get(nparentid));
	        	}
	        }
	        
	        this._initialized = true;
    	} else {
    		LOG.debug("Nav Structure had been already initialized for NavService instance {}", this);
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

    public List<N> getNav(AssetId sitePlan) {
        if (sitePlan == null) {
            throw new IllegalArgumentException("Null param not allowed");
        }

        // Initialize the nav structure
        _initializeNavStructure();

        // find the requested structure
        List<N> spNodes = nodesById.get(sitePlan);
        if (spNodes == null) throw new IllegalArgumentException("Could not locate nav structure corresponding to "+sitePlan);
        if (spNodes.size() > 1) throw new IllegalStateException("Cannot have more than one site plan node with the same id in the tree");
        N requestedRoot = spNodes.get(0); // never null

        // return the loaded children of the structure root
        return (List<N>) requestedRoot.getChildren();
    }
    
    public List<N> getBreadcrumb(AssetId id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot calculate breadcrumb of a null asset");
        }
        
        // Initialize the nav structure
        _initializeNavStructure();

        Collection<BreadcrumbCandidate<N>> breadcrumbs = new ArrayList<>();
        LOG.debug("Obtaining all nodes whose ID matches {}", id);
        List<N> nodes = nodesById.get(id);
        if (nodes != null) {
        	// Build all possible breadcrumbs
	        for (N node : nodesById.get(id)) {
	            breadcrumbs.add(new BreadcrumbCandidate<N>(getBreadcrumbForNode(node)));
	        }
	        
	        // Choose the preferred breadcrumb. Users may override this method
	        // to implement their own choosing logic.
	        List<N> breadcrumb = chooseBreadcrumb(breadcrumbs);
	        LOG.debug("This is the preferred breadcrumb for ID {}: {}", id, breadcrumb);
	        	        
	        return breadcrumb;
        } else {
        	LOG.info("Didn't find any node in this nav structure whose ID matched {}, thus a breadcrumb cannot be calculated. These are all available nodes: {}", id, this.nodesById);
        	LOG.debug("Node not found, getBreadcrumb will return null");
        	return null;
        }

        
    }

    /**
     * Get the breadcrumb corresponding to the specified node.
     *
     * Default implementation simply uses the specified node's parents.
     *
     * @param node the node whose breadcrumb needs to be calculated
     * @return the breadcrumb
     */
    protected List<N> getBreadcrumbForNode(N node) {
        List<N> ancestors = new ArrayList<>();
        do {
            ancestors.add(node);
            node = node.getParent();
        } while (node != null);
        Collections.reverse(ancestors);
        return ancestors;
    }

    /**
     * Determines a node's "preferred" breadcrumb from a list of "candidates".
     *
     * This default implementation simply returns the first one returned by the specified
     * collection's iterator.
     * 
     * However, you may override this method in order to implement your own ad-hoc logic for
     * determining the preferred breadcrumb.
     * 
     * The candidates passed into this method have already parsed the raw breadcrumb so to split it
     * up in the due SiteNavigation node, Nav Structure Placeholder node and the clean breadcrumb path.
     * 
     * @param candidates The candidates you get to choose the preferred breadcrumb from.
     * @return The preferred breadcrumb (list of nodes).
     */
    protected List<N> chooseBreadcrumb(Collection<BreadcrumbCandidate<N>> candidates) {
        return candidates.iterator().next().getBreadcrumb();
    }
    
    protected static class BreadcrumbCandidate<N extends AssetNode<N>> {
    	private List<N> breadcrumb;
    	private N siteNavigation;
    	private N navStructurePlaceholder;
    	
    	public BreadcrumbCandidate(List<N> rawBreadcrumb) {
    		if (rawBreadcrumb.size() < 2) {
    			throw new IllegalArgumentException("Breadcrumb candidate is not valid, insufficient nodes.");
    		}
    		this.siteNavigation = rawBreadcrumb.get(0);
    		if (!this.siteNavigation.getId().getType().equals("SiteNavigation")) {
    			throw new IllegalArgumentException("Breadcrumb candidate is not valid, first node must be the SiteNavigation node, got this instead: " + this.siteNavigation);
    		}
    		this.navStructurePlaceholder = rawBreadcrumb.get(1);
    		if (!this.navStructurePlaceholder.getId().getType().equals("Page")) {
    			throw new IllegalArgumentException("Breadcrumb candidate is not valid, second node must be the Nav Structure Placeholder (Page) node, got this instead: " + this.navStructurePlaceholder);
    		}
    		this.breadcrumb = rawBreadcrumb.subList(2, rawBreadcrumb.size());
    	}
    	
    	public N getNavStructurePlaceholder() {
    		return this.navStructurePlaceholder;
    	}
    	
    	public List<N> getBreadcrumb() {
    		return this.breadcrumb;
    	}
    }
    
}