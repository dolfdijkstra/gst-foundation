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
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;

/**
 * Simple WRA bean
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public class WraBeanImpl implements WebReferenceableAsset {
    private AssetId id;
    private String name;
    private String description;
    private String subtype;
    private String status;
    private String metaTitle;
    private String metaDescription;
    private String metaKeyword;
    private String h1Title;
    private String linkText;
    private String path;
    private String template;
    private Date startDate;
    private Date endDate;

    public WraBeanImpl() {
    }

    public WraBeanImpl(TemplateAsset asset) {
        id = asset.getAssetId();
        name = asset.asString("name");
        description = asset.asString("description");
        subtype = asset.asString("subtype");
        status = asset.asString("status");
        metaTitle = asset.asString("metatitle");
        metaDescription = asset.asString("metadescription");
        metaKeyword = asset.asString("metakeyword");
        h1Title = asset.asString("h1title");
        linkText = asset.asString("linktext");
        path = asset.asString("path");
        template = asset.asString("template");
        startDate = asset.asDate("startdate");
        endDate = asset.asDate("enddate");
    }

    public WraBeanImpl(WebReferenceableAsset wra) {
        id = wra.getId();
        name = wra.getName();
        description = wra.getDescription();
        subtype = wra.getSubtype();
        status = wra.getStatus();
        metaTitle = wra.getMetaTitle();
        metaDescription = wra.getMetaDescription();
        metaKeyword = wra.getMetaKeyword();
        h1Title = wra.getH1Title();
        linkText = wra.getLinkText();
        path = wra.getPath();
        template = wra.getTemplate();
        startDate = wra.getStartDate();
        endDate = wra.getEndDate();
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

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getH1Title() {
        return h1Title;
    }

    public void setH1Title(String h1Title) {
        this.h1Title = h1Title;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
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
