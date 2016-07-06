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

import com.fatwire.gst.foundation.taglib.MultilingualGsfSimpleTag;
import com.fatwire.gst.foundation.wra.navigation.NavNode;
import com.fatwire.gst.foundation.wra.navigation.NavigationHelper;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * jsp tag to generate the navigation data structure for multilingual site
 * plans.
 * 
 * @author Tony Field
 * 
 * 
 * @deprecated as of release 12.x, temporarily, until a brand new, significantly improved NavigationService implementation is released (soon)
 * 
 */
public final class MultilingualNavigationTag extends MultilingualGsfSimpleTag {

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

    public void doTag() throws JspException, IOException {
        LOG.trace("gsf:multilingual-navigation start");

        DimensionFilterInstance filter = getDimensionFilter();
        final NavigationHelper nh = new NavigationHelper(getICS());
        final NavNode nav = nh.getSitePlanByPage(depth, pagename, filter);
        getJspContext().setAttribute(name, nav);
        super.doTag();

        LOG.trace("gsf:multilingual-navigation end");
    }
}
