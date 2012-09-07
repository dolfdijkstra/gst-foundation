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
package com.fatwire.gst.foundation.navigation.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.query.Query;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder;
import com.fatwire.gst.foundation.navigation.NavigationNode;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.wra.WraUriBuilder;

/**
 * To retrieve the Navigation Bar data.
 * 
 * 
 * @author Dolf Dijkstra
 * @since August 31,2012
 */
public class SimpleNavigationHelper implements NavigationService {

    protected static final Log LOG = LogFactory.getLog(SimpleNavigationHelper.class);
    private final ICS ics;
    private final TemplateAssetAccess assetTemplate;

    private String linkLabelAttribute = "linktext";

    private String pathAttribute = "path";

    private static final String NAME_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE EXISTS( SELECT 1 FROM Page p ,AssetPublication ap , Publication pub WHERE p.name=? AND pub.name=? AND ap.assetid=p.id AND pub.id = ap.pubid  AND SitePlanTree.oid = p.id) AND ncode='Placed' ORDER BY nrank";

    private static final PreparedStmt NAME_STMT = new PreparedStmt(NAME_SQL, Arrays.asList("SitePlanTree", "Page",
            "AssetPublication", "Publication"));

    private static final String ID_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE SitePlanTree.oid = ? AND ncode='Placed' ORDER BY nrank";

    private static final PreparedStmt ID_STMT = new PreparedStmt(ID_SQL, Arrays.asList("SitePlanTree"));

    private static final String NODE_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE otype='Publication' AND exists (SELECT 1 FROM Publication WHERE name=? AND id=SitePlanTree.oid)";

    private static final PreparedStmt NODE_STMT = new PreparedStmt(NODE_SQL, Arrays.asList("SitePlanTree",
            "Publication"));

    private static final String CHILD_SQL = "SELECT otype,oid,nrank,nid from SitePlanTree where nparentid=? and ncode='Placed' order by nrank";
    private static final PreparedStmt CHILD_STMT = new PreparedStmt(CHILD_SQL, Arrays.asList("SitePlanTree"));

    static {
        NAME_STMT.setElement(0, "Page", "name");
        NAME_STMT.setElement(1, "Publication", "name");
        ID_STMT.setElement(0, "SitePlanTree", "oid");

        NODE_STMT.setElement(0, "Publication", "name");
        CHILD_STMT.setElement(0, "SitePlanTree", "nparentid");

    }

    /**
     * Constructor.
     * 
     * @param ics object
     */
    public SimpleNavigationHelper(final ICS ics) {
        this.ics = ics;
        this.assetTemplate = new TemplateAssetAccess(ics);

    }

    /**
     * 
     * 
     * @param ics
     * @param assetTemplate
     */
    public SimpleNavigationHelper(ICS ics, TemplateAssetAccess assetTemplate) {
        this.ics = ics;
        this.assetTemplate = assetTemplate;
    }

    /**
     * Constructor that sets the linkLabel and path attributes.
     * 
     * @param ics
     * @param assetTemplate
     * @param linkLabelAttribute
     * @param pathAttribute
     */
    public SimpleNavigationHelper(ICS ics, TemplateAssetAccess assetTemplate, String linkLabelAttribute,
            String pathAttribute) {
        if (StringUtils.isBlank(linkLabelAttribute))
            throw new IllegalArgumentException("linkLabelAttribute cannot be blank");
        if (StringUtils.isBlank(pathAttribute))
            throw new IllegalArgumentException("pathAttribute cannot be blank");

        this.ics = ics;
        this.assetTemplate = assetTemplate;
        this.pathAttribute = pathAttribute;
        this.linkLabelAttribute = linkLabelAttribute;
    }

    /**
     * @param site
     * @return the root SitePlanTree nodes for this site
     */
    public Collection<NavigationNode> getRootNodesForSite(String site) {

        return getRootNodesForSite(site, -1);

    }

    /**
     * @param site
     * @param depth
     * @param linkAttribute
     * @return
     */
    @Override
    public Collection<NavigationNode> getRootNodesForSite(String site, int depth, String linkAttribute) {
        if (StringUtils.isBlank(site))
            throw new IllegalArgumentException("site cannot be blank");
        if (StringUtils.isBlank(linkAttribute))
            throw new IllegalArgumentException("linkAttribute cannot be blank");
        StatementParam param = NODE_STMT.newParam();
        param.setString(0, site);
        Row root = SqlHelper.selectSingle(ics, NODE_STMT, param);
        if (root != null) {
            Long nid = root.getLong("nid");
            return getNodeChildren(nid, 0, depth, linkAttribute);
        } else {
            LOG.debug("No root SitePlanTree nodes found for site " + site);
        }
        return Collections.emptyList();

    }

    @Override
    public NavigationNode getNodeByName(String site, String pagename, int depth) {
        return getNodeByName(site, pagename, depth, this.linkLabelAttribute);
    }

    @Override
    public NavigationNode getNodeByName(String pagename, int depth, String linkAttribute) {
        return getNodeByName(ics.GetVar("site"), pagename, depth, this.linkLabelAttribute);
    }

    @Override
    public NavigationNode getNodeByName(String site, String pagename, int depth, String linkAttribute) {
        if (StringUtils.isBlank(site))
            throw new IllegalArgumentException("site cannot be blank");
        if (StringUtils.isBlank(pagename))
            throw new IllegalArgumentException("pagename cannot be blank");
        if (StringUtils.isBlank(linkAttribute))
            throw new IllegalArgumentException("linkAttribute cannot be blank");

        StatementParam param = NAME_STMT.newParam();
        param.setString(0, pagename);
        param.setString(1, site);

        Row root = SqlHelper.selectSingle(ics, NAME_STMT, param);
        if (root != null) {
            final NavigationNode node = getNode(root, 0, depth, linkAttribute);
            return node;
        } else {
            LOG.debug("No SitePlanTree nodes found for Page " + pagename + " in site " + site);
        }
        return null;

    }

