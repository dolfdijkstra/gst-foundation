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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.taglib.NavNode;
import com.fatwire.gst.foundation.taglib.NavigationHelper;
import com.openmarket.framework.jsp.Base;

/**
 * 
 * jsp tag to generate the navigation data structure.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class NavigationTag extends Base {

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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @param pagename the pagename to set
     */
    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.framework.jsp.Base#doEndTag(COM.FutureTense.Interfaces
     * .ICS, boolean)
     */
    @Override
    protected int doEndTag(ICS ics, boolean debug) throws Exception {
        NavigationHelper nh = new NavigationHelper(ics);

        NavNode nav = nh.getSitePlanByPage(depth, pagename);
        pageContext.setAttribute(name, nav);
        depth=1;
        return Base.EVAL_PAGE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.openmarket.framework.jsp.Base#doStartTag(COM.FutureTense.Interfaces
     * .ICS, boolean)
     */
    @Override
    protected int doStartTag(ICS arg0, boolean arg1) throws Exception {
        
        return SKIP_BODY;
    }

}
