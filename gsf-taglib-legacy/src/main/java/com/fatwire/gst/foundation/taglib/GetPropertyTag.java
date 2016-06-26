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

import com.fatwire.gst.foundation.controller.action.support.IcsFactoryUtil;
import com.fatwire.gst.foundation.properties.Property;
import com.fatwire.gst.foundation.properties.PropertyDao;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * extract the asset id from the asset property value
 *
 * @author Tony Field
 * @since 11-09-02
 */
public final class GetPropertyTag extends GsfSimpleTag {

    private String name;
    private String property;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {
    	PropertyDao propertyDao = IcsFactoryUtil.getFactory(getICS()).getObject("propertyDao", PropertyDao.class);
        Property p = propertyDao.getProperty(property);
        if (p != null) {
            getJspContext().setAttribute(name, p.asString());
        }
        super.doTag();
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(final String property) {
        this.property = property;
    }
}