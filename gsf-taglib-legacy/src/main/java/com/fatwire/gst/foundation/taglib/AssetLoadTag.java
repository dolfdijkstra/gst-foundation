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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;

import org.apache.commons.lang3.StringUtils;

public class AssetLoadTag extends GsfSimpleTag {

    private String c;
    private long cid;
    private String attributes;
    private String name;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    @Override
    public void doTag() throws JspException, IOException {

        final ICS ics = getICS();
        final ScatteredAssetAccessTemplate t = new ScatteredAssetAccessTemplate(ics);
        // todo: medium: find in page context if it's there (due to presence of
        // gsf:root
        if (StringUtils.isBlank(c) || cid == 0) {
            if (StringUtils.isBlank(attributes)) {
                getJspContext().setAttribute(name, t.readCurrent());
            } else {
                getJspContext().setAttribute(name, t.readCurrent(attributes.split(",")));
            }

        } else {
            final AssetId id = t.createAssetId(c, cid);
            if (StringUtils.isBlank(attributes)) {
                getJspContext().setAttribute(name, t.read(id));
            } else {
                getJspContext().setAttribute(name, t.read(id, attributes.split(",")));
            }

        }

        super.doTag();
    }

    /**
     * @param c the c to set
     */
    public void setC(String c) {
        this.c = c;
    }

    /**
     * @param cid the cid to set
     */
    public void setCid(final long cid) {
        this.cid = cid;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(final String attributes) {
        this.attributes = attributes;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
