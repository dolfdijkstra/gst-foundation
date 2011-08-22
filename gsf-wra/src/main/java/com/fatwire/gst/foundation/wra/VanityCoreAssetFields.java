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
package com.fatwire.gst.foundation.wra;

import java.util.Date;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
/**
 * 
 * 
 * @author Dolf Dijkstra
 * @since June 2011
 */
public final class VanityCoreAssetFields {

    private final AssetIdWithSite aws;
    private final String template;
    private String name;

    private String description;
    private String subtype;
    private String path;

    private Date startDate;
    private Date endDate;

    /**
     * @param aws
     * @param template
     */
    public VanityCoreAssetFields(AssetIdWithSite assetid, String template) {
        super();
        this.aws = assetid;
        this.template = template;
    }

    public VanityCoreAssetFields(AssetId id, String site, String template) {
        this(new AssetIdWithSite(id, site), template);
    }

    /**
     * @return the asset id
     */
    public AssetIdWithSite getId() {
        return aws;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the subtype
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * @param subtype the subtype to set
     */
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
