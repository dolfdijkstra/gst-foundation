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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.render.ContentServer;
import com.fatwire.gst.foundation.facade.runtag.render.SatellitePage;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;

import org.apache.commons.lang.StringUtils;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */

public class IncludePage implements Include {
    public static final List<String> FORBIDDEN_VARS = Collections.unmodifiableList(Arrays.asList("tid", "eid", "seid",
            "packedargs", "variant", "context", "pagename", "rendermode", "ft_ss"));

    private Style style;
    private Map<String, String> list = new HashMap<String, String>();
    private String pagename;
    private String packedArgs;

    private List<String> pc;
    private final ICS ics;

    public IncludePage(ICS ics, String pagename) {
        this(ics, pagename, Style.embedded);
    }

    public IncludePage(ICS ics, String pagename, Style style) {
        this.ics = ics;
        this.style = style;
        this.pagename = pagename;
        String[] keys = ics.pageCriteriaKeys(pagename);
        if (keys == null)
            throw new IllegalArgumentException("Can't find page criteria for " + pagename
                    + ". Please check if pagecriteria are set for " + pagename + ".");
        pc = Arrays.asList(keys);
    }

    public void include(ICS ics) {
        switch (style) {
            case embedded: {
                ContentServer tag = new ContentServer(pagename);
                for (Entry<String, String> e : list.entrySet()) {
                    tag.setArgument(e.getKey(), e.getValue());
                }
                if (StringUtils.isNotBlank(packedArgs)) {
                    tag.setArgument("PACKEDARGS", packedArgs);
                }
                String s = tag.execute(ics);
                if (s != null) {
                    ics.StreamText(s);
                }
            }
                break;
            case pagelet: {
                SatellitePage tag = new SatellitePage(pagename);
                for (Entry<String, String> e : list.entrySet()) {
                    tag.setArgument(e.getKey(), e.getValue());
                }
                if (StringUtils.isNotBlank(packedArgs)) {
                    tag.setPackedArgs(packedArgs);
                }

                String s = tag.execute(ics);
                if (s != null) {
                    ics.StreamText(s);
                }
            }
                break;
            default:
                throw new IllegalStateException("Can't handle style " + style);
        }

    }

    /**
     * @param name
     * @param value
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setArgument(java.lang.String,
     *      java.lang.String)
     */
    public IncludePage argument(String name, String value) {
        if (pc.contains(name) && !FORBIDDEN_VARS.contains(name))
            list.put(name, value);
        else
            throw new IllegalArgumentException("Can't deal with " + name);
        return this;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setPackedargs(java.lang.String)
     */
    public IncludePage packedargs(String s) {
        this.packedArgs = s;
        return this;
    }

    public IncludePage copyArguments(final String... name) {
        if (name == null) {
            return this;
        }
        for (final String n : name) {
            argument(n, ics.GetVar(n));
        }
        return this;
    }

}
