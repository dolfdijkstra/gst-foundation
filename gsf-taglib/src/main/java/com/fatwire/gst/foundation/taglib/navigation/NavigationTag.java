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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.taglib.GsfRootTag;
import com.fatwire.gst.foundation.taglib.NavNode;
import com.fatwire.gst.foundation.taglib.NavigationHelper;

/**
 * 
 * jsp tag to generate the navigation data structure.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class NavigationTag extends SimpleTagSupport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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

        final ICS ics = (ICS) this.getJspContext().getAttribute(GsfRootTag.ICS_VARIABLE_NAME);
        final NavigationHelper nh = new NavigationHelper(ics);

        final NavNode nav = nh.getSitePlanByPage(depth, pagename);
        getJspContext().setAttribute(name, nav);
        depth = 1;
        super.doTag();
    }

}
