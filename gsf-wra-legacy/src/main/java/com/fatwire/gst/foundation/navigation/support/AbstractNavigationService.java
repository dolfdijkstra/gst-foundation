/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.query.Query;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.navigation.NavigationNode;
import com.fatwire.gst.foundation.navigation.NavigationService;

/**
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation (coming soon)
 *
 */
public abstract class AbstractNavigationService implements NavigationService {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.navigation.support.AbstractNavigationService");

    private static final String NODE_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE otype='Publication' AND exists (SELECT 1 FROM Publication WHERE name=? AND id=SitePlanTree.oid)";

    private static final PreparedStmt NODE_STMT = new PreparedStmt(NODE_SQL, Arrays.asList("SitePlanTree",
            "Publication"));

    private static final String NAME_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE EXISTS( SELECT 1 FROM Page p ,AssetPublication ap , Publication pub WHERE p.name=? AND pub.name=? AND ap.assetid=p.id AND pub.id = ap.pubid  AND SitePlanTree.oid = p.id) AND ncode='Placed' ORDER BY nrank";

    private static final PreparedStmt NAME_STMT = new PreparedStmt(NAME_SQL, Arrays.asList("SitePlanTree", "Page",
            "AssetPublication", "Publication"));

    private static final String ID_SQL = "SELECT nid,oid,otype FROM SitePlanTree WHERE SitePlanTree.oid = ? AND ncode='Placed' ORDER BY nrank";

    private static final PreparedStmt ID_STMT = new PreparedStmt(ID_SQL, Arrays.asList("SitePlanTree"));

    static {
        NODE_STMT.setElement(0, "Publication", "name");

        NAME_STMT.setElement(0, "Page", "name");
        NAME_STMT.setElement(1, "Publication", "name");

        ID_STMT.setElement(0, "SitePlanTree", "oid");

    }

    protected final ICS ics;

    protected final TemplateAssetAccess assetTemplate;
    protected String linkLabelAttribute = "linktext";
    protected String pathAttribute = "path";

    protected AbstractNavigationService(ICS ics) {
        this(ics, new TemplateAssetAccess(ics));
    }

    protected AbstractNavigationService(ICS ics, TemplateAssetAccess assetTemplate) {
        super();
        this.ics = ics;
        this.assetTemplate = assetTemplate;
    }

    /**
     * Constructor that sets the linkLabel and path attributes.
     * 
     * @param ics Content Server context object
     * @param assetTemplate template asset access
     * @param linkLabelAttribute link label attribute string
     * @param pathAttribute path attribute string
     */
    protected AbstractNavigationService(ICS ics, TemplateAssetAccess assetTemplate, String linkLabelAttribute,
            String pathAttribute) {
        this.ics = ics;
        this.assetTemplate = assetTemplate;
        if (StringUtils.isBlank(linkLabelAttribute))
            throw new IllegalArgumentException("linkLabelAttribute cannot be blank");
        if (StringUtils.isBlank(pathAttribute))
            throw new IllegalArgumentException("pathAttribute cannot be blank");

        this.pathAttribute = pathAttribute;
        this.linkLabelAttribute = linkLabelAttribute;
    }

    /**
     * @param site site to run process over
     * @return the root SitePlanTree nodes for this site
     */
    public Collection<NavigationNode> getRootNodesForSite(String site) {

        return getRootNodesForSite(site, -1);

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
    public NavigationNode getNodeByName(String site, String pagename, int depth) {
        return getNodeByName(site, pagename, depth, this.linkLabelAttribute);
    }

    @Override
    public NavigationNode getNodeByName(String pagename, int depth, String linkAttribute) {
        return getNodeByName(ics.GetVar("site"), pagename, depth, this.linkLabelAttribute);
    }

    @Override
    public NavigationNode getNodeByName(String pagename, int depth) {

        return getNodeByName(ics.GetVar("site"), pagename, depth);
    }

    /**
     * @param site site to run process over
     * @param depth depth to return
     * @param linkAttribute link attribute
     * @return collection of navigation nodes
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
     * @param linkAttribute the attribute to use for the link text
     * @return collection of navigation nodes
     */

    protected abstract Collection<NavigationNode> getNodeChildren(long nodeId, int level, int depth,
            String linkAttribute);

    /**
     * @param row the resultlist containing nid/oid/otype
     * @param level the current level
     * @param depth the maximum depth
     * @param linkAttribute the attribute for the link text.
     * @return the NavigationNode
     */
    protected abstract NavigationNode getNode(Row row, int level, int depth, String linkAttribute);

    /**
     * @return the linkLabelAttribute
     */
    public String getLinkLabelAttribute() {
        return linkLabelAttribute;
    }

    /**
     * @param linkLabelAttribute string value of link label attribute
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
     * @param pathAttribute path attribute string
     */
    public void setPathAttribute(String pathAttribute) {
        this.pathAttribute = pathAttribute;
    }

}
