/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.html;

public final class Img extends BaseElement {
    private String src;
    private String alt;
    private String longdesc;
    private String name;
    private String height;
    private String width;
    private String usemap;
    private String ismap;

    /**
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src the src to set
     */
    public void setSrc(final String src) {
        this.src = src;
    }

    /**
     * @return the alt
     */
    public String getAlt() {
        return alt;
    }

    /**
     * @param alt the alt to set
     */
    public void setAlt(final String alt) {
        this.alt = alt;
    }

    /**
     * @return the longdesc
     */
    public String getLongdesc() {
        return longdesc;
    }

    /**
     * @param longdesc the longdesc to set
     */
    public void setLongdesc(final String longdesc) {
        this.longdesc = longdesc;
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
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(final String height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(final String width) {
        this.width = width;
    }

    /**
     * @return the usemap
     */
    public String getUsemap() {
        return usemap;
    }

    /**
     * @param usemap the usemap to set
     */
    public void setUsemap(final String usemap) {
        this.usemap = usemap;
    }

    /**
     * @return the ismap
     */
    public String getIsmap() {
        return ismap;
    }

    /**
     * @param ismap the ismap to set
     */
    public void setIsmap(final String ismap) {
        this.ismap = ismap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Img [src=" + src + ", alt=" + alt + ", getId()=" + getId() + "]";
    }

}
