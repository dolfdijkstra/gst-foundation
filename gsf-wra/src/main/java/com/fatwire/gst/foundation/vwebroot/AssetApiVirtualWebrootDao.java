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
import java.util.SortedSet;
import java.util.TreeSet;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.wra.WebReferenceableAsset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DAO for working with Virtual Webroots
 * 
 * @author Tony Field
 * @since Jul 22, 2010
 */
public final class AssetApiVirtualWebrootDao implements VirtualWebrootDao {

    private static final Log LOG = LogFactory.getLog(AssetApiVirtualWebrootDao.class.getName());

    private final ICS ics;

    public AssetApiVirtualWebrootDao(ICS ics) {
        this.ics = ics;
    }

    public VirtualWebroot getVirtualWebroot(long cid) {
        String sCid = Long.toString(cid);
        if (LOG.isTraceEnabled())
            LOG.trace("Loading virtual webroot data for for GSTVirtualWebroot:" + sCid);
        AssetData ad = AssetDataUtils.getAssetData(ics, AssetIdUtils.createAssetId("GSTVirtualWebroot", sCid), "master_vwebroot", "env_vwebroot",
                "env_name");
        return new VWebrootBeanImpl(cid, AttributeDataUtils.getWithFallback(ad, "master_vwebroot"),
                AttributeDataUtils.getWithFallback(ad, "env_vwebroot"), AttributeDataUtils.getWithFallback(ad,
                        "env_name"));

    }

    /**
     * Get all of the virtual webroots, sorted by URL length.
     * 
     * @return list of virtual webroots
     */
    public SortedSet<VirtualWebroot> getAllVirtualWebroots() {
        AssetList al = new AssetList();
        al.setExcludeVoided(true);
        al.setList("pr-out");
        al.setType("GSTVirtualWebroot");
        ics.RegisterList("pr-out", null);
        al.execute(ics);
        IList ilist = ics.GetList("pr-out");
        ics.RegisterList("pr-out", null);
        if (ilist == null)
            throw new IllegalStateException("No GSTVirtualWebroots are registered");

        SortedSet<VirtualWebroot> result = new TreeSet<VirtualWebroot>(new UrlInfoComparator());
        for (Row r : new IListIterable(ilist)) {
            result.add(getVirtualWebroot(r.getLong("id")));
        }
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
     * WebReferenceableAsset, for the current environment. If the current
     * environment is not configured, no match can be found.
     * 
     * @param wra web-referenceable asset
     * @return matching VirtualWebroot or null if no match is found.
     */
    public VirtualWebroot lookupVirtualWebrootForAsset(WebReferenceableAsset wra) {
        if (LOG.isDebugEnabled())
            LOG.debug("Looking up virtual webroot for WRA " + wra.getId());
        String wraPath = wra.getPath();
        return lookupVirtualWebrootForUri(wraPath);
    }

    public VirtualWebroot lookupVirtualWebrootForUri(String wraPath) {
        if (wraPath == null) {
            LOG.trace("WRA does not have a path set - cannot locate virtual webroot");
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
