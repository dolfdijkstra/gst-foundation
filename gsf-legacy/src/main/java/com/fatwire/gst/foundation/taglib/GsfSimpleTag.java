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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.fatwire.gst.foundation.controller.action.support.IcsFactoryUtil;

import COM.FutureTense.Interfaces.ICS;

/**
 * Abstract class, usefull to get to ICS in a SimpleTag.
 * 
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 * 
 * @deprecated as of release 12.x
 * 
 */
public abstract class GsfSimpleTag extends SimpleTagSupport {

    protected final ICS getICS() {
        final Object o = getJspContext().getAttribute(GsfRootTag.ICS_VARIABLE_NAME, PageContext.PAGE_SCOPE);
        if (o instanceof ICS) {
            return (ICS) o;
        }
        throw new RuntimeException("Can't find ICS object on the page context.");
    }

    protected final PageContext getPageContext() {
        return (PageContext) getJspContext();
    }

    protected final <T> T getService(String name, Class<T> type) {
        return IcsFactoryUtil.getFactory(getICS()).getObject(name, type);
    }

}
