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
package com.fatwire.gst.foundation.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.include.DefaultIncludeService;
import com.fatwire.gst.foundation.include.Include;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Includes a template,element or page in a jsp page.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * 
 * 
 * @deprecated as of release 12.x
 * 
 */
public final class IncludeTag extends GsfSimpleTag {

    static final Logger LOG = LoggerFactory.getLogger("tools.gsf.legacy.taglib.IncludeTag");

    private String name;
    private boolean silent = false;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        final ICS ics = getICS();

        final Include inc = discover(name);
        if (inc != null) {
            inc.include(ics);
        } else if (!silent) {
            throw new IllegalStateException("Can't find object to include with the name " + name);
        }

        super.doTag();
    }

    protected Include discover(final String name) {
        final DefaultIncludeService s = findService();
        if (s == null)
            throw new IllegalStateException(
                    "Can't find DefaultIncludeService from a parent tag. Does your Action have a @InjectForRequest field of type IncludeService."
                            + " This is required to make use of the include tag in your jsp.");
            
        return s.find(name);
    }

    protected DefaultIncludeService findService() {
        final JspTag parent = SimpleTagSupport.findAncestorWithClass(this, PageTag.class);
        if (parent instanceof PageTag) {
            final PageTag t = (PageTag) parent;
            return t.getJspIncludeService();
        }
        throw new IllegalStateException("Cannot find a parent JSP tag of type PageTag. Is the include tag nested in a gsf:root tag?");

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @param silent the silent to set
     */
    public void setSilent(final boolean silent) {
        this.silent = silent;
    }

}
