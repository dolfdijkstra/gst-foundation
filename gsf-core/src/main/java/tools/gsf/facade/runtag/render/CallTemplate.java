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

package tools.gsf.facade.runtag.render;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;
import COM.FutureTense.Util.ftErrors;
import com.fatwire.assetapi.data.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.runtag.TagRunnerRuntimeException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CallTemplate tag with many improvements around context and style.
 * <p>
 * <code>
 * &lt;RENDER.CALLTEMPLATE SITE="site name"
 * SLOTNAME="name of slot"
 * TID="caller Template or CSElement id" [TTYPE="caller Template or CSElement"]
 * [C="asset type"] [CID="asset id"] [TNAME="target Template or CSElement name"]
 * [CONTEXT="context override"] [STYLE="pagelet or element"]
 * [VARIANT="template variant name"] [PACKEDARGS="packed arguments"]&gt;
 * [&lt;RENDER.ARGUMENT NAME="variable1" VALUE="value1"/&gt;]
 * &lt;/RENDER.CALLTEMPLATE&gt;
 * </code>
 * 
 * <p>
 * <b>
 * MAIN CHANGES WITH REGARDS TO LEGACY render:calltemplate FACADE:
 * <ul>
 * <li>"override" property / mechanism has been removed from this tag.</li>
 * <li>Default "style" property / mechanism has been removed from this tag.</li>
 * <li>Style calculation intelligence has been removed from this tag. Style must now be explicitly set by the caller; otherwise, this facade will throw an Exception.</li>
 * <li></li>
 * </ul>
 * </b>
 * </p>
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @author Freddy Villalba
 * @since Aug 15, 2016
 */
public class CallTemplate extends TagRunnerWithRenderArguments {

    private static Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.runtag.render.CallTemplate");

    static private boolean configLoaded = false;
    /**
     * Do not use the user provided value for style, override
     */
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
     * Sets up CallTemplate.
     * 
     * <b>IMPORTANT: note that Style is now required, as opposed to previous versions of the GSF. Also, bear in mind that,
     * in WCS 12c, using style="element" implies the called Template's Controller does NOT get invoked at all.</b> 
     *
     * @param slotname slot name
     * @param tname    template name
     * @param type     template type
     * @param style    call style (e.g. pagelet, embedded or element) 
     */
    public CallTemplate(final String slotname, final String tname, final Type type, final CallTemplate.Style style) {
        super("RENDER.CALLTEMPLATE");
        setSlotname(slotname);
        setTname(tname);
        setTtype(type);
        setStyle(style);
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
    	
    	// if style has not been explicitly set, bomb out
    	if (this.style == null) {
    		throw new IllegalStateException("Starting GSF-12, you must explicitly set 'style'. Also, bear in mind that, in WCS 12c, calling a template with style='element' prevents the corresponding Controller from getting invoked.");
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

    protected static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Collections.unmodifiableList(Arrays.asList("TNAME",
            "C", "CID", "EID", "SEID", "PACKEDARGS", "VARIANT", "CONTEXT", "SITE", "TID", "rendermode", "ft_ss",
            "SystemAssetsRoot", "cshttp", "errno", "tablename", "empty", "errdetail", "null"));

    protected void handleError(ICS ics) {

        int errno = ics.GetErrno();
        if (errno != -10004) // error checking was too aggressive, any error set
        // by an element would leak into this handler
        {
            return;
        }
        FTValList arguments = list;
        ftErrors complexError = ics.getComplexError();
        String pagename = ics.GetVar("pagename");
        String elementname = ics.ResolveVariables("CS.elementname");
        String msg = "ics.runTag(RENDER.CALLTEMPLATE) failed for tname: " + list.getValString("TNAME") + " for asset: "
                + list.getValString("C") + ":" + list.getValString("CID") + " within pagename:" + pagename;
        if (elementname != null) {
            msg += " and element:" + elementname;
        }
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
