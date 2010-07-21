/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.url;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;

/**
 * Simple bean representing a GSTVirtualWebroot
 *
 * @author Tony Field
 * @since Jul 20, 2010
 */
final class GSTVirtualWebroot {
    private long id;
    private String masterVWebroot;
    private String envVWebroot;
    private String envName;

    private GSTVirtualWebroot(long id, String masterVWebroot, String envVWebroot, String envName) {
        this.id = id;
        if (!Utilities.goodString(masterVWebroot))
            throw new IllegalArgumentException("Invalid Master VWebroot:" + masterVWebroot);
        this.masterVWebroot = masterVWebroot;
        if (!Utilities.goodString(envVWebroot))
            throw new IllegalArgumentException("Invalid Env VWebroot:" + envVWebroot);
        this.envVWebroot = envVWebroot;
        if (!Utilities.goodString(envName)) throw new IllegalArgumentException("Invalid Env Name:" + envName);
        this.envName = envName;
    }

    public long getId() {
        return id;
    }

    public String getMasterVWebroot() {
        return masterVWebroot;
    }

    public String getEnvVWebroot() {
        return envVWebroot;
    }

    public String getEnvName() {
        return envName;
    }


    static GSTVirtualWebroot loadData(String cid) {
        AssetData ad = AssetDataUtils.getAssetData("GSTVirtualWebroot", cid, "master_vwebroot", "env_vwebroot", "env_name");
        return new GSTVirtualWebroot(Long.parseLong(cid), AttributeDataUtils.getWithFallback(ad, "master_vwebroot"), AttributeDataUtils.getWithFallback(ad, "env_vwebroot"), AttributeDataUtils.getWithFallback(ad, "env_name"));

    }

    static SortedSet<GSTVirtualWebroot> getAllVirtualWebroots(ICS ics) {
        AssetList al = new AssetList();
        al.setExcludeVoided(true);
        al.setList("pr-out");
        ics.RegisterList("pr-out", null);
        al.execute(ics);
        IList ilist = ics.GetList("pr-out");
        ics.RegisterList("pr-out", null);
        if (ilist == null) throw new IllegalStateException("No GSTVirtualWebroots are registered");

        SortedSet result = new TreeSet<GSTVirtualWebroot>(new UrlInfoComparator());
        for (Row r : new IListIterable(ilist)) {
            result.add(loadData(r.getString("id")));
        }
        return result;
    }

    static class UrlInfoComparator implements Comparator<GSTVirtualWebroot> {

        public int compare(GSTVirtualWebroot o1, GSTVirtualWebroot o2) {
            int i = o1.masterVWebroot.compareTo(o2.masterVWebroot);
            if (i == 0) {
                int j = o1.envName.compareTo(o2.envName);
                if (j == 0) {
                    int k = o1.envVWebroot.compareTo(o2.envVWebroot);
                    if (k == 0) {
                        return (int) (o1.id - o2.id);
                    }
                    return k;
                }
                return j;
            }
            return i;
        }
    }
}
