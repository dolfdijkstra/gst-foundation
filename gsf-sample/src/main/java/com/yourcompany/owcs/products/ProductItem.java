/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.yourcompany.owcs.products;

import java.util.Collection;

/**
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 * 
 */
public class ProductItem {
    private Collection<String> colorCodes;
    private String description;
    private String image;
    private String itemNumber;
    private String linkText;
    private String sizeCodes;
    private String url;

    /**
     * @return the colorCodes
     */
    public Collection<String> getColorCodes() {
        return colorCodes;
    }

    /**
     * @param colorCodes the colorCodes to set
     */
    public void setColorCodes(Collection<String> colorCodes) {
        this.colorCodes = colorCodes;
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
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the itemNumber
     */
    public String getItemNumber() {
        return itemNumber;
    }

    /**
     * @param itemNumber the itemNumber to set
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * @return the linkText
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * @param linkText the linkText to set
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * @return the sizeCodes
     */
    public String getSizeCodes() {
        return sizeCodes;
    }

    /**
     * @param sizeCodes the sizeCodes to set
     */
    public void setSizeCodes(String sizeCodes) {
        this.sizeCodes = sizeCodes;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
