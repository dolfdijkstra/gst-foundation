/*
 * Copyright (c) 2009 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.apache.commons.logging.Log;

import static com.fatwire.gst.foundation.IListUtils.getStringValue;

/**
 * Utilities for working efficiently with the AssetRelationTree.
 * 
 * @author Tony Field
 * @since Jun 7, 2009
 */
public final class AssetRelationTreeUtils {
    /**
     * Get all of the parent assets in the AssetRelationTree for the specified
     * asset. Association name is required, but expectedParentType is an
     * optional filter argument.
     * 
     * @param ics ICS context
     * @param log logger. May be null.
     * @param child child asset id
     * @param expectedParentType asset type of the parent to be returned. If
     *            null, type of parent is irrelevant.
     * @param associationName name of association to use while looking for
     *            parents. may not be null
     * @return list of parents, never null.
     */
    public static List<AssetId> getAssetRelationTreeParents(ICS ics, Log log, AssetId child, String expectedParentType,
            String associationName) {
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
                        log.trace("Node not found in AssetRelationTree for asset " + child);
                        return Collections.emptyList();
                    default: {
                        throw new CSRuntimeException("Failed to look up asset " + child + " in AssetRelationTree.",
                                errno);
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
                    if (child.getType().equals(getStringValue(row, "otype"))) {
                        String nid = getStringValue(row, "nid");
                        String ncode = getStringValue(row, "ncode");
                        if (log != null && log.isTraceEnabled()) {
                            log.trace("Found " + child + " in AssetRelationTree.  Node ID: " + nid + ", ncode: "
                                    + ncode + ", expecting ncode: " + associationName);
                        }
                        if (associationName.equals(ncode)) {
                            childNodeIds.add(getStringValue(row, "nid"));
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
                        AssetId parent = new AssetIdImpl(getStringValue(art, "otype"), Long.valueOf(getStringValue(art,
                                "oid")));
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
                                    log.debug("Parent " + parent + " is not of the expected type ("
                                            + expectedParentType
                                            + ") so it is being excluded from the return list for child: " + child);
                                }
                            }
                        }
                    } else {
                        throw new CSRuntimeException("Failed to look up parent of article " + child
                                + " in AssetRelationTree.  TreeManager call failed unexpectedly", ics.GetErrno());
                    }

                }
            }

            return parents;
        } else {
            throw new CSRuntimeException("Failed to look up article " + child
                    + " in AssetRelationTree.  TreeManager call failed unexpectedly", ics.GetErrno());
        }
    }
}
