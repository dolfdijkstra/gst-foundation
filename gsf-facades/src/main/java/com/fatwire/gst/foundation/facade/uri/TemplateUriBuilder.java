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

package com.fatwire.gst.foundation.facade.uri;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl;

/**
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 */
public class TemplateUriBuilder {

    private final GetTemplateUrl tag = new GetTemplateUrl();

    private String site;

    /**
     * Constructor with c and cid, with a wrapper .
     * 
     * @param c
     * @param cid
     * @param tname
     * @param wrapper
     */
    public TemplateUriBuilder(String c, String cid, String tname, String wrapper) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setTname(tname);
        tag.setWrapperpage(wrapper);
    }

    /**
     * Constructor with c and cid, without a wrapper.
     * 
     * @param c
     * @param cid
     * @param tname
     */
    public TemplateUriBuilder(String c, String cid, String tname) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setTname(tname);

    }

    /**
     * Constructor with AssetId and wrapper.
     * 
     * @param id
     * @param tname
     * @param wrapper
     */
    public TemplateUriBuilder(AssetId id, String tname, String wrapper) {
        this(id.getType(), Long.toString(id.getId()), tname, wrapper);
    }

    /**
     * Constructor with AssetId, without a wrapper.
     * 
     * @param id
     * @param tname
     * @param wrapper
     */
    public TemplateUriBuilder(AssetId id, String tname) {
        this(id.getType(), Long.toString(id.getId()), tname);
    }

    /**
     * Returns the uri to the asset rendered with the specified template and
     * wrapper (if provided).
     * 
     * @param ics
     * @return
     * @see com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner#execute(COM.FutureTense.Interfaces.ICS)
     */
    public String toURI(ICS ics) {
        tag.setContext("");
        if (site == null) {
            tag.setSite(ics.GetVar("site"));
        }
        tag.setSlotname("irrelevant__");
        if (ics.GetVar("eid") != null) {
            tag.setTid(ics.GetVar("eid"));
            tag.setTtype("CSElement");
        } else {
            tag.setTid(ics.GetVar("tid"));
            tag.setTtype("Template");

        }
        tag.setOutstr("uri__");
        tag.execute(ics);
        String uri = ics.GetVar("uri__");
        ics.RemoveVar("uri__");
        return uri;
    }

    /**
     * @param name
     * @param value
     * @see com.fatwire.gst.foundation.facade.runtag.render.TagRunnerWithArguments#setArgument(java.lang.String,
     *      java.lang.String)
     */
    public TemplateUriBuilder argument(String name, String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAssembler(java.lang.String)
     */
    public TemplateUriBuilder assembler(String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAuthority(java.lang.String)
     */
    public TemplateUriBuilder authority(String s) {
        tag.setAuthority(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setContainer(java.lang.String)
     */
    public TemplateUriBuilder container(String s) {
        tag.setContainer(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setDynamic(java.lang.String)
     */
    public TemplateUriBuilder dynamic(boolean s) {
        tag.setDynamic(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setFragment(java.lang.String)
     */
    public TemplateUriBuilder fragment(String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setPackedargs(java.lang.String)
     */
    public TemplateUriBuilder packedargs(String s) {
        tag.setPackedargs(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSatellite(java.lang.String)
     */
    public TemplateUriBuilder satellite(boolean s) {
        tag.setSatellite(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setScheme(java.lang.String)
     */
    public TemplateUriBuilder scheme(String s) {
        tag.setScheme(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSite(java.lang.String)
     */
    public TemplateUriBuilder site(String s) {
        site = s;
        tag.setSite(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setWrapperpage(java.lang.String)
     */
    public TemplateUriBuilder wrapper(String s) {
        tag.setWrapperpage(s);
        return this;
    }

}