    @Override
    public NavigationNode getNodeByName(String pagename, int depth) {

        return getNodeByName(ics.GetVar("site"), pagename, depth);
    }

    @Override
    public Collection<NavigationNode> getRootNodesForSite(int depth) {

        return getRootNodesForSite(ics.GetVar("site"));
    }

    @Override
    public Collection<NavigationNode> getRootNodesForSite(String site, int depth) {

        return getRootNodesForSite(site, depth, linkLabelAttribute);
    }

    @Override
    public NavigationNode getNodeByQuery(Query query, int depth, String linkAttribute) {
        Iterable<TemplateAsset> assets = assetTemplate.query(query);
        TemplateAsset asset;
        if (assets != null)
            asset = assets.iterator().next();
        else
            return null;
        StatementParam param = ID_STMT.newParam();
        param.setLong(0, asset.getAssetId().getId());

        Row root = SqlHelper.selectSingle(ics, ID_STMT, param);
        if (root != null) {
            final NavigationNode node = getNode(root, 0, depth, linkAttribute);
            return node;
        } else {
            LOG.debug("No SitePlanTree nodes found for Query " + query.toString());
        }
        return null;
    }

    /**
     * List all the child NavigationNode at this SitePlanTree nodeId.
     * 
     * @param nodeId the nodeId from the SitePlanTree
     * @param level the tree level depth
     * @param depth the maximum depth
     * @return
     */

    protected Collection<NavigationNode> getNodeChildren(final long nodeId, final int level, final int depth,
            String linkAttribute) {
        StatementParam param = CHILD_STMT.newParam();
        param.setLong(0, nodeId);

        IListIterable root = SqlHelper.select(ics, CHILD_STMT, param);
        List<NavigationNode> collection = new LinkedList<NavigationNode>();
        for (Row row : root) {
            final NavigationNode node = getNode(row, level, depth, linkAttribute);
            if (node != null)
                collection.add(node);

        }
        return collection;
    }

    /**
     * @param row the resultlist containing nid/oid/otype
     * @param level the current level
     * @param depth the maximum depth
     * @param linkAttribute the attribute for the link text.
     * @return
     */
    protected NavigationNode getNode(Row row, final int level, final int depth, String linkAttribute) {
        long nid = row.getLong("nid");
        long pageId = row.getLong("oid");

        AssetId pid = assetTemplate.createAssetId(row.getString("otype"), pageId);
        LogDep.logDep(ics, pid);
        TemplateAsset asset = assetTemplate.read(pid, "name", "subtype", "template", pathAttribute, linkAttribute);

        final NavigationNode node = new NavigationNode();

        node.setPage(pid);
        node.setLevel(level);
        node.setPagesubtype(asset.getSubtype());
        node.setPagename(asset.asString("name"));
        node.setId(asset.getAssetId());
        final String url = getUrl(asset);

        if (url != null) {
            node.setUrl(url); // escape by default.
        }

        final String linktext = asset.asString(linkAttribute);

        if (linktext != null) {
            node.setLinktext(linktext);
        } else {
            node.setLinktext(asset.asString("name"));
        }
        if (depth < 0 || depth > level) {
            // get the children in the Site Plan, note recursing here
            Collection<NavigationNode> children = getNodeChildren(nid, level + 1, depth, linkAttribute);
            for (final NavigationNode kid : children) {
                if (kid != null && kid.getPage() != null) {
                    node.addChild(kid);
                }
            }
        }
        return node;
    }

    /**
     * Builds up a URI for this asset, using the pathAttribute and the template
     * field of the asset
     * 
     * @param asset
     * @return the uri, xml escaped
     */
    protected String getUrl(TemplateAsset asset) {
        String template = asset.asString("template");
        String path = asset.asString(pathAttribute);
        if (StringUtils.isBlank(template)) {
            LOG.debug("Asset " + asset.getAssetId() + " does not have a valid template set.");
            return null;
        }
        if (StringUtils.isBlank(path)) {
            LOG.debug("Asset " + asset.getAssetId()
                    + " does not have a valid path set. Defaulting to a non Vanity Url.");
            return new TemplateUriBuilder(asset.getAssetId(), template).toURI(ics);
        }

        String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher",
                "ServletRequest.properties", true);
        if (!Utilities.goodString(wrapper)) {
            wrapper = "GST/Dispatcher";
        }
        String uri = new WraUriBuilder(asset.getAssetId()).wrapper(wrapper).template(template).toURI(ics);
        return StringEscapeUtils.escapeXml(uri);
    }

    /**
     * @return the linkLabelAttribute
     */
    public String getLinkLabelAttribute() {
        return linkLabelAttribute;
    }

    /**
     * @param linkLabelAttribute
     */
    public void setLinkLabelAttribute(String linkLabelAttribute) {
        this.linkLabelAttribute = linkLabelAttribute;
    }

    /**
     * @return the pathAttribute
     */
    public String getPathAttribute() {
        return pathAttribute;
    }

    /**
     * @param pathAttribute
     */
    public void setPathAttribute(String pathAttribute) {
        this.pathAttribute = pathAttribute;
    }

}
