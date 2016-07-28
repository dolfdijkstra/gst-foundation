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
import com.fatwire.gst.foundation.facade.runtag.render.GetPageUrl;

/**
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public class PageUriBuilder {

    private final GetPageUrl tag = new GetPageUrl();

    /**
     * Constructor with pagename and a wrapper .
     * 
     * @param pagename string value for page name
     * @param wrapper string value for wrapper
     */
    public PageUriBuilder(String pagename, String wrapper) {
        tag.setPagename(pagename);
        tag.setWrapperpage(wrapper);
    }

    /**
     * Constructor with pagename, without a wrapper.
     * 
     * @param pagename string value for page name
     */
    public PageUriBuilder(String pagename) {
        tag.setPagename(pagename);

    }

    /**
     * Constructor with c and cid, with a wrapper .
     * 
     * @param c current asset
     * @param cid content id
     * @param pagename string value for page name
     * @param wrapper string value for wrapper
     */
    public PageUriBuilder(String c, String cid, String pagename, String wrapper) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setPagename(pagename);
        tag.setWrapperpage(wrapper);
    }

    /**
     * Constructor with c and cid, without a wrapper.
     * 
     * @param c current asset
     * @param cid content id
     * @param pagename string value for page name
     */
    public PageUriBuilder(String c, String cid, String pagename) {
        tag.setC(c);
        tag.setCid(cid);
        tag.setPagename(pagename);

    }

    /**
     * Constructor with AssetId and wrapper.
     * 
     * @param id asset id
     * @param pagename string value for page name
     * @param wrapper string value for wrapper
     */
    public PageUriBuilder(AssetId id, String pagename, String wrapper) {
        this(id.getType(), Long.toString(id.getId()), pagename, wrapper);
    }

    /**
     * Constructor with AssetId, without a wrapper.
     * 
     * @param id asset id
     * @param pagename string value for page name
     */
    public PageUriBuilder(AssetId id, String pagename) {
        this(id.getType(), Long.toString(id.getId()), pagename);
    }

    /**
     * Returns the uri to the asset rendered with the specified template and
     * wrapper (if provided).
     * 
     * @param ics Content Server context object
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner#execute(COM.FutureTense.Interfaces.ICS)
     */
    public String toURI(ICS ics) {

        tag.setOutstr("uri__");
        tag.execute(ics);
        String uri = ics.GetVar("uri__");
        ics.RemoveVar("uri__");
        return uri;
    }

    /**
     * @param name argument name
     * @param value argument value
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.TagRunnerWithArguments#setArgument(java.lang.String,
     *      java.lang.String)
     */
    public PageUriBuilder argument(String name, String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * @param s string value of assembler
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAssembler(java.lang.String)
     */
    public PageUriBuilder assembler(String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s string value of authority
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setAuthority(java.lang.String)
     */
    public PageUriBuilder authority(String s) {
        tag.setAuthority(s);
        return this;
    }

    /**
     * @param s string value of container
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setContainer(java.lang.String)
     */
    public PageUriBuilder container(String s) {
        tag.setContainer(s);
        return this;
    }

    /**
     * @param s boolean value for Dynamic
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setDynamic(java.lang.String)
     */
    public PageUriBuilder dynamic(boolean s) {
        tag.setDynamic(s);
        return this;
    }

    /**
     * @param s string value of fragment
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setFragment(java.lang.String)
     */
    public PageUriBuilder fragment(String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s string value of packed arguments
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setPackedargs(java.lang.String)
     */
    public PageUriBuilder packedargs(String s) {
        tag.setPackedargs(s);
        return this;
    }

    /**
     * @param s boolean value of satellite
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setSatellite(java.lang.String)
     */
    public PageUriBuilder satellite(boolean s) {
        tag.setSatellite(s);
        return this;
    }

    /**
     * @param s string value of scheme
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setScheme(java.lang.String)
     */
    public PageUriBuilder scheme(String s) {
        tag.setScheme(s);
        return this;
    }

    /**
     * @param s string value of wrapper page
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl#setWrapperpage(java.lang.String)
     */
    public PageUriBuilder wrapper(String s) {
        tag.setWrapperpage(s);
        return this;
    }

}
