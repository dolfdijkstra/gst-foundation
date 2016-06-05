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

import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.ActionLocatorUtils;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.AnnotationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSP tag that replaces the GsfRoot tag and adds Action support. If the action
 * name argument is provided, than a Action is looked up and executed.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class ActionTag extends GsfSimpleTag {
    static final Logger LOG = LoggerFactory.getLogger("com.function1.gsf.foundation.taglib.ActionTag");
    static final Logger LOG_TIME = LoggerFactory.getLogger("com.function1.gsf.foundation.taglib.ActionTag.time");

    private String action;

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.taglib.GsfRootTag#doStartTag()
     */
    @Override
    public void doTag() throws JspException {
        final ICS ics = getICS();

        final long start = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0;
        final ActionLocator locator = getActionLocator();
        if (locator == null)
            throw new IllegalStateException("The ActionLocator cannot be found.");
        if ("+".equals(action)) {
            action = ics.ResolveVariables("CS.elementname") + "_action";
        }
        final Action a = locator.getAction(ics, action);

        if (a != null) {
            if (LOG_TIME.isDebugEnabled()) {

                DebugHelper.printTime(LOG_TIME, "Locating Action " + a.getClass().getName(), start);
            }

            final long beforeHandleRequest = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0;
            a.handleRequest(ics);
            copyModelData(a);
            if (LOG_TIME.isDebugEnabled()) {
                DebugHelper.printTime(LOG_TIME, "Executing Action " + a.getClass().getName(), beforeHandleRequest);
            }
        } else {
            throw new IllegalArgumentException("Action with name '" + action + "' cannot be found.");
        }

    }

    /**
     * Copies the data from the Model to the jsp page scope
     * 
     * @param a the action to copy from.
     */
    private void copyModelData(final Action a) {
        if (a == null) {
            return;
        }
        final Model model = AnnotationUtils.findService(a, Model.class);
        if (model == null) {
            return;
        }
        for (final Entry<String, ?> e : model.entries()) {
            // don't overwrite or worse, delete in case value is null.
            if (getJspContext().getAttribute(e.getKey(), PageContext.PAGE_SCOPE) == null) {
                getJspContext().setAttribute(e.getKey(), e.getValue(), PageContext.PAGE_SCOPE);
            }
        }
    }

    /**
     * @return the ActionLocator
     */
    protected ActionLocator getActionLocator() {

        return ActionLocatorUtils.getActionLocator(getPageContext().getServletContext());
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(final String action) {
        this.action = action;
    }

}
