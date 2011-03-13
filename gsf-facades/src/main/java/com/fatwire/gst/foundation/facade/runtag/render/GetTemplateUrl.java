/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.runtag.render;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the RENDER.GETTEMPLATEURL tag.
 * <p/>
 * &lt;render:gettemplateurl [addsession="true|false"]
 * [assembler="uri assembler shortform"] [authority="authority value"]
 * [c="asset type"] [cid="asset id"] [container="servlet|portlet"]
 * [context="context override"] [deptype="exists|none"] [dynamic="true|false"]
 * [fragment="fragment value"] outstr="theURLVariable"
 * [packedargs="stringFromPACKARGStag"] [satellite="true|false"]
 * [scheme="scheme value"] site="site name" slotname="name of slot"
 * tid="caller Template or CSElement id"
 * [tname="target Template or CSElement name"]
 * [ttype="caller Template or CSElement"] [variant="template variant name"]
 * [wrapperpage="name of uncached wrapper page"] /&gt;
 * <p/>
 * [&lt;render:argument name="variable1" value="value1"/&gt;]
 * <p/>
 * &lt;/render:gettemplateurl&gt;
 * 
 * @author Tony Field
 * @since Nov 17, 2009
 */
public final class GetTemplateUrl extends TagRunnerWithArguments {
    private static Log LOG = LogFactory.getLog(GetTemplateUrl.class);

    public GetTemplateUrl() {
        super("RENDER.GETTEMPLATEURL");
    }

    public void setAsset(String s) {
        set("ASSET", s);
    }

    public void setAddSession(boolean b) {
        set("ADDSESSION", b ? "TRUE" : "FALSE");
    }

    public void setAssembler(String s) {
        set("ASSEMBLER", s);
    }

    public void setAuthority(String s) {
        set("AUTHORITY", s);
    }

    public void setC(String s) {
        set("C", s);
    }

    public void setCid(String s) {
        set("CID", s);
    }

    public void setContainer(String s) {
        set("CONTAINER", s);
    }

    public void setContext(String s) {
        set("CONTEXT", s);
    }

    public void setDeptype(String s) {
        set("DEPTYPE", s);
    }

    public void setDynamic(String s) {
        set("DYNAMIC", s);
    }

    public void setFragment(String s) {
        set("FRAGMENT", s);
    }

    public void setOutstr(String s) {
        set("OUTSTR", s);
    }

    public void setPackedargs(String s) {
        set("PACKEDARGS", s);
    }

    public void setSatellite(String s) {
        set("SATELLITE", s);
    }

    public void setScheme(String s) {
        set("SCHEME", s);
    }

    public void setSite(String s) {
        set("SITE", s);
    }

    public void setSlotname(String s) {
        set("SLOTNAME", s);
    }

    public void setTid(String s) {
        set("TID", s);
    }

    public void setTname(String s) {
        set("TNAME", s);
    }

    public void setTtype(String s) {
        set("TTYPE", s);
    }

    public void setVariant(String s) {
        set("VARIANT", s);
    }

    public void setWrapperpage(String s) {
        LOG.trace("Setting wrapper to :" + s);
        set("wrapperpage", s);
        set("WRAPPERPAGE", s);
    }

    /**
     * Shortcut constructor that sets intelligent default values. Not all values
     * are required. Site and TID/TTYPE are auto-detected if not explicitly
     * specified.
     * 
     * Outstr still needs to be set, and any nested parameters need to be set as
     * well prior to calling execute.
     * 
     * @param ics
     * @param c
     * @param cid
     * @param tname
     * @param wrapper
     * @param slotname
     * @param site
     * @param context
     * @param ttype
     * @param tid
     * @param assembler
     * @param authority
     * @param container
     * @param deptype
     * @param dynamic
     * @param fragment
     * @param packedargs
     * @param satellite
     * @param scheme
     * @param variant
     */
    public GetTemplateUrl(ICS ics, String c, String cid, String tname, String wrapper, String slotname, String site,
            String context, String ttype, String tid, String assembler, String authority, String container,
            String deptype, String dynamic, String fragment, String packedargs, String satellite, String scheme,
            String variant) {
        super("RENDER.GETTEMPLATEURL");

        setC(c);

        setCid(cid);

        setTname(tname);

        if (wrapper != null) {
            setWrapperpage(wrapper);
        }
        setSlotname(slotname);

        if (site == null) {
            site = ics.GetVar("site");
        }
        setSite(site);

        // too bad this can't be nuked automatically
        setContext(context);

        if (tid == null) {
            ttype = "Template";
            tid = ics.GetVar("tid");
            if (!AssetList.assetExists(ics, ttype, tid)) {
                ttype = "CSElement";
                tid = ics.GetVar("eid");
                if (!AssetList.assetExists(ics, ttype, tid)) {
                    throw new IllegalArgumentException(
                            "tid was not specified and neither tid nor eid were found valid in the variable scope");
                }
            }
        }

        setTtype(ttype);
        setTid(tid);

        if (assembler != null) {
            setAssembler(assembler);
        }

        if (authority != null) {
            setAuthority(authority);
        }

        if (container != null) {
            setContainer(container);
        }

        if (deptype != null) {
            setDeptype(deptype);
        }

        if (dynamic != null) {
            setDynamic(dynamic);
        }

        if (fragment != null) {
            setFragment(fragment);
        }

        if (packedargs != null) {
            setPackedargs(packedargs);
        }

        if (satellite != null) {
            setSatellite(satellite);
        }

        if (scheme != null) {
            setScheme(scheme);
        }

        if (variant != null) {
            setVariant(variant);
        }
    }

    /**
     * Creates a GetTemplateUrl tag facade instance that is ready to execute
     * unless any parameters need to be passed (like p, for instance). Note that
     * this will auto-discover tid/ttype, and site, and it sets context to an
     * empty string.
     * 
     * @param ics
     * @param c
     * @param cid
     * @param tname
     * @param wrapper optional wrapper
     * @param slotname
     */
    public GetTemplateUrl(ICS ics, String c, String cid, String tname, String wrapper, String slotname) {
        this(ics, c, cid, tname, wrapper, slotname, null, "", null, null, null, null, null, null, null, null, null,
                null, null, null);
    }
}
