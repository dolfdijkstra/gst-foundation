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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.properties.AssetApiPropertyDao;
import com.fatwire.gst.foundation.properties.PropertyDao;

/**
 * Load property values from the property asset into the page scope
 *
 * @author Tony Field
 * @since 2011-09-02
 */
public class GetPropertiesTag extends GsfSimpleTag {

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().setAttribute("prop", getAllPropsAsMap());
        super.doTag();
    }

    private Map<String, String> getAllPropsAsMap() {
        final ICS ics = getICS();
        PropertyDao propertyDao = AssetApiPropertyDao.getInstance(ics);
        HashMap<String, String> map = new HashMap<String, String>();
        for (String name : propertyDao.getPropertyNames()) {
            map.put(name, propertyDao.getProperty(name).asString());
        }
        return map;
    }

}