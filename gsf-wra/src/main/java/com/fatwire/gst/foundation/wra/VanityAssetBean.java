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

/**
 * Simple VanityAsset
 * 
 * @author Dolf Dijkstra
 * 
 */
public class VanityAssetBean implements VanityAsset {
    private AssetId id;
    private String name;
    private String description;
    private String subtype;
    private String status;
    private String path;
    private String template;
    private Date startDate;
    private Date endDate;

    public VanityAssetBean() {
    }

    public VanityAssetBean(VanityAsset va) {
        id = va.getId();
        name = va.getName();
        description = va.getDescription();
        subtype = va.getSubtype();
        status = va.getStatus();
        path = va.getPath();
        template = va.getTemplate();
        startDate = va.getStartDate();
        endDate = va.getEndDate();
    }
    public VanityAssetBean(Alias alias) {
        id = alias.getTarget();
        name = alias.getName();
        description = alias.getDescription();
        subtype = alias.getSubtype();
        status = alias.getStatus();
        path = alias.getPath();
        template = alias.getTemplate();
        startDate = alias.getStartDate();
        endDate = alias.getEndDate();
    }

    public AssetId getId() {
        return id;
    }

    public void setId(AssetId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        return (getId() != null) ? getId().toString() : "[null id]";
    }
}
