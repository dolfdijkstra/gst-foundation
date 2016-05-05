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

package com.fatwire.gst.foundation.wra.navigation;

import static com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate.isValidOnDate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.fatwire.gst.foundation.navigation.NavigationNode;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.navigation.support.AbstractNavigationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraUriBuilder;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * @author Dolf Dijkstra
 * 
 */
public class WraNavigationService extends AbstractNavigationService implements NavigationService {

    private static final Log LOG = LogFactory.getLog(WraNavigationService.class);
    /**
     * Name of the page subtype indicating that this page is NOT rendered on the
     * site but is instead merely used to group navigation components on the
     * site.
     */
    public static final String NAVBAR_NAME = "GSTNavName";
    /**
     * Name of the page subtype indicating that this page is a Link, meaning
     * that the content is in the unnamed association
     */
    public static final String NAVBAR_LINK = "GSTNavLink";

    /**
     * Constant containing the asset type of the GST Alias asset.
     */
    public final String GST_ALIAS_TYPE = Alias.ALIAS_ASSET_TYPE_NAME;

    private static final String CHILD_SQL = "SELECT otype,oid,nrank,nid from SitePlanTree where nparentid=? and ncode='Placed' order by nrank";
    private static final PreparedStmt CHILD_STMT = new PreparedStmt(CHILD_SQL, Arrays.asList("SitePlanTree"));

    static {

        CHILD_STMT.setElement(0, "SitePlanTree", "nparentid");

    }

    /**
     * Local instance of the AliasCoreFieldDao.
     */
    protected final AliasCoreFieldDao aliasDao;

    protected final DimensionFilterInstance dimensionFilter;
    private final Date date;

    public WraNavigationService(ICS ics, TemplateAssetAccess assetTemplate, AliasCoreFieldDao aliasDao,
            final DimensionFilterInstance dimensionFilter, Date previewDate) {
        super(ics, assetTemplate, "linktext", "path");
        this.aliasDao = aliasDao;
        this.dimensionFilter = dimensionFilter;
        this.date = previewDate;

    }

    @Override
    protected NavigationNode getNode(Row row, int level, int depth, String linkAttribute) {
        long nid = row.getLong("nid");
        long pageId = row.getLong("oid");

        AssetId pid = assetTemplate.createAssetId(row.getString("otype"), pageId);
        if (!isValidOnDate(ics, pid, date)) {
            // the input object is not valid. Abort
            if (LOG.isDebugEnabled()) {
                LOG.debug("Input asset " + pid + " is not effective.");
            }
            return null;
        }
        LogDep.logDep(ics, pid); // probably redundant call
        TemplateAsset asset = assetTemplate.read(pid, "name", "subtype", "template", pathAttribute, linkAttribute);

        final NavigationNode node = new NavigationNode();

        node.setPage(pid);
        node.setLevel(level);
        node.setPagesubtype(asset.getSubtype());
        node.setPagename(asset.asString("name"));
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(asset.asString("subtype"));
        final boolean isNavigationLink = NAVBAR_LINK.equals(asset.asString("subtype"));
        if (isNavigationPlaceholder) {
            // return node without an associated asset
        } else if (isNavigationLink) {

            Collection<AssetId> assocs;
            assocs = assetTemplate.readAssociatedAssetIds(pid, "-");
            if (dimensionFilter != null)
                assocs = dimensionFilter.filterAssets(assocs);
            for (AssetId assoc : assocs) {
                if (isValidOnDate(ics, assoc, date)) {
                    if (isGstAlias(assoc)) {
                        final Alias alias = aliasDao.getAlias(assoc);
                        node.setId(alias.getId());

                        final String url = alias.getTargetUrl() != null ? alias.getTargetUrl() : getUrl(alias
                                .getTarget());
                        final String linktext = alias.getLinkText();

                        if (url != null) {
                            node.setUrl(url);
                        }
                        if (linktext != null) {
                            node.setLinktext(linktext);
                        }
                    } else {
                        node.setId(assoc);
                        asset = assetTemplate.read(assoc, "name", "subtype", "template", pathAttribute, linkAttribute);
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
                    }
                }
            }

        } else {
            // TODO Add support for locale (date checking is already done at
            // start of this function).

            // other subtype
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

    private String getUrl(AssetId assoc) {
        TemplateAsset asset = assetTemplate.read(assoc, "name", "subtype", "template", pathAttribute);
        return getUrl(asset);
    }

    @Override
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
     * Builds up a URI for this asset, using the pathAttribute and the template
     * field of the asset
     * 
     * @param asset template asset
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
     * Return true if the asset type is a GSTAlias asset type. May be overridden
     * if customers are attempting to retrofit this class for alias-like
     * functionality that is not implemented by the GSTAlias asset type.
     * 
     * @param id asset for which a link is required
     * @return true if the asset is an alias, false if it is a web-referenceable
     *         asset
     */
    protected boolean isGstAlias(final AssetId id) {
        return GST_ALIAS_TYPE.equals(id.getType());
    }

}
