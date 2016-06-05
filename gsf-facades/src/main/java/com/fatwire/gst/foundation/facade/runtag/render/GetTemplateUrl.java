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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the RENDER.GETTEMPLATEURL tag.
 * <p>
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
 * <p>
 * [&lt;render:argument name="variable1" value="value1"/&gt;]
 * <p>
 * &lt;/render:gettemplateurl&gt;
 * 
 * @author Tony Field
 * @since Nov 17, 2009
 * @author Dolf Dijkstra
 * @since Mar 13, 2011
 */
public final class GetTemplateUrl extends TagRunnerWithArguments {
    private static Logger LOG = LoggerFactory.getLogger("com.function1.gsf.foundation.facade.runtag.render.GetTemplateUrl");

    /**
     * 
     */
    public GetTemplateUrl() {
        super("RENDER.GETTEMPLATEURL");
    }

    /**
     * @param s string value for asset
     */
    public void setAsset(String s) {
        set("ASSET", s);
    }

    /**
     * @param b boolean value for ADDSESSION
     */
    public void setAddSession(boolean b) {
        set("ADDSESSION", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s string value for assembler
     */
    public void setAssembler(String s) {
        set("ASSEMBLER", s);
        set("assembler", s);
    }

    /**
     * @param s string value for AUTHORITY
     */
    public void setAuthority(String s) {
        set("AUTHORITY", s);
    }

    /**
     * @param s string value for "C", current asset
     */
    public void setC(String s) {
        set("C", s);
    }

    /**
     * @param s string value for CID, current asset id
     */
    public void setCid(String s) {
        set("CID", s);
    }

    /**
     * @param s string value for container
     */
    public void setContainer(String s) {
        set("CONTAINER", s);
    }

    /**
     * @param s string value for context
     */
    public void setContext(String s) {
        set("CONTEXT", s);
    }

    /**
     * @param s string value for DEPTYPE
     */
    public void setDeptype(String s) {
        set("DEPTYPE", s);
    }

    /**
     * @param b string value TRUE|FALSE for DYNAMIC
     */
    public void setDynamic(boolean b) {
        set("DYNAMIC", b ? "TRUE" : "FALSE");
    }
    /**
     * @param b value for DYNAMIC
     * @deprecated use {@link #setDynamic(boolean)}.
     */
    public void setDynamic(String b) {
        set("DYNAMIC", b );
    }

    /**
     * @param s string value for FRAGMENT
     */
    public void setFragment(String s) {
        set("FRAGMENT", s);
    }

    /**
     * @param s string value for OUTSTR
     */
    public void setOutstr(String s) {
        set("OUTSTR", s);
    }

    /**
     * @param s string value for packed arguments
     */
    public void setPackedargs(String s) {
        set("PACKEDARGS", s);
    }

    /**
     * @param b boolean value for SATELLITE, TRUE|FALSE
     */
    public void setSatellite(boolean b) {
        set("SATELLITE", b ? "TRUE" : "FALSE");
    }

    /**
     * @param b boolean for SATELLITE
     * @deprecated use {@link #setSatellite(boolean)}.
     */
    public void setSatellite(String b) {
        set("SATELLITE", b );
    }

    /**
     * @param s string value for SCHEME
     */
    public void setScheme(String s) {
        set("SCHEME", s);
    }

    /**
     * @param s string value for site
     */
    public void setSite(String s) {
        set("SITE", s);
    }

    /**
     * @param s string value for SLOTNAME
     */
    public void setSlotname(String s) {
        set("SLOTNAME", s);
    }

    /**
     * @param s string value for tid, template id
     */
    public void setTid(String s) {
        set("TID", s);
    }

    /**
     * @param s string value for tname, template name
     */
    public void setTname(String s) {
        set("TNAME", s);
    }

    /**
     * @param s string value for ttype, template type
     */
    public void setTtype(String s) {
        set("TTYPE", s);
    }

    /**
     * @param s string value for variant
     */
    public void setVariant(String s) {
        set("VARIANT", s);
    }

    /**
     * @param s string value for wrapper page
     */
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
     * @param ics Content Server context object
     * @param c current asset
     * @param cid content id
     * @param tname template name
     * @param wrapper wrapper
     * @param slotname slot name
     * @param site name of the site
     * @param context web context
     * @param ttype template type
     * @param tid template id
     * @param assembler assembler
     * @param authority authority
     * @param container container
     * @param deptype dep type
     * @param dynamic dynamic
     * @param fragment fragment
     * @param packedargs packed arguments
     * @param satellite satellite
     * @param scheme scheme
     * @param variant variant
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
            setDynamic("false".equalsIgnoreCase(dynamic));
        }

        if (fragment != null) {
            setFragment(fragment);
        }

        if (packedargs != null) {
            setPackedargs(packedargs);
        }

        if (satellite != null) {
            setSatellite("false".equalsIgnoreCase(satellite));
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
     * @param ics Content Server context object
     * @param c current asset
     * @param cid content id
     * @param tname template name
     * @param wrapper optional wrapper
     * @param slotname slot name
     */
    public GetTemplateUrl(ICS ics, String c, String cid, String tname, String wrapper, String slotname) {
        this(ics, c, cid, tname, wrapper, slotname, null, "", null, null, null, null, null, null, null, null, null,
                null, null, null);
    }
}
