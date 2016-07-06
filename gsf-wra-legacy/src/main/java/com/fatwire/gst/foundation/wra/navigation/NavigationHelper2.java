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
package com.fatwire.gst.foundation.wra.navigation;

import static com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate.isValidOnDate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetClosure;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.DateFilterClosure;
import com.fatwire.gst.foundation.facade.assetapi.asset.PreviewContext;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.fatwire.gst.foundation.navigation.NavigationNode;
import com.fatwire.gst.foundation.navigation.NavigationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraUriBuilder;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * Used to retrieve the Navigation Bar data. See the description of
 * {@link #getSitePlan(String)} for more details.
 * 
 * 
 * @author Dolf Dijkstra
 * @since Jun 8, 2012
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation (coming soon)
 */
public class NavigationHelper2 extends AbstractNavigationHelper {
    /**
     * Constructor. Initializes assetEffectiveDate to null.
     * 
     * @param ics object
     */
    public NavigationHelper2(final ICS ics) {
        super(ics);

    }

    public NavigationHelper2(ICS ics, TemplateAssetAccess assetTemplate, WraCoreFieldDao wraDao,
            AliasCoreFieldDao aliasDao) {
        super(ics, assetTemplate, wraDao, aliasDao);
    }

    /**
     * Called from public {@link #getSitePlan(int, AssetId)}. See that
     * function's description for details
     * 
     * @param depth the depth of the tree to retrieve, -1 for unlimited depth.
     * @param pageId id of the page asset
     * @param level starting level number when traversing the site plan tree
     * @return NavNode of the site plan tree
     */
    @Override
    protected NavigationNode getSitePlan(final int depth, final AssetId pageId, final int level,
            final DimensionFilterInstance dimensionFilter) {
        // check the start/end date of the page asset
        LogDep.logDep(ics, pageId);
        if (!isValidOnDate(ics, pageId, assetEffectiveDate)) {
            // the input object is not valid. Abort
            if (LOG.isDebugEnabled()) {
                LOG.debug("Input asset " + pageId + " is not effective on " + assetEffectiveDate);
            }
            return null;
        }

        // determine if it's a wra, a placeholder or an alias

        final AssetData pageData = AssetDataUtils.getAssetData(ics, pageId, "subtype", "name");
        final String subtype = AttributeDataUtils.asString(pageData.getAttributeData("subtype"));
        final String name = AttributeDataUtils.asString(pageData.getAttributeData("name"));
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);
        final NavigationNode node = new NavigationNode();

        node.setPage(pageId);
        node.setLevel(level);
        node.setPagesubtype(subtype);
        node.setPagename(name);

        if (isNavigationPlaceholder) {
            // no link if it's just a placeholder
        } else if (NAVBAR_LINK.equals(subtype)) {
            // add the WRA asset to the node
            final AssetClosure closure = new NodeWraAssetClosure(node);
            final DateFilterClosure dateFilterClosure = new DateFilterClosure(PreviewContext.getPreviewDate(ics,
                    assetEffectiveDate), closure);

            // retrieve the unnamed association(s) based on date filter
            if (dimensionFilter == null) {
                assetTemplate.readAssociatedAssets(pageId, "-", dateFilterClosure);
            } else {

                final Collection<AssetId> associatedWraList = assetTemplate.readAssociatedAssetIds(pageId, "-");
                for (final AssetId child : dimensionFilter.filterAssets(associatedWraList)) {
                    assetTemplate.readAsset(child, dateFilterClosure);
                }
            }

        } else {
            // not a GSTNavLink, probably 11g page
            final AssetClosure closure = new NodeNoneWraAssetClosure(node, "linktext");
            final DateFilterClosure dateFilterClosure = new DateFilterClosure(PreviewContext.getPreviewDate(ics,
                    assetEffectiveDate), closure);

            // retrieve the unnamed association(s) based on date filter
            if (dimensionFilter == null) {
                assetTemplate.readAsset(pageId, dateFilterClosure);
            } else {
                for (final AssetId child : dimensionFilter.filterAssets(Collections.singleton(pageId))) {
                    assetTemplate.readAsset(child, dateFilterClosure);
                }
            }

        }

        if (depth < 0 || depth > level) {
            // get the children in the Site Plan
            final List<AssetId> childrenIDs = ListPages.getChildPages(ics, pageId.getId());
            for (final AssetId aid : childrenIDs) {
                // note recursing here
                final NavigationNode kidInfo = getSitePlan(depth, aid, level + 1, dimensionFilter);
                if (kidInfo != null && kidInfo.getPage() != null) {
                    node.addChild(kidInfo);
                }
            }
        }
        return node;
    }

    class NodeWraAssetClosure implements AssetClosure {

        private final NavigationNode node;

        public NodeWraAssetClosure(final NavigationNode node) {
            this.node = node;
        }

        @Override
        public boolean work(final AssetData asset) {
            final AssetId id = asset.getAssetId();
            node.setId(id);
            if (isGstAlias(id)) {
                decorateAsAlias(id, node);
            } else {
                decorateAsWra(id, node);
            }
            return false; // needs to return only one node
        }

        protected void decorateAsAlias(final AssetId id, final NavigationNode node) {
            final Alias alias = aliasDao.getAlias(id);
            final String url = getUrlForAlias(alias);
            final String linktext = alias.getLinkText();
            if (url != null) {
                node.setUrl(url);
            }
            if (linktext != null) {
                node.setLinktext(linktext);
            }

        }

        protected void decorateAsWra(final AssetId id, final NavigationNode node) {

            final WebReferenceableAsset wra = wraDao.getWra(id);
            final String url = getUrlForWra(wra);
            final String linktext = wra.getLinkText();
            if (url != null) {
                node.setUrl(url);
            }
            if (linktext != null) {
                node.setLinktext(linktext);
            }
        }

        /**
         * Get the URL for the alias.
         * 
         * For external links, the targeturl attribute is rendered.
         * 
         * For Aliases that refer to another WRA, the alias is allowed to
         * override any WRA fields. For instance, the path, and the template can
         * be overridden by an alias for a WRA (though the template in the Alias
         * had better be typeless, or a template of the same name must exist in
         * the WRA's asset type or there will be a problem).
         * 
         * @param alias Alias bean, which of course is also a WRA.
         * @return url
         */
        protected String getUrlForAlias(final Alias alias) {
            if (alias.getTargetUrl() != null) {
                return alias.getTargetUrl();
            } else {
                return getUrlForWra(alias);
            }
        }

        /**
         * Get the URL to use for the web-referenceable asset.
         * 
         * @param wra WebReferenceableAsset bean
         * @return url
         */
        protected String getUrlForWra(final VanityAsset wra) {
            if (wra.getTemplate() == null || wra.getTemplate().length() == 0) {
                LOG.warn("Asset " + wra + " does not have a valid template set.");
                return null;
            }
            String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher",
                    "ServletRequest.properties", true);
            if (!Utilities.goodString(wrapper)) {
                wrapper = "GST/Dispatcher";
            }
            return new WraUriBuilder(wra, wrapper).toURI(ics);

        }

    }

    class NodeNoneWraAssetClosure implements AssetClosure {

        private final NavigationNode node;
        private final String linkTextAttribute;

        public NodeNoneWraAssetClosure(final NavigationNode node, String linkTextAttribute) {
            this.node = node;
            this.linkTextAttribute = linkTextAttribute;

        }

        @Override
        public boolean work(final AssetData asset) {
            final AssetId id = asset.getAssetId();
            node.setId(id);
            decorateAsNoneWra(id, node);
            return false; // needs to return only one node
        }

        protected void decorateAsNoneWra(final AssetId id, final NavigationNode node) {
            TemplateAsset asset = assetTemplate.read(id);
            final String url = getUrl(asset);

            final String linktext = asset.asString(linkTextAttribute);

            if (url != null) {
                node.setUrl(url);
            }
            if (linktext != null) {
                node.setLinktext(linktext);
            }
        }

        private String getUrl(TemplateAsset asset) {
            String template = asset.asString("template");
            if (StringUtils.isNotBlank(template)) {
                LOG.warn("Asset " + asset.getAssetId() + " does not have a valid template set.");
                return null;
            }
            String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher",
                    "ServletRequest.properties", true);
            if (!Utilities.goodString(wrapper)) {
                wrapper = "GST/Dispatcher";
            }
            return new WraUriBuilder(asset.getAssetId()).wrapper(wrapper).template(template).toURI(ics);

        }

    }

}
