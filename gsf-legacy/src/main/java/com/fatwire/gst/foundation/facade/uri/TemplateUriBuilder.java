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
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public class TemplateUriBuilder {

    private final GetTemplateUrl tag = new GetTemplateUrl();

    private String site;

    /**
     * Constructor with c and cid, with a wrapper .
     * 
     * @param c current asset
     * @param cid content id
     * @param tname template name
     * @param wrapper string value for wrapper
     */
    public TemplateUriBuilder(final String c, final String cid, final String tname, final String wrapper) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setTname(tname);
        tag.setWrapperpage(wrapper);
    }

    /**
     * Constructor with c and cid, without a wrapper.
     * 
     * @param c current asset
     * @param cid content id
     * @param tname template name
     */
    public TemplateUriBuilder(final String c, final String cid, final String tname) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setTname(tname);

    }

    /**
     * Constructor with AssetId and wrapper.
     * 
     * @param id asset id
     * @param tname template name
     * @param wrapper string value for wrapper
     */
    public TemplateUriBuilder(final AssetId id, final String tname, final String wrapper) {
        this(id.getType(), Long.toString(id.getId()), tname, wrapper);
    }

    /**
     * Constructor with AssetId, without a wrapper.
     * 
     * @param id asset id
     * @param tname template name
     */
    public TemplateUriBuilder(final AssetId id, final String tname) {
        this(id.getType(), Long.toString(id.getId()), tname);
    }

    /**
     * Returns the uri to the asset rendered with the specified template and
     * wrapper (if provided).
     * 
     * @param ics Content Server context object
     * @return the Content Server url.
     * @see "com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner#execute(COM.FutureTense.Interfaces.ICS)"
     */
    public String toURI(final ICS ics) {
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
        final String uri = ics.GetVar("uri__");
        ics.RemoveVar("uri__");
        return uri;
    }

    /**
     * @param name argument name
     * @param value argument value
     * @see "com.fatwire.gst.foundation.facade.runtag.render.TagRunnerWithArguments#setArgument(java.lang.String, java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder argument(final String name, final String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * @param s string value for assembler
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAssembler(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder assembler(final String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s string value for authority
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAuthority(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder authority(final String s) {
        tag.setAuthority(s);
        return this;
    }

    /**
     * @param s string value for container
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setContainer(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder container(final String s) {
        tag.setContainer(s);
        return this;
    }

    /**
     * @param s flag for dynamic
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setDynamic(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder dynamic(final boolean s) {
        tag.setDynamic(s);
        return this;
    }

    /**
     * @param s string value for fragment
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setFragment(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder fragment(final String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s string value for packed arguments
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setPackedargs(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder packedargs(final String s) {
        tag.setPackedargs(s);
        return this;
    }

    /**
     * @param s flag for satellite
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSatellite(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder satellite(final boolean s) {
        tag.setSatellite(s);
        return this;
    }

    /**
     * @param s string value for scheme
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setScheme(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder scheme(final String s) {
        tag.setScheme(s);
        return this;
    }

    /**
     * @param s string value for site
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSite(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder site(final String s) {
        site = s;
        tag.setSite(s);
        return this;
    }

    /**
     * @param s string value for wrapper page
     * @see "com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setWrapperpage(java.lang.String)"
     * @return this template uri builder
     */
    public TemplateUriBuilder wrapper(final String s) {
        tag.setWrapperpage(s);
        return this;
    }

}
