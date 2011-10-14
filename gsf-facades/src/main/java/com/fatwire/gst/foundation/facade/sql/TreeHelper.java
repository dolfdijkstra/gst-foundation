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

package com.fatwire.gst.foundation.facade.sql;

import java.util.Arrays;
import java.util.List;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple class used to help out with TreeManager commands
 *
 * @author Tony Field
 * @since 2011-10-13
 */
public final class TreeHelper {
    private static final Log LOG = LogFactory.getLog(TreeHelper.class.getPackage().getName());

    private TreeHelper() {
    }

    public static IListIterable findNode(ICS ics, String tree, AssetId assetId) {
        return findNode(ics, tree, assetId.getType(), assetId.getId());
    }

    public static IListIterable findNode(ICS ics, String tree, String otype, long oid) {
        return findNode(ics, tree, otype, Long.toString(oid));
    }

    public static IListIterable findNode(ICS ics, String tree, String otype, String oid) {

        if (ics == null) throw new IllegalArgumentException("ICS may not be null");
        if (tree == null || tree.length() == 0) throw new IllegalArgumentException("Tree name not specified");
        if (otype == null) throw new IllegalArgumentException("Object type may not be null");
        if (oid == null) throw new IllegalArgumentException("Object id may not be null");

        FTValList vl = new FTValList();
        vl.setValString("ftcmd", "findnode");
        vl.setValString("treename", tree);
        vl.setValString("where", "oid");
        vl.setValString("oid", oid);

        IList nodeInfo = treeManager(ics, vl, Arrays.asList(-111));

        IListIterable result = new IListIterable(nodeInfo);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Found " + result.size() + " nodes for object " + otype + ":" + oid + " in tree " + tree);
        }

        return result;
    }

    public static IListIterable findParents(ICS ics, String tree, String nid) {
        if (ics == null) throw new IllegalArgumentException("ICS may not be null");
        if (tree == null || tree.length() == 0) throw new IllegalArgumentException("Tree name not specified");
        if (nid == null) throw new IllegalArgumentException("Node id may not be null");

        FTValList vl = new FTValList();
        vl.setValString("ftcmd", "getparent");
        vl.setValString("treename", tree);
        vl.setValString("node", nid);

        IList parentInfo = treeManager(ics, vl, Arrays.asList(-112));

        IListIterable result = new IListIterable(parentInfo);

        if (LOG.isTraceEnabled()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Found " + result.size() + " parents for node " + nid + " in tree " + tree);
            }
        }

        return result;
    }

    /**
     * Query treemanager using the commands specified, and handles all required error checking.
     *
     * @param ics              context
     * @param vl               commands
     * @param acceptableErrnos list of errnos that are okay - that won't result in an exception being thrown.
     * @return IList exactly as returned from TreeManager.  May be empty or null.
     * @throws CSRuntimeException if the command fails or returns an unsafe errno.
     */
    private static IList treeManager(ICS ics, FTValList vl, List<Integer> acceptableErrnos) {
        String ftcmd = vl.getValString("ftcmd");
        String treeName = vl.getValString("treename");

        if (ics.TreeManager(vl)) {
            int errno = ics.GetErrno();
            if (errno < 0) {
                if (acceptableErrnos.contains(errno)) {
                    LOG.trace("TreeManager failed with expected errno: " + errno + " for command " + ftcmd + ": " + vl);
                } else {
                    throw new CSRuntimeException("TreeManager failed unexpectedly for command " + ftcmd + ": " + vl, errno);
                }
            }
            IList result = ics.GetList(treeName);
            ics.RegisterList(treeName, null);
            return result;
        } else {
            throw new CSRuntimeException("TreeManager failed unexpectedly for command " + ftcmd + ": " + vl, -4); // unknown
        }
    }
}
