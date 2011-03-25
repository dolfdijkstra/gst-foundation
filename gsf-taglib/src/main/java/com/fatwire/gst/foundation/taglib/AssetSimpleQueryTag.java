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
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.JspTags.Root;

import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;

public class AssetSimpleQueryTag extends SimpleTagSupport {

    private String attributes;
    private String list;
    private String query;
    private String c;
    private String subtype;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        ICS ics = (ICS) this.getJspContext().getAttribute(Root.sICS);
        ScatteredAssetAccessTemplate t = new ScatteredAssetAccessTemplate(ics);

        if (StringUtils.isBlank(attributes)) {
            getJspContext().setAttribute(list, t.query(c, subtype, query));
        } else {
            getJspContext().setAttribute(list, t.query(c, subtype, query,attributes.split(",")));
        }

        super.doTag();
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    /**
     * @param list the list to set
     */
    public void setList(String list) {
        this.list = list;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

}
