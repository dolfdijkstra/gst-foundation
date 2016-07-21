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

package com.fatwire.gst.foundation.taglib.navigation;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import tools.gsf.navigation.NavigationNode;
import tools.gsf.navigation.NavigationService;
import com.fatwire.gst.foundation.taglib.GsfSimpleTag;

/**
 * 
 * jsp tag to generate the navigation data structure.
 * 
 * @author Dolf Dijkstra
 * @since August 31, 2012
 * 
 * 
 * @deprecated as of release 12.x, temporarily, until a brand new, significantly improved NavigationService implementation is released (soon)
 * 
 */
public class PluggableNavigationTag extends GsfSimpleTag {

    private String name;
    private int depth = 1;
    private String pagename;

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(final int depth) {
        this.depth = depth;
    }

    /**
     * @param pagename the pagename to set
     */
    public void setPagename(final String pagename) {
        this.pagename = pagename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {
        super.doTag();
        final ICS ics = (ICS) getICS();

        final NavigationService nh = getService("navigationService", NavigationService.class);
        if (nh == null)
            throw new IllegalStateException("No NavigationService found, cannot retrieve navigation node.");
        String site = ics.GetVar("site");
        if (StringUtils.isBlank(site))
            throw new IllegalStateException("site is not a valid 'ics' variable.");
        if (StringUtils.isBlank(pagename)) {

            Collection<NavigationNode> nav = nh.getRootNodesForSite(site, depth);
            getJspContext().setAttribute(name, nav);
        } else {
            NavigationNode nav = nh.getNodeByName(site, pagename, depth);
            getJspContext().setAttribute(name, nav);

        }
        depth = 1;
        pagename = null;

    }
}
