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
package com.fatwire.gst.foundation.controller;

import java.io.Serializable;

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * AssetId with SiteName attached to it
 *
 * @author Dolf Dijkstra
 * @since Jun 17, 2010
 */
public class AssetIdWithSite implements AssetId, Comparable<AssetIdWithSite>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String type;
    private final long id;
    private final String site;


    public AssetIdWithSite(String type, long id, String site) {
        if (type == null || site == null) throw new NullPointerException("type=" + type + ",site=" + site);
        this.type = type;
        this.id = id;
        this.site = site;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.assetapi.data.AssetId#getId()
     */

    public long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.assetapi.data.AssetId#getType()
     */

    public String getType() {
        return type;
    }

    public AssetId getAssetId() {
        return new AssetIdImpl(type, id);
    }

    /**
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((site == null) ? 0 : site.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof AssetIdWithSite)) return false;
        AssetIdWithSite other = (AssetIdWithSite) obj;
        if (id != other.id) return false;
        if (site == null) {
            if (other.site != null) return false;
        } else if (!site.equals(other.site)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return "AssetIdWithSite [type=" + type + ", id=" + id + ", site=" + site + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */

    public int compareTo(AssetIdWithSite o) {
        if (this == o) return 0;
        if (o.id > this.id) return -1;
        if (o.id < this.id) return 1;
        int t = this.type.compareTo(o.type);
        if (t != 0) return t;

        return this.site.compareTo(o.site);
    }

}
