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

import java.util.Set;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.satellite.Page;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class GsfCallTemplate {
    private String site;
    private String tname;
    private String assettype;
    private long assetid;
    private String packedargs;
    private Style style;
    private final FTValList arguments = new FTValList();

    public GsfCallTemplate argument(String name, String value) {
        arguments.setValString(name, value);
        return this;
    }

    public String include(ICS ics, FTValList vIn) {
        // required parameters
        Assert.hasText(site, "Site must not be blank.");
        Assert.hasText(tname, "Tname must not be blank.");
        Assert.hasText(assettype, "Assettype must not be blank.");
        if (assetid < 1)
            throw new IllegalArgumentException("Assetid must be set.");

        if (StringUtils.isNotBlank(packedargs)) {
            ics.decode(packedargs, arguments);
        }

        try {
            arguments.setValString("c", assettype);
            arguments.setValString("cid", Long.toString(assetid));
            String rm = ics.GetVar("rendermode");
            if (rm != null) {
                arguments.setValString("rendermode", rm);
            }

            String pagename;
            String elementname;
            if (tname.startsWith("/")) // typeless
            {
                elementname = tname;
                pagename = site + tname;
            } else {
                elementname = assettype + "/" + tname;
                pagename = site + "/" + assettype + "/" + tname;
            }

            ics.ClearErrno();
            switch (style) {
                case element: {
                    ics.PushVars();
                    // pushvars copies current scope into new scope
                    // so remove vars that are undesireable
                    ics.RemoveVar("tid");
                    ics.RemoveVar("context");
                    boolean ret = ics.CallElement(elementname, arguments);
                    ics.PopVars();
                    if (!ret)
                        throw new RuntimeException(ics.getComplexError());
                    return null;
                }
                case embedded: {
                    ics.PushVars();
                    // pushvars copies current scope into new scope
                    // so remove vars that are undesireable
                    ics.RemoveVar("tid");
                    ics.RemoveVar("context");
                    boolean ret = ics.InsertPage(pagename, arguments);
                    ics.PopVars();
                    if (!ret)
                        throw new RuntimeException(ics.getComplexError());

                    return null;

                }
                default: {
                    Page p = new Page();
                    p.setPagename(pagename);

                    @SuppressWarnings("unchecked")
                    Set<String> keys = arguments.keySet();
                    for (String key : keys) {
                        p.set(key, arguments.getValString(key));
                    }

                    return p.execute(ics);
                }
            }
        } finally {
            ics.ClearErrno();
        }

    }

    /**
     * @return the site
     */
    public String getSite() {
        return site;
    }

    /**
     * @param site the site to set
     */
    public void setSite(String site) {
        this.site = site;
    }

    /**
     * @return the tname
     */
    public String getTname() {
        return tname;
    }

    /**
     * @param tname the tname to set
     */
    public void setTname(String tname) {
        this.tname = tname;
    }

    /**
     * @return the assettype
     */
    public String getAssettype() {
        return assettype;
    }

    /**
     * @param assettype the assettype to set
     */
    public void setAssettype(String assettype) {
        this.assettype = assettype;
    }

    /**
     * @return the assetid
     */
    public long getAssetid() {
        return assetid;
    }

    /**
     * @param assetid the assetid to set
     */
    public void setAssetid(long assetid) {
        this.assetid = assetid;
    }

    /**
     * @param id the assetid to set
     */
    public void setAssetid(AssetId id) {
        this.assetid = id.getId();
        this.assettype = id.getType();
    }

    /**
     * @return the packedargs
     */
    public String getPackedArgs() {
        return packedargs;
    }

    /**
     * @param packedargs the packedargs to set
     */
    public void setPackedArgs(String packedargs) {
        this.packedargs = packedargs;
    }

    /**
     * @return the style
     */
    public Style getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(Style style) {
        this.style = style;
    }

}
