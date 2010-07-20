/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.pageref;

import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;

/**
 * Simple bean representing a GSTVirtualWebroot
 *
 * @author Tony Field
 * @since Jul 20, 2010
 */
final class GSTVirtualWebroot implements Comparable<GSTVirtualWebroot> {
    private long id;
    private String masterVWebroot;
    private String envVWebroot;
    private String envName;

    private GSTVirtualWebroot(long id, String masterVWebroot, String envVWebroot, String envName) {
        this.id = id;
        if (!Utilities.goodString(masterVWebroot)) throw new IllegalArgumentException("Invalid Master VWebroot:"+masterVWebroot);
        this.masterVWebroot = masterVWebroot;
        if (!Utilities.goodString(envVWebroot)) throw new IllegalArgumentException("Invalid Env VWebroot:"+envVWebroot);
        this.envVWebroot = envVWebroot;
        if (!Utilities.goodString(envName)) throw new IllegalArgumentException("Invalid Env Name:"+envName);
        this.envName = envName;
    }

    public int compareTo(GSTVirtualWebroot that) {
        int i = this.masterVWebroot.compareTo(that.masterVWebroot);
        if (i == 0) {
            int j = this.envName.compareTo(that.envName);
            if (j == 0) {
                int k = this.envVWebroot.compareTo(that.envVWebroot);
                if (k == 0) {
                    return (int) (this.id - that.id);
                }
                return k;
            }
            return j;
        }
        return i;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GSTVirtualWebroot)) return false;

        GSTVirtualWebroot that = (GSTVirtualWebroot) o;

        if (id != that.id) return false;
        if (!envName.equals(that.envName)) return false;
        if (!envVWebroot.equals(that.envVWebroot)) return false;
        if (!masterVWebroot.equals(that.masterVWebroot)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + masterVWebroot.hashCode();
        result = 31 * result + envVWebroot.hashCode();
        result = 31 * result + envName.hashCode();
        return result;
    }

    static GSTVirtualWebroot loadData(String cid) {
        AssetData ad = AssetDataUtils.getAssetData("GSTVirtualWebroot", cid, "master_vwebroot", "env_vwebroot", "env_name");
        return new GSTVirtualWebroot(Long.parseLong(cid), AttributeDataUtils.getWithFallback(ad, "master_vwebroot"), AttributeDataUtils.getWithFallback(ad, "env_vwebroot"), AttributeDataUtils.getWithFallback(ad, "env_name"));

    }


}
