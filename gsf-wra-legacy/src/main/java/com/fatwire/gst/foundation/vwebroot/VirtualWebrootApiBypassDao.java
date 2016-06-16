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
package com.fatwire.gst.foundation.vwebroot;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.DirectSqlAccessTools;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.openmarket.xcelerate.asset.AssetIdImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Backdoor implementation of VirtualWebrootDao that does not utilize any Asset
 * APIs. This class should be used sparingly and may result in some
 * dependencies, that would ordinarily be recorded, being skipped.
 * <p>
 * User: Tony Field Date: 2011-05-06
 * 
 * @deprecated May 15, 2016 by fvillalba
 * 
 */
@Deprecated
public class VirtualWebrootApiBypassDao implements VirtualWebrootDao {
    private static final Logger LOG = LoggerFactory.getLogger("com.function1.gsf.foundation.vwebroot.VirtualWebrootApiBypassDao");

    private final ICS ics;
    private final DirectSqlAccessTools directSqlAccessTools;

    public VirtualWebrootApiBypassDao(ICS ics) {
        this.ics = ics;
        this.directSqlAccessTools = new DirectSqlAccessTools(ics);
    }

    public VirtualWebroot getVirtualWebroot(long cid) {
        if (LOG.isTraceEnabled())
            LOG.trace("Loading virtual webroot data for for GSTVirtualWebroot:" + cid);
        AssetId id = new AssetIdImpl("GSTVirtualWebroot", cid);
        Map<String, String> m = directSqlAccessTools.getFlexAttributeValues(id, "master_vwebroot", "env_vwebroot",
                "env_name");
        return new VWebrootBeanImpl(cid, m.get("master_vwebroot"), m.get("env_vwebroot"), m.get("env_name"));
    }

    // private static final PreparedStmt ALL_VW = new
    // PreparedStmt("SELECT id from GSTVirtualWebroot WHERE status != 'VO'",
    // Collections.singletonList("GSTVirtualWebroot"));

    /**
     * Get all of the virtual webroots, sorted by URL length.
     * 
     * @return list of virtual webroots
     */
    public SortedSet<VirtualWebroot> getAllVirtualWebroots() {

        SortedSet<VirtualWebroot> result = new TreeSet<VirtualWebroot>(new UrlInfoComparator());
        for (Row r : SqlHelper
                .select(ics, "GSTVirtualWebroot", "SELECT id from GSTVirtualWebroot WHERE status != 'VO'")) {
            result.add(getVirtualWebroot(r.getLong("id")));
        }
        if (result.size() == 0)
            throw new IllegalStateException("No GSTVirtualWebroots are registered");
        return result;
    }

    /**
     * Get the current virtual webroot environment as defined by the
     * configuration properties. Null indicates that none is configured.
     * 
     * @return virtual webroot environment or null if not set.
     */
    public String getVirtualWebrootEnvironment() {
        String environmentName = System.getProperty("com.fatwire.gst.foundation.env-name", null);

        // avoid configuration problem trickery
        if (environmentName != null) {
            environmentName = environmentName.trim();
            if (environmentName.length() == 0)
                environmentName = null;
        }

        if (environmentName == null) {
            // allow user to have accidentally mis-configured things
            environmentName = ics.GetProperty("com.fatwire.gst.foundation.env-name");
            // avoid configuration problem trickery
            if (environmentName != null) {
                environmentName = environmentName.trim();
                if (environmentName.length() == 0)
                    environmentName = null;
            }
        }
        if (environmentName == null)
            LOG.debug("Virtual webroot environment is not configured.");
        return environmentName;
    }

    /**
     * Look up and return the VirtualWebroot corresponding to the specified
     * VanityAsset, for the current environment. If the current environment is
     * not configured, no match can be found.
     * 
     * @param wra web-referenceable asset
     * @return matching VirtualWebroot or null if no match is found.
     */
    public VirtualWebroot lookupVirtualWebrootForAsset(VanityAsset wra) {
        if (LOG.isDebugEnabled())
            LOG.debug("Looking up virtual webroot for WRA " + wra.getId());
        String wraPath = wra.getPath();
        return lookupVirtualWebrootForUri(wraPath);
    }

    public VirtualWebroot lookupVirtualWebrootForUri(String wraPath) {
        if (wraPath == null) {
            LOG.trace("WRA does ont have a path set - cannot locate virtual webroot");
            return null;
        }
        String env = getVirtualWebrootEnvironment();
        if (env == null)
            return null;
        for (VirtualWebroot vw : getAllVirtualWebroots()) {
            // find longest first one that is found in the prefix of path. that
            // is virtual-webroot
            // the path in the asset must start with the MASTER virtual webroot
            // for this to work. This could
            // be loosened up but there is no real reason to right now.
            if (env.equals(vw.getEnvironmentName()) && wraPath.startsWith(vw.getMasterVirtualWebroot())) {
                return vw;
            }
        }
        return null; // no match
    }

    /**
     * Comparator that compares virtual webroots by webroot. Uses
     * reverse-natural ordering to ensure that overlapping virtual webroots
     * resolve properly.
     */
    public static class UrlInfoComparator implements Comparator<VirtualWebroot> {

        public int compare(VirtualWebroot o1, VirtualWebroot o2) {
            int i = -o1.getMasterVirtualWebroot().compareTo(o2.getMasterVirtualWebroot());
            if (i == 0) {
                int j = -o1.getEnvironmentName().compareTo(o2.getEnvironmentName());
                if (j == 0) {
                    int k = -o1.getEnvironmentVirtualWebroot().compareTo(o2.getEnvironmentVirtualWebroot());
                    if (k == 0) {
                        return (int) (o1.getId().getId() - o2.getId().getId());
                    }
                    return k;
                }
                return j;
            }
            return i;
        }
    }
}
