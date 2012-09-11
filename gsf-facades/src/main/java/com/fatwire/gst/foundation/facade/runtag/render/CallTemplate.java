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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.TagRunnerRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CallTemplate tag with many improvements around context and style.
 * <p/>
 * <code>
 * &lt;RENDER.CALLTEMPLATE SITE="site name"
 * SLOTNAME="name of slot"
 * TID="caller Template or CSElement id" [TTYPE="caller Template or CSElement"]
 * [C="asset type"] [CID="asset id"] [TNAME="target Template or CSElement name"]
 * [CONTEXT="context override"] [STYLE="pagelet or element"]
 * [VARIANT="template variant name"] [PACKEDARGS="packed arguments"]&gt;
 * <p/>
 * [&lt;RENDER.ARGUMENT NAME="variable1" VALUE="value1"/&gt;]
 * <p/>
 * &lt;/RENDER.CALLTEMPLATE&gt;
 * </code>
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class CallTemplate extends TagRunnerWithRenderArguments {

    private static Log LOG = LogFactory.getLog(CallTemplate.class.getPackage().getName());

    static private boolean configLoaded = false;
    /**
     * The default style, used if present
     */
    static private Style defaultStyle = null;
    /**
     * Do not use the user provided value for style, override
     */
    static private boolean override = true;
    static private boolean config_FixPageCriteria = false;
    private boolean fixPageCriteria = false;
    private String site, type, tname, cid;
    private Style style;

    public enum Style {
        element, pagelet, embedded
    }

    public enum Type {
        Template, CSElement
    }

    public CallTemplate() {
        super("RENDER.CALLTEMPLATE");
    }

    /**
     * Sets up CallTemplate with default <tt>Style.element</tt>
     * 
     * @param slotname
     * @param tname
     * @param type
     */
    public CallTemplate(final String slotname, final String tname, final Type type) {
        super("RENDER.CALLTEMPLATE");
        setSlotname(slotname);
        setTname(tname);
        setTtype(type);
        setContext("");
    }

    /**
     * Checks the current settings and based on the current and target template
     * state set the style to a best guess. This is only done if the developer
     * didnot explicitly set the style.
     */
    @Override
    protected void preExecute(final ICS ics) {
        // consider to set site to ics.GetVar("site"); as helper for reasonable

        // default value
        readConfig(ics);

        if (defaultStyle != null) {
            setStyle(defaultStyle);
        } else if (override || style == null) {
            final Style newStyle = proposeStyle(ics);
            setStyle(newStyle);
        }
        ics.ClearErrno();
        super.preExecute(ics);
    }

    @Override
    protected void postExecute(ICS ics) {
        site = null;
        type = null;
        tname = null;
        cid = null;
        style = null;
        super.postExecute(ics);
    }

    public void setSite(final String s) {
        set("SITE", s);
        site = s;
    }

    public void setSlotname(final String s) {
        set("SLOTNAME", s);
    }

    public void setTid(final String s) {
        set("TID", s);
    }

    public void setTtype(final Type s) {
        set("TTYPE", s.toString());
    }

    public void setC(final String s) {
        set("C", s);
        type = s;
    }

    public void setCid(final String s) {
        set("CID", s);
        cid = s;
    }

    public void setTname(final String s) {
        set("TNAME", s);
        tname = s;
    }

    public void setContext(final String s) {
        set("CONTEXT", s);
    }

    public void setStyle(final Style s) {
        set("STYLE", s != null ? s.toString() : null);
        style = s;
    }

    public void setVariant(final String s) {
        set("VARIANT", s);
    }

    public void setPackedargs(final String s) {
        // todo: this may need more work
        set("PACKEDARGS", s);
    }

    public void setAsset(final AssetId id) {
        setC(id.getType());
        setCid(Long.toString(id.getId()));
    }

    void readConfig(final ICS ics) {
        if (configLoaded) {
            return;
        }
        final String tmp = getProperty(ics, "style");
        if (tmp != null) {
            CallTemplate.defaultStyle = Style.valueOf(tmp);
        }
        CallTemplate.override = "true".equals(getProperty(ics, "override"));
        CallTemplate.config_FixPageCriteria = "true".equals(getProperty(ics, "config_FixPageCriteria"));
        CallTemplate.configLoaded = true;

    }

    private String getProperty(final ICS ics, final String name) {
        String val = System.getProperty(CallTemplate.class.getName() + "." + name);
        if (!Utilities.goodString(val)) {
            val = ics.GetProperty(CallTemplate.class.getName() + "." + name, "futuretense_xcel.ini", true);
        }
        LOG.trace(CallTemplate.class.getName() + "." + name + "=" + val);
        return val;
    }

    public Style proposeStyle(final ICS ics) {

        /**
         * Considerations 1) Check target for parameter callstyle and use that
         * 
         */
        String pname = getTargetPagename();

        // String targetStyle =(String)
        // ics.getPageData(pname).getDefaultArguments().get("callstyle");
        final boolean targetCached = RenderUtils.isCacheable(ics, pname);
        final boolean currentCached = RenderUtils.isCacheable(ics, ics.GetVar(ftMessage.PageName));

        final Style proposal = calculateStyle(ics, pname, currentCached, targetCached);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting style to '" + proposal + (style != null ? "' (user did set '" + style + "')" : "'")
                    + " for calltemplate to '" + pname + "' with " + type + "," + cid + "," + getList()
                    + " in element: '" + ics.ResolveVariables("CS.elementname") + "', caching: '" + currentCached + "/"
                    + targetCached + "', page: " + ics.pageURL());
        }
        return proposal;

    }

    private String getTargetPagename() {
        String pname;

        if (tname.startsWith("/")) // typeless
        {
            pname = site + tname;
        } else {
            pname = site + "/" + type + "/" + tname;
        }
        return pname;
    }

    private Style calculateStyle(final ICS ics, final String pname, final boolean currentCache,
            final boolean targetCache) {
        if (currentCache == false) // we are not caching for the current pagelet
        {
            if (targetCache == false) {
                return Style.element; // call as element is target is also not
                // cacheable
            } else {
                checkPageCriteria(ics, pname);
                return Style.pagelet; // otherwise call as pagelet
            }

        } else { // currently we are caching

            if (targetCache == false) {
                checkPageCriteria(ics, pname);
                return Style.pagelet;
            } else {
                // LOG.debug("getvar.cid=" + ics.GetVar("cid") + " at " +
                // ics.pageURL());

                final FTValList m = COM.FutureTense.Interfaces.Utilities.getParams(ics.pageURL());
                final String pageCid = m.getValString("cid");
                if (pageCid != null && !pageCid.equals(ics.GetVar("cid"))) {
                    LOG.warn(ics.GetVar("cid") + " does not match cid (" + pageCid + ") in " + ics.pageURL());
                }
                // should we check if cid is current page criteria, we are a
                // Template??
                if (cid != null && cid.equals(pageCid)) {
                    // if c/cid does not change than we call this as an element,
                    // as reuse is unlikely
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Calling " + pname + " as an element from " + ics.ResolveVariables("CS.elementname")
                                + " because cid is same as on current pagelet.");
                    }
                    return Style.element;
                } else {
                    checkPageCriteria(ics, pname);
                    return Style.embedded; // this is calltemplate, assuming
                    // that
                    // headers/footers/leftnavs etc will be
                    // via CSElements/SiteEntry
                }
            }

        }
    }

    protected static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Collections.unmodifiableList(Arrays.asList("TNAME",
            "C", "CID", "EID", "SEID", "PACKEDARGS", "VARIANT", "CONTEXT", "SITE", "TID", "rendermode", "ft_ss",
            "SystemAssetsRoot", "cshttp", "errno", "tablename", "empty", "errdetail", "null"));

    @SuppressWarnings("unchecked")
    private void checkPageCriteria(final ICS ics, final String target) {
        final FTValList o = getList();

        if (o != null) {
            String[] pc = ics.pageCriteriaKeys(target);
            if (pc == null) {
                pc = new String[0];
            }
            final Map<String, ?> m = o;
            for (final Iterator<?> i = m.entrySet().iterator(); i.hasNext();) {
                final Entry<String, ?> e = (Entry<String, ?>) i.next();
                final String key = e.getKey();
                // only inspect arguments that start with ARGS_
                if (key.startsWith(ARGS)) {

                    String shortKey = key.substring(ARGS.length());
                    boolean found = CALLTEMPLATE_EXCLUDE_VARS.contains(shortKey);
                    if (!found) {
                        for (final String c : pc) {
                            if (c.equalsIgnoreCase(shortKey)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        LOG.error("Argument '" + key + "' not found as PageCriterium on " + target
                                + ". Calling element is " + ics.ResolveVariables("CS.elementname")
                                + ". Arguments are: " + m.keySet().toString() + ". PageCriteria: " + Arrays.asList(pc),
                                new Exception());
                        // we could correct this by calling as an element
                        // or by removing the argument
                        if (isFixPageCriteria() || config_FixPageCriteria) {
                            i.remove();
                            LOG.warn("Argument '" + key + "' is removed from the call to '" + target
                                    + "' as it is not a PageCriterium.");
                        }

                    }
                }
            }

        }

    }

    protected void handleError(ICS ics) {

        int errno = ics.GetErrno();
        if (errno != -10004) // error checking was too aggressive, any error set
                             // by an element would leak into this handler
            return;
        FTValList arguments = list;
        ftErrors complexError = ics.getComplexError();
        String pagename = ics.GetVar("pagename");
        String elementname = ics.ResolveVariables("CS.elementname");
        String msg = "ics.runTag(RENDER.CALLTEMPLATE) failed for tname: " + list.getValString("TNAME") + " for asset: "
                + list.getValString("C") + ":" + list.getValString("CID") + " within pagename:" + pagename;
        if (elementname != null)
            msg += " and element:" + elementname;
        msg += ".";
        ics.ClearErrno();
        throw new TagRunnerRuntimeException(msg, errno, arguments, complexError, pagename, elementname);
    }

    /**
     * @return the fixPageCriteria
     */
    public boolean isFixPageCriteria() {
        return fixPageCriteria;
    }

    /**
     * @param fixPageCriteria the fixPageCriteria to set
     */
    public void setFixPageCriteria(boolean fixPageCriteria) {
        this.fixPageCriteria = fixPageCriteria;
    }

}
