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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl;

import org.apache.commons.lang.StringUtils;

/**
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 */
public class WraUriBuilder {

    private final GetTemplateUrl tag = new GetTemplateUrl();

    /**
     * Constructor with AssetId and default wrapper (GST/Dispatcher).
     * 
     * @param id
     */
    public WraUriBuilder(AssetId id) {
        this(id, "GST/Dispatcher");

    }

    /**
     * Constructor with AssetId and wrapper.
     * 
     * @param id
     * @param wrapper
     */
    public WraUriBuilder(AssetId id, String wrapper) {
        tag.setWrapperpage(wrapper);
        tag.setC(id.getType());
        tag.setCid(Long.toString(id.getId()));
        tag.setAssembler("wrapath");

    }

    public WraUriBuilder(WebReferenceableAsset wra, String wrapper) {
        this(wra.getId(), wrapper);
        if (StringUtils.isBlank(wra.getTemplate()))
            throw new IllegalArgumentException("The template attribute for asset " + wra.getId().getType() + ":"
                    + wra.getId().getId() + " is not provided.");
        tag.setTname(wra.getTemplate());
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
        ics.RemoveVar("uri__");
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
    public WraUriBuilder argument(String name, String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAssembler(java.lang.String)
     */
    public WraUriBuilder assembler(String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAuthority(java.lang.String)
     */
    public WraUriBuilder authority(String s) {
        tag.setAuthority(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setContainer(java.lang.String)
     */
    public WraUriBuilder container(String s) {
        tag.setContainer(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setDynamic(java.lang.String)
     */
    public WraUriBuilder dynamic(boolean s) {
        tag.setDynamic(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setFragment(java.lang.String)
     */
    public WraUriBuilder fragment(String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setPackedargs(java.lang.String)
     */
    public WraUriBuilder packedargs(String s) {
        tag.setPackedargs(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSatellite(java.lang.String)
     */
    public WraUriBuilder satellite(String s) {
        tag.setSatellite("TRUE".equalsIgnoreCase(s));
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setScheme(java.lang.String)
     */
    public WraUriBuilder scheme(String s) {
        tag.setScheme(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setWrapperpage(java.lang.String)
     */
    public WraUriBuilder wrapper(String s) {
        tag.setWrapperpage(s);
        return this;
    }

    public WraUriBuilder template(String template) {
        tag.setTname(template);
        return this;
    }

}
