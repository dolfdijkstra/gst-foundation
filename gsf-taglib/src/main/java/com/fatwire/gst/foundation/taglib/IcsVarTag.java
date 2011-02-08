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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.JspTags.Root;

public class IcsVarTag extends BodyTagSupport {

    public static final String variableScopeName = "cs";
    /**
	 * 
	 */
    private static final long serialVersionUID = -5369419132504852400L;

    private static final Log log = LogFactory.getLog(IcsVarTag.class);

    /*
     * (non-Javadoc)
     * 
     * @see COM.FutureTense.JspTags.Root#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        log.debug("doStartTag");

        int ret = super.doStartTag();

        ICS ics = getICS();

        if (ics != null) {
            pageContext.setAttribute(variableScopeName, new ICSAsMap(ics), PageContext.PAGE_SCOPE);

        } else {
            throw new JspException("ics is not found on the page context");
        }
        return ret;
    }

    protected ICS getICS() {
        Object o = pageContext.getAttribute(Root.sICS, PageContext.PAGE_SCOPE);
        if (o instanceof ICS)
            return (ICS) o;
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        pageContext.removeAttribute(variableScopeName, PageContext.PAGE_SCOPE);
        return Tag.EVAL_PAGE;
    }

}
