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

    private static final long serialVersionUID = 1L;
    private final AssetIdImpl id;
    private final String site;

    public AssetIdWithSite(AssetId id, String site) {
        if (id == null)
            throw new NullPointerException("id can not be null.");
        if (id instanceof AssetIdImpl) {
            this.id = (AssetIdImpl) id;
        } else {
            this.id = new AssetIdImpl(id.getType(), id.getId());
        }
        this.site = site;
    }

    public AssetIdWithSite(String type, long id, String site) {
        if (type == null || site == null)
            throw new NullPointerException("type=" + type + ",site=" + site);
        this.id = new AssetIdImpl(type, id);
        this.site = site;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.assetapi.data.AssetId#getId()
     */

    public long getId() {
        return id.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.assetapi.data.AssetId#getType()
     */

    public String getType() {
        return id.getType();
    }

    public AssetId getAssetId() {
        return id;
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
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return "AssetIdWithSite [type=" + id.getType() + ", id=" + id.getId() + ", site=" + site + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */

    public int compareTo(AssetIdWithSite o) {
        if (this == o)
            return 0;
        int l = id.compareTo(o.getAssetId());
        if (l != 0)
            return l;

        return this.site.compareTo(o.site);
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((site == null) ? 0 : site.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj instanceof AssetIdImpl)
            return id.equals(obj);
        if (!(obj instanceof AssetIdWithSite))
            return false;
        AssetIdWithSite other = (AssetIdWithSite) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (site == null) {
            if (other.site != null)
                return false;
        } else if (!site.equals(other.site))
            return false;
        return true;
    }

}
