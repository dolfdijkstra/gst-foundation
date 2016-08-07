/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.runtag.asset;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.TreeHelper;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import com.openmarket.xcelerate.publish.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


/**
 * Utilities for working efficiently with the AssetRelationTree.
 *
 * @author Tony Field
 * @since Jun 7, 2009
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class AssetRelationTreeUtils {

    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.runtag.asset.AssetRelationTreeUtils");

    /**
     * Get all of the parent assets in the AssetRelationTree for the specified
     * asset. Association name is required, but expectedParentType is an
     * optional filter argument.
     * <p>
     * Does not record any asset dependencies
     *
     * @param ics                ICS context
     * @param log                logger. May be null.
     * @param child              child asset id
     * @param expectedParentType asset type of the parent to be returned. If
     *                           null, type of parent is irrelevant.
     * @param associationName    name of association to use while looking for
     *                           parents. may not be null
     * @return list of parents, never null.
     * @see #getParents(ICS, AssetId, String[])
     */
    public static List<AssetId> getAssetRelationTreeParents(ICS ics, Logger log, AssetId child, String expectedParentType, String associationName) {
        FTValList vl = new FTValList();
        vl.setValString("ftcmd", "findnode");
        vl.setValString("treename", "AssetRelationTree");
        vl.setValString("where", "oid");
        vl.setValString("oid", Long.toString(child.getId()));
        if (ics.TreeManager(vl)) {
            int errno = ics.GetErrno();
            if (errno < 0) {
                switch (errno) {
                    case -111:
                        if (log != null && log.isTraceEnabled()) {
                            log.trace("Node not found in AssetRelationTree for asset " + child);
                        }
                        return Collections.emptyList();
                    default: {
                        throw new CSRuntimeException("Failed to look up asset " + child + " in AssetRelationTree.", errno);
                    }
                }
            }

            // found node. get its parent
            IList art = ics.GetList("AssetRelationTree");
            ics.RegisterList("AssetRelationTree", null);

            List<AssetId> parents = new ArrayList<AssetId>();

            if (art == null || !art.hasData() || art.numRows() == 0) {
                if (log != null && log.isTraceEnabled()) {
                    log.trace("Failed to locate " + child + " in AssetRelationTree.");
                }
            } else {

                List<String> childNodeIds = new ArrayList<String>();
                for (IList row : new IterableIListWrapper(art)) {
                    if (child.getType().equals(IListUtils.getStringValue(row, "otype"))) {
                        String nid = IListUtils.getStringValue(row, "nid");
                        String ncode = IListUtils.getStringValue(row, "ncode");
                        if (log != null && log.isTraceEnabled()) {
                            log.trace("Found " + child + " in AssetRelationTree.  Node ID: " + nid + ", ncode: " + ncode + ", expecting ncode: " + associationName);
                        }
                        if (associationName.equals(ncode)) {
                            childNodeIds.add(IListUtils.getStringValue(row, "nid"));
                        }
                    }
                }

                for (String nid : childNodeIds) {
                    vl.clear();
                    vl.setValString("ftcmd", "getparent");
                    vl.setValString("treename", "AssetRelationTree");
                    vl.setValString("node", nid);
                    if (ics.TreeManager(vl) && ics.GetErrno() >= 0) {
                        art = ics.GetList("AssetRelationTree");
                        ics.RegisterList("AssetRelationTree", null);
                        AssetId parent = new AssetIdImpl(IListUtils.getStringValue(art, "otype"), Long.valueOf(IListUtils.getStringValue(art, "oid")));
                        if (log != null && log.isTraceEnabled()) {
                            log.trace(child + " in AssetRelationTree has a parent " + parent);
                        }
                        if (expectedParentType == null) {
                            parents.add(parent);
                        } else {
                            if (expectedParentType.equals(parent.getType())) {
                                parents.add(parent);
                            } else {
                                if (log != null && log.isDebugEnabled()) {
                                    log.debug("Parent " + parent + " is not of the expected type (" + expectedParentType + ") so it is being excluded from the return list for child: " + child);
                                }
                            }
                        }
                    } else {
                        throw new CSRuntimeException("Failed to look up parent of article " + child + " in AssetRelationTree.  TreeManager call failed unexpectedly", ics.GetErrno());
                    }

                }
            }

            return parents;
        } else {
            throw new CSRuntimeException("Failed to look up article " + child + " in AssetRelationTree.  TreeManager call failed unexpectedly", ics.GetErrno());
        }
    }

    /**
     * Look up parents in Asset Relation Tree for the specified child.  Records asset dependencies as required (either a
     * qualified or an unqualified unknowndeps).
     *
     * @param ics             context
     * @param child           asset id that will have its parents retrieved
     * @param associationName name of association to use for lookup.  May not ever be null.
     * @return list of parents, never null.
     */
    public static Collection<AssetId> getParents(ICS ics, AssetId child, String... associationName) {

        // validate input
        if (ics == null) {
            throw new IllegalArgumentException("ICS cannot be null");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child asset id is required");
        }
        if (associationName == null) {
            throw new IllegalArgumentException("Association name may not be null");
        }

        List<String> assocNames = Arrays.asList(associationName); // so lame...

        Collection<AssetId> parents = new HashSet<AssetId>();

        for (Row childInfo : TreeHelper.findNode(ics, "AssetRelationTree", child)) {

            // right assoc name?
            String ncode = childInfo.getString("ncode");
            if (!assocNames.contains(ncode)) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Asset " + child + " with node " + childInfo.getString("nid") + " is not the child of any other asset using the association name " + assocNames + ". (This node is for the name " + ncode + ".)");
                }
                // nope...
                continue;
            }

            // Yup. Find its parent.
            for (Row parentInfo : TreeHelper.findParents(ics, "AssetRelationTree", childInfo.getString("nid"))) {
                AssetId parent = AssetIdUtils.createAssetId(parentInfo.getString("otype"), parentInfo.getString("oid"));
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Found parent " + parent + " of child " + child + " with association name " + ncode);
                }
                parents.add(parent);
            }
        }

        // log dep on child asset
        LogDep.logDep(ics, child);

        // log dep on context
        // todo: inspect the specified association definitions in "associationName" and only
        // record asset-type specific deps instead of unknowndep if possible
        Render.UnknownDeps(ics);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Looked up child asset " + child + " in AssetRelationTree for parents with the association names " + Arrays.asList(associationName) + " and found " + parents.size() + " results.  Details: " + (LOG.isTraceEnabled() ? parents : ""));
        }

        return parents;
    }
}
