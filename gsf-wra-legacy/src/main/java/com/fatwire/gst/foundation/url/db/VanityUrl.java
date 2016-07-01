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
package com.fatwire.gst.foundation.url.db;

import java.util.Date;

/**
 * Bean holding the VanityUrl data.
 * 
 * @author Dolf Dijkstra
 * @since November 1, 2011
 * 
 * @deprecated May 15, 2016 by fvillalba
 * 
 */
@Deprecated
class VanityUrl {
    private long id;
    private String path;
    private String assettype;
    private long assetid;
    private Date startdate;
    private Date enddate;
    private String opt_vwebroot;
    private String opt_url_path;
    private int opt_depth;
    private String opt_site;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getAssettype() {
        return assettype;
    }

    public void setAssettype(final String assettype) {
        this.assettype = assettype;
    }

    public long getAssetid() {
        return assetid;
    }

    public void setAssetid(final long assetid) {
        this.assetid = assetid;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(final Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(final Date enddate) {
        this.enddate = enddate;
    }

    public String getOpt_vwebroot() {
        return opt_vwebroot;
    }

    public void setOpt_vwebroot(final String opt_vwebroot) {
        this.opt_vwebroot = opt_vwebroot;
    }

    public String getOpt_url_path() {
        return opt_url_path;
    }

    public void setOpt_url_path(final String opt_url_path) {
        this.opt_url_path = opt_url_path;
    }

    public int getOpt_depth() {
        return opt_depth;
    }

    public void setOpt_depth(final int opt_depth) {
        this.opt_depth = opt_depth;
    }

    public String getOpt_site() {
        return opt_site;
    }

    public void setOpt_site(final String opt_site) {
        this.opt_site = opt_site;
    }

    @Override
    public String toString() {
        return "VanityUrl [path=" + path + ", assettype=" + assettype + ", assetid=" + assetid + "]";
    }

}
