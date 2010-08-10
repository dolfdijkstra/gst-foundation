/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.runtag.render;

import com.fatwire.assetapi.data.AssetId;

/**
 * CallTemplate tag
 * <p/>
 * <code>
 * &lt;RENDER.CALLTEMPLATE SITE="site name" 
 * SLOTNAME="name of slot"
 * TID="caller Template or CSElement id" [TTYPE="caller Template or CSElement"]
 * [C="asset type"] [CID="asset id"] [TNAME="target Template or CSElement name"]
 * [CONTEXT="context override"] [STYLE="pagelet or element"]
 * [VARIANT="template variant name"] [PACKEDARGS="packed arguments"]&gt;
 * <p/>
 * [&lt;RENDER.ARGUMENT NAME="variable1" VALUE="value1"/&gt;]
 * <p/>
 * &lt;/RENDER.CALLTEMPLATE&gt;
 * </code>
 * 
 * @author Tony Field
 * @since Jun 10, 2010
 */
public class CallTemplate extends TagRunnerWithArguments {
    

    public enum Style {
        element, pagelet, embedded
    }

    public enum Type {
        Template, CSElement
    }

    public CallTemplate() {
        super("RENDER.CALLTEMPLATE");
    }

    /**
     * Sets up CallTemplate with default <tt>Style.element</tt>
     * 
     * @param slotname
     * @param tname
     * @param type
     */
    public CallTemplate(String slotname, String tname, Type type) {
        super("RENDER.CALLTEMPLATE");
        setSlotname(slotname);
        setTname(tname);
        setTtype(type);
        setStyle(Style.element);
        setContext("");

    }

    public void setSite(String s) {
        set("SITE", s);
    }

    public void setSlotname(String s) {
        set("SLOTNAME", s);
    }

    public void setTid(String s) {
        set("TID", s);
    }

    public void setTtype(Type s) {
        set("TTYPE", s.toString());
    }

    public void setC(String s) {
        set("C", s);
    }

    public void setCid(String s) {
        set("CID", s);
    }

    public void setTname(String s) {
        set("TNAME", s);
    }

    public void setContext(String s) {
        set("CONTEXT", s);
    }

    public void setStyle(Style s) {
        set("STYLE", s.toString());
    }

    public void setVariant(String s) {
        set("VARIANT", s);
    }

    public void setPackedargs(String s) {
        set("PACKEDARGS", s);
    }

    public void setAsset(AssetId id) {
        setC(id.getType());
        setCid(Long.toString(id.getId()));
    }

}
