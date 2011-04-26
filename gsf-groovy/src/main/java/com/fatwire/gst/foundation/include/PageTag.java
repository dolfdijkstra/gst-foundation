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

import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.ActionLocatorUtils;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.taglib.GsfRootTag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class PageTag extends GsfRootTag {
    static final Log LOG = LogFactory.getLog(PageTag.class.getPackage().getName());
    static final Log LOG_TIME = LogFactory.getLog(PageTag.class.getPackage().getName() + ".time");

    /**
     * 
     */
    private static final long serialVersionUID = -5979095889062674072L;
    private String action;
    private JspIncludeService includeService;

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.taglib.GsfRootTag#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.taglib.GsfRootTag#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        final int r = super.doStartTag();
        final ICS ics = getICS();

        if (action != null) {
            final long start = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0;
            final ActionLocator locator = getActionLocator();

            final Action a = locator.getAction(ics, action);

            if (a != null) {
                if (LOG_TIME.isDebugEnabled()) {

                    DebugHelper.printTime(LOG_TIME, "Locating Action " + a.getClass().getName(), start);
                }

                includeService = (JspIncludeService) findService(a, IncludeService.class);
                if (includeService == null && LOG.isDebugEnabled()) {
                    LOG.debug("includeService is null");
                }
                final long midway = LOG_TIME.isDebugEnabled() ? System.nanoTime() : 0;
                a.handleRequest(ics);
                copyModelData(a);
                if (LOG_TIME.isDebugEnabled()) {
                    DebugHelper.printTime(LOG_TIME, "Executing Action " + a.getClass().getName(), midway);
                }
            } else {
                throw new IllegalArgumentException("Action with name '" + action + "' can't be found.");
            }

        }
        return r;
    }

    /**
     * Copies the data from the Model to the jsp page scope
     * 
     * @param a
     */
    private void copyModelData(final Action a) {
        if (a == null) {
            return;
        }
        final Model model = findService(a, Model.class);
        if (model == null) {
            return;
        }
        for (final Entry<String, ?> e : model.entries()) {
            // don't overwrite or worse, delete in case value is null.
            if (pageContext.getAttribute(e.getKey()) == null) {
                pageContext.setAttribute(e.getKey(), e.getValue());
            }
        }
    }

    /**
     * 
     * 
     * @param <T> the class of the object that is returned .
     * @param object the object containing the object to find.
     * @param clazz the Class of the type that is searched for.
     * @return the object that is present on the field with the InjectForRequest
     *         annotation.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findService(final Object object, final Class<T> clazz) {
        Class<?> klazz = object.getClass();
        while (klazz != null && klazz != Object.class) {
            for (final Field field : object.getClass().getDeclaredFields()) {
                if (field.getAnnotation(InjectForRequest.class) != null && clazz.isAssignableFrom(field.getType())) {
                    try {
                        return (T) field.get(object);
                    } catch (final IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            klazz = klazz.getSuperclass();
        }
        return null;
    }

    public static <T> Field findField(final Object a, final Class<T> type) {
        Class<?> klazz = a.getClass();
        while (klazz != null && klazz != Object.class) {
            for (final Field field : a.getClass().getDeclaredFields()) {
                if (field.getAnnotation(InjectForRequest.class) != null && type.isAssignableFrom(field.getType())) {
                    try {
                        return field;
                    } catch (final IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            klazz = klazz.getSuperclass();
        }
        return null;
    }

    protected ActionLocator getActionLocator() {
        return ActionLocatorUtils.getActionLocator(pageContext.getServletContext());
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

    /**
     * This is a discovery method to be used by the IncludeTag class. The
     * contract is that PageTag and IncludeTag work together and that the
     * includeService is made availble on the PageTag jsp tag as loaded by the
     * jsp page. An alternative would be to add this object to the
     * <tt>pageContext</tt>. It was chosen not to do this as maintaining it on
     * the tag reduces scope and possible intermingling by the accidental
     * developer.
     * 
     * @return the IncludeService
     */
    public JspIncludeService getJspIncludeService() {
        return includeService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
     */
    @Override
    public void release() {
        includeService = null;
        super.release();
    }
}
