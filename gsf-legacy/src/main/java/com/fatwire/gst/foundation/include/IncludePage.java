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

import org.apache.commons.lang3.StringUtils;

import COM.FutureTense.ContentServer.PageData;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.render.ContentServer;
import com.fatwire.gst.foundation.facade.runtag.render.SatellitePage;

/**
 * Class that calls render:contentserver or render:satellitepage based on style.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * 
 * @deprecated as of release 12.x, replace with OOTB features (e.g. callelement tag, calltemplate tag, ics.RunTag and the like)
 * 
 */
public class IncludePage implements Include {
    public static final List<String> FORBIDDEN_VARS = Collections.unmodifiableList(Arrays.asList("tid", "eid", "seid",
            "packedargs", "variant", "context", "pagename", "rendermode", "ft_ss"));

    private Style style;
    private final Map<String, String> list = new HashMap<String, String>();
    private final String pagename;
    private String packedArgs;

    private final List<String> pc;
    private final ICS ics;

    /**
     * @param ics Content Server context object
     * @param pagename page name
     */
    public IncludePage(final ICS ics, final String pagename) {
        this(ics, pagename, Style.embedded);
    }

    /**
     * @param ics Content Server context object
     * @param pagename page name
     * @param style string value of style
     */
    public IncludePage(final ICS ics, final String pagename, final Style style) {
        this.ics = ics;
        this.style = style;
        this.pagename = pagename;
        final String[] keys = ics.pageCriteriaKeys(pagename);
        if (keys == null) {
            throw new IllegalArgumentException("Can't find page criteria for " + pagename
                    + ". Please check if pagecriteria are set for " + pagename + ".");
        }
        pc = Arrays.asList(keys);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.include.Include#include(COM.FutureTense.Interfaces
     * .ICS)
     */
    public void include(final ICS ics) {
        switch (style) {
            case embedded: {
                final ContentServer tag = new ContentServer(pagename);
                for (final Entry<String, String> e : list.entrySet()) {
                    tag.setArgument(e.getKey(), e.getValue());
                }
                if (StringUtils.isNotBlank(packedArgs)) {
                    tag.setArgument("PACKEDARGS", packedArgs);
                }
                final String s = tag.execute(ics);
                if (s != null) {
                    ics.StreamText(s);
                }
            }
                break;
            case pagelet: {
                final SatellitePage tag = new SatellitePage(pagename);
                for (final Entry<String, String> e : list.entrySet()) {
                    tag.setArgument(e.getKey(), e.getValue());
                }
                if (StringUtils.isNotBlank(packedArgs)) {
                    tag.setPackedArgs(packedArgs);
                }

                final String s = tag.execute(ics);
                if (s != null) {
                    ics.StreamText(s);
                }
            }
                break;
            case element: {
                PageData data = ics.getPageData(pagename);
                if (!data.isRegistered())
                    throw new IllegalArgumentException(pagename + " is not a registered page.");
                String element = data.getRootElement();
                FTValList ftv = argsToFTValList();
                ics.CallElement(element, ftv);
            }
            default:
                throw new IllegalStateException("Can't handle style " + style);
        }

    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private FTValList argsToFTValList() {
        FTValList ftv = new FTValList();
        ftv.putAll(list);
        return ftv;
    }

    /**
     * @param name argument name
     * @param value argument value
     * @return this
     * @see "com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setArgument(java.lang.String, java.lang.String)"
     */
    public IncludePage argument(final String name, final String value) {
        if (FORBIDDEN_VARS.contains(name)) {
            throw new IllegalArgumentException("Can't deal with " + name);
        }

        if (pc.contains(name)) {
            list.put(name, value);
        } else {
            throw new IllegalArgumentException( name + " is not part of the page criteria: " + pc.toString());
        }
        return this;
    }

    /**
     * Adds packedargs.
     * 
     * @param s string value of packed arguments
     * @return this
     * @see "com.fatwire.gst.foundation.facade.runtag.render.CallTemplate#setPackedargs(java.lang.String)"
     */
    public IncludePage packedargs(final String s) {
        this.packedArgs = s;
        return this;
    }

    /**
     * Copies the ics variables identified by the name array
     * 
     * @param name array of argument names
     * @return this
     */
    public IncludePage copyArguments(final String... name) {
        if (name == null) {
            return this;
        }
        for (final String n : name) {
            argument(n, ics.GetVar(n));
        }
        return this;
    }

    public IncludePage style(final Style s) {
        style = s;
        return this;
    }

    /**
     * Sets Style to embedded
     * 
     * @return this
     */
    public IncludePage embedded() {
        return style(Style.embedded);
    }

    /**
     * Sets Style to pagelet
     * 
     * @return this
     */
    public IncludePage pagelet() {
        return style(Style.pagelet);
    }
}
