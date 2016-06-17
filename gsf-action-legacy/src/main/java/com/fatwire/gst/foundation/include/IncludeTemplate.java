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
package com.fatwire.gst.foundation.include;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Type;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class IncludeTemplate implements Include {

    public static final List<String> FORBIDDEN_VARS = Collections.unmodifiableList(Arrays.asList("c", "cid", "eid",
            "seid", "packedargs", "variant", "context", "pagename", "childpagename", "site", "sitepfx", "tid",
            "rendermode", "ft_ss", "SystemAssetsRoot", "cshttp", "errno", "tablename", "empty", "errdetail", "null"));

    private final CallTemplate tag;
    private final List<String> pc;
    private final ICS ics;

    /**
     * @param ics Content Server context
     * @param asset asset to render
     * @param tname template name
     */
    public IncludeTemplate(final ICS ics, final AssetId asset, final String tname) {
        this.ics = ics;
        tag = new CallTemplate();
        tag.setTname(tname);

        final String eid = ics.GetVar("eid");
        if (eid != null) {
            tag.setTid(eid);
            tag.setTtype(Type.CSElement);
        } else {
            tag.setTid(ics.GetVar("tid"));
            tag.setTtype(Type.Template);
        }
        tag.setContext("");
        final String site = ics.GetVar("site");
        tag.setSite(site);
        final String packedargs = ics.GetVar("packedargs");
        if (packedargs != null && packedargs.length() > 0) {
            tag.setPackedargs(packedargs);
        }

        tag.setAsset(asset);
        tag.setFixPageCriteria(false); // for some reason the check pagecriteria
                                       // code in CallTemplate is not working.
        tag.setSlotname("foo");
        final String target = tname.startsWith("/") ? site + tname : site + "/" + asset.getType() + "/" + tname;
        final String[] keys = ics.pageCriteriaKeys(target);
        if (keys == null) {
            throw new IllegalArgumentException("Can't find page criteria for " + target
                    + ". Please check if pagecriteria are set for " + target + ".");
        }
        pc = Arrays.asList(keys);
        // copy the current available arguments
        // developer can override later by calling method argument
        for (final String key : keys) {
            if (!FORBIDDEN_VARS.contains(key.toLowerCase(Locale.US))) {
                final String value = ics.GetVar(key);
                if (StringUtils.isNotBlank(value)) {
                    tag.setArgument(key, value);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.include.Include#include(COM.FutureTense.Interfaces
     * .ICS)
     */
    public void include(final ICS ics) {
        final String s = tag.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }

    }

    /**
     * @param name argument name
     * @param value argument value
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setArgument(java.lang.String,
     *      java.lang.String)
     */
    public IncludeTemplate argument(final String name, final String value) {
        if (StringUtils.isBlank(name)) {
            return this;
        }
        if (FORBIDDEN_VARS.contains(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException("Can't deal with " + name
                    + ". It is a forbidden argument to set as an argument. Forbidden arguments are: "
                    + FORBIDDEN_VARS.toString());
        }
        if (pc.contains(name)) {
            tag.setArgument(name, value);
        } else {
            throw new IllegalArgumentException("Can't deal with " + name
                    + ". It not part of page criteria. PageCriteria are: " + pc.toString());
        }
        return this;
    }

    /**
     * Copies the ics variables identified by the name array
     * 
     * @param name argument name
     * @return this
     */
    public IncludeTemplate copyArguments(final String... name) {
        if (name == null) {
            return this;
        }
        for (final String n : name) {
            argument(n, ics.GetVar(n));
        }
        return this;
    }

    /**
     * Adds packedargs.
     * 
     * @param s packedargs
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setPackedargs(java.lang.String)
     */
    public IncludeTemplate packedargs(final String s) {
        tag.setPackedargs(s);
        return this;
    }

    /**
     * @param s style
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setStyle(com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style)
     */
    public IncludeTemplate style(final Style s) {
        tag.setStyle(s);
        return this;
    }

    /**
     * Sets Style to element
     * 
     * @return this
     */
    public IncludeTemplate element() {
        return style(Style.element);
    }

    /**
     * Sets Style to embedded
     * 
     * @return this
     */
    public IncludeTemplate embedded() {
        return style(Style.embedded);
    }

    /**
     * Sets Style to pagelet
     * 
     * @return this
     */
    public IncludeTemplate pagelet() {
        return style(Style.pagelet);
    }

}
