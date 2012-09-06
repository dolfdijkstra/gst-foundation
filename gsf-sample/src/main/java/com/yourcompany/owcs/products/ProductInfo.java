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
public class ProductInfo {
    private String brandName;
    private String catalogCodes;
    private Collection<ProductItem> items;
    private String longDescription;
    private String mediaCode;
    private String moreInfoAssoc;
    private String shortDescription;
    private Collection<String> skus;

    /**
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName the brandName to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return the catalogCodes
     */
    public String getCatalogCodes() {
        return catalogCodes;
    }

    /**
     * @param catalogCodes the catalogCodes to set
     */
    public void setCatalogCodes(String catalogCodes) {
        this.catalogCodes = catalogCodes;
    }

    /**
     * @return the items
     */
    public Collection<ProductItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(Collection<ProductItem> items) {
        this.items = items;
    }

    /**
     * @return the longDescription
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription the longDescription to set
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * @return the mediaCode
     */
    public String getMediaCode() {
        return mediaCode;
    }

    /**
     * @param mediaCode the mediaCode to set
     */
    public void setMediaCode(String mediaCode) {
        this.mediaCode = mediaCode;
    }

    /**
     * @return the moreInfoAssoc
     */
    public String getMoreInfoAssoc() {
        return moreInfoAssoc;
    }

    /**
     * @param moreInfoAssoc the moreInfoAssoc to set
     */
    public void setMoreInfoAssoc(String moreInfoAssoc) {
        this.moreInfoAssoc = moreInfoAssoc;
    }

    /**
     * @return the shortDescription
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * @param shortDescription the shortDescription to set
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * @return the skus
     */
    public Collection<String> getSkus() {
        return skus;
    }

    /**
     * @param skus the skus to set
     */
    public void setSkus(Collection<String> skus) {
        this.skus = skus;
    }

}
