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
package tools.gsf.navigation.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder;
import tools.gsf.navigation.NavigationNode;
import tools.gsf.navigation.NavigationService;
import com.fatwire.gst.foundation.wra.WraUriBuilder;

/**
 * To retrieve the Navigation Bar data.
 * 
 * 
 * @author Dolf Dijkstra
 * @since August 31,2012
 * 
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation (coming soon)
 * 
 */
public class SimpleNavigationHelper extends AbstractNavigationService implements NavigationService {

    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.navigation.support.SimpleNavigationHelper");

    private static final String CHILD_SQL = "SELECT otype,oid,nrank,nid from SitePlanTree where nparentid=? and ncode='Placed' order by nrank";
    private static final PreparedStmt CHILD_STMT = new PreparedStmt(CHILD_SQL, Arrays.asList("SitePlanTree"));

    static {

        CHILD_STMT.setElement(0, "SitePlanTree", "nparentid");

    }

    /**
     * Constructor.
     * 
     * @param ics object
     */
    public SimpleNavigationHelper(final ICS ics) {
        super(ics);

    }

    /**
     * 
     * 
     * @param ics Content Server context object
     * @param assetTemplate asset template
     */
    public SimpleNavigationHelper(final ICS ics, final TemplateAssetAccess assetTemplate) {
        super(ics, assetTemplate);
    }

    /**
     * @param ics Content Server context object
     * @param assetTemplate asset template
     * @param linkLabelAttribute string value for link label attribute
     * @param pathAttribute string value for path attribute
     */
    public SimpleNavigationHelper(final ICS ics, final TemplateAssetAccess assetTemplate,
            final String linkLabelAttribute, final String pathAttribute) {
        super(ics, assetTemplate, linkLabelAttribute, pathAttribute);

    }

    @Override
    protected NavigationNode getNode(final Row row, final int level, final int depth, final String linkAttribute) {
        final long nid = row.getLong("nid");
        final long pageId = row.getLong("oid");

        final AssetId pid = assetTemplate.createAssetId(row.getString("otype"), pageId);
        LogDep.logDep(ics, pid);
        final TemplateAsset asset = assetTemplate
                .read(pid, "name", "subtype", "template", pathAttribute, linkAttribute);

        final NavigationNode node = new NavigationNode();

        final String p = ics.GetVar("p");
        if (StringUtils.isNotBlank(p)) {
            if (pid.getId() == Long.parseLong(p)) {
                node.setActive(true);
            }
        }

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
            final Collection<NavigationNode> children = getNodeChildren(nid, level + 1, depth, linkAttribute);
            for (final NavigationNode kid : children) {
                if (kid != null && kid.getPage() != null) {
                    node.addChild(kid);
                }
            }
        }
        return node;
    }

    @Override
    protected Collection<NavigationNode> getNodeChildren(final long nodeId, final int level, final int depth,
            final String linkAttribute) {
        final StatementParam param = CHILD_STMT.newParam();
        param.setLong(0, nodeId);

        final IListIterable root = SqlHelper.select(ics, CHILD_STMT, param);
        final List<NavigationNode> collection = new LinkedList<NavigationNode>();
        for (final Row row : root) {
            final NavigationNode node = getNode(row, level, depth, linkAttribute);
            if (node != null) {
                collection.add(node);
            }

        }
        return collection;
    }

    /**
     * Builds up a URI for this asset, using the pathAttribute and the template
     * field of the asset
     * 
     * @param asset template asset
     * @return the uri, xml escaped
     */
    protected String getUrl(final TemplateAsset asset) {
        final String template = asset.asString("template");
        final String path = asset.asString(pathAttribute);
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
        final String uri = new WraUriBuilder(asset.getAssetId()).wrapper(wrapper).template(template).toURI(ics);
        return StringEscapeUtils.escapeXml(uri);
    }

}
