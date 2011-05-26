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

/**
 * @author Dolf Dijkstra
 * @since Apr 16, 2011
 */
public final class Anchor extends BaseElement {

    private String charset;
    private String type;
    private String name;

    private String href;
    private String hreflang;
    private String rel;
    private String rev;

    private String accesskey;
    private String shape;
    private String coords;
    private String tabindex;

    private String onfocus;
    private String onblur;

    private String target;

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
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
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(final String href) {
        this.href = href;
    }

    /**
     * @return the hreflang
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * @param hreflang the hreflang to set
     */
    public void setHreflang(final String hreflang) {
        this.hreflang = hreflang;
    }

    /**
     * @return the rel
     */
    public String getRel() {
        return rel;
    }

    /**
     * @param rel the rel to set
     */
    public void setRel(final String rel) {
        this.rel = rel;
    }

    /**
     * @return the rev
     */
    public String getRev() {
        return rev;
    }

    /**
     * @param rev the rev to set
     */
    public void setRev(final String rev) {
        this.rev = rev;
    }

    /**
     * @return the accesskey
     */
    public String getAccesskey() {
        return accesskey;
    }

    /**
     * @param accesskey the accesskey to set
     */
    public void setAccesskey(final String accesskey) {
        this.accesskey = accesskey;
    }

    /**
     * @return the shape
     */
    public String getShape() {
        return shape;
    }

    /**
     * @param shape the shape to set
     */
    public void setShape(final String shape) {
        this.shape = shape;
    }

    /**
     * @return the coords
     */
    public String getCoords() {
        return coords;
    }

    /**
     * @param coords the coords to set
     */
    public void setCoords(final String coords) {
        this.coords = coords;
    }

    /**
     * @return the tabindex
     */
    public String getTabindex() {
        return tabindex;
    }

    /**
     * @param tabindex the tabindex to set
     */
    public void setTabindex(final String tabindex) {
        this.tabindex = tabindex;
    }

    /**
     * @return the onfocus
     */
    public String getOnfocus() {
        return onfocus;
    }

    /**
     * @param onfocus the onfocus to set
     */
    public void setOnfocus(final String onfocus) {
        this.onfocus = onfocus;
    }

    /**
     * @return the onblur
     */
    public String getOnblur() {
        return onblur;
    }

    /**
     * @param onblur the onblur to set
     */
    public void setOnblur(final String onblur) {
        this.onblur = onblur;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(final String target) {
        this.target = target;
    }

}
