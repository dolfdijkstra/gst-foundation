package com.fatwire.gst.foundation.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.RenderUtils;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;
import com.fatwire.gst.foundation.facade.runtag.render.SatellitePage;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.wra.Alias;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.VanityAsset;
import com.fatwire.gst.foundation.wra.VanityAssetBean;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;
import com.openmarket.xcelerate.publish.PubConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

public class BaseRenderPage {
    public static final String PACKEDARGS = "packedargs";

    protected static final Log LOG = LogFactory.getLog("com.fatwire.gst.foundation.controller");
    protected WraPathTranslationService pathTranslationService;
    protected WraCoreFieldDao wraCoreFieldDao;
    protected AliasCoreFieldDao aliasCoreFieldDao;
    protected ICS ics;
    private static final List<String> CALLTEMPLATE_EXCLUDE_VARS = Arrays.asList("c", "cid", "eid", "seid", PACKEDARGS,
            "variant", "context", "pagename", "childpagename", "site", "tid", "virtual-webroot", "url-path",
            "SystemAssetsRoot", "rendermode", "cshttp", "errno", "tablename", "empty", "ft_ss", "errdetail", "null");

    public BaseRenderPage() {
        super();
    }

    protected void callPage(AssetIdWithSite id, String pagename, String packedArgs) {
        final SatellitePage sp = new SatellitePage(pagename);
        if (StringUtils.isNotBlank(packedArgs))
            sp.setPackedArgs(packedArgs);
        sp.setArgument(PubConstants.c, id.getType());
        sp.setArgument(PubConstants.cid, id.getId());
        String[] pc = ics.pageCriteriaKeys(pagename);
        if (pc != null) {
            for (String p : pc) {
                if (!CALLTEMPLATE_EXCLUDE_VARS.contains(p)) {
                    // add only if page critera and not excluded
                    sp.setArgument(p, ics.GetVar(p));
                }
            }
        }
        String s = sp.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }

    }

    protected void callTemplate(final AssetIdWithSite id, final String tname) {
        final CallTemplate ct = new CallTemplate();
        // ct.setFixPageCriteria(true);
        ct.setSite(id.getSite());
        ct.setSlotname("wrapper");
        ct.setTid(ics.GetVar("eid"));
        // ct.setTtype(CallTemplate.Type.CSElement);
        ct.setAsset(id);
        ct.setTname(tname);
        ct.setContext("");
        ct.setArgument("site", id.getSite());

        final String packedargs = ics.GetVar(PACKEDARGS);
        if (StringUtils.isNotBlank(packedargs)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("packedargs is " + packedargs);
            }
            ct.setPackedargs(massagePackedArgs(packedargs));
        }

        // typeless or not...
        final String targetPagename = tname.startsWith("/") ? (id.getSite() + tname) : (id.getSite() + "/"
                + id.getType() + "/" + tname);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Target pagename is " + targetPagename);
        }

        // create a list of parameters that can be specified as arguments to the
        // CallTemplate tag.
        final Map<String, String> arguments = new HashMap<String, String>();

        // Prime the map with the ics variable scope for the architect to make
        // the controller as transparent as possible
        String[] pageKeys = ics.pageCriteriaKeys(targetPagename);
        if (pageKeys != null) {
            for (final String pcVarName : pageKeys) {
                if (!CALLTEMPLATE_EXCLUDE_VARS.contains(pcVarName) && StringUtils.isNotBlank(ics.GetVar(pcVarName))) {
                    arguments.put(pcVarName, ics.GetVar(pcVarName));
                }
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("PageCriteria for " + targetPagename + " is null.");
            }
        }

        // allow users to set their own
        getCallTemplateArguments(id, arguments);

        // add them to the tag
        for (final Entry<String, String> e : arguments.entrySet()) {
            ct.setArgument(e.getKey(), e.getValue());
            if (LOG.isTraceEnabled()) {
                LOG.trace("CallTemplate param added: " + e.getKey() + "=" + e.getValue());
            }
        }

        // override calltemplate call style
        imposeCallTemplateStyle(ct, targetPagename);

        final String s = ct.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }
    }

    /**
     * Add p to the input parameters, if it is known or knowable. First check to
     * see if it has been explicitly set, then look it up if it hasn't been. The
     * variable is not guaranteed to be found.
     * 
     * @param id asset id with site
     * @param arguments calltemplate arguments
     */
    protected final void findAndSetP(final AssetIdWithSite id, final Map<String, String> arguments) {
        final String pVar = ics.GetVar("p");
        if (pVar != null && pVar.length() > 0) {
            arguments.put("p", pVar);
        } else {
            final long p = wraCoreFieldDao.findP(id);
            if (p > 0L) {
                arguments.put("p", Long.toString(p));
            }
        }
    }

    /**
     * This method collects additional arguments for the CallTemplate call. New
     * arguments are added to the map as name-value pairs.
     * 
     * @param id AssetIdWithSite object
     * @param arguments Map<String,String> containing arguments for the nested
     *            CallTemplate call
     */
    protected void getCallTemplateArguments(AssetIdWithSite id, Map<String, String> arguments) {
        findAndSetP(id, arguments);
    }

    protected Style getCallTemplateCallStyle(String target) {
        if (RenderUtils.isCacheable(ics, ics.GetVar(ftMessage.PageName))) {
            // call as element when current is caching.
            // note that it may be useful to set this to "embedded" in some
            // cases.
            // override it in that situation
            return Style.element;
        } else if (RenderUtils.isCacheable(ics, target)) {
            // call as embedded when current is not caching and target is
            return Style.embedded;
        } else {
            // call as element when current is not caching and target is not
            return Style.element;
        }
    }

    /**
     * Load the WRA, or the alias, and return it for use by the controller. If
     * the asset is not found, an exception is thrown
     * 
     * @param id asset id
     * @return WRA, never null. May be an instance of an Alias
     */
    protected VanityAsset getWraAndResolveAlias(AssetIdWithSite id) {
        try {
            if (Alias.ALIAS_ASSET_TYPE_NAME.equals(id.getType())) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading alias: " + id);
                Alias alias = aliasCoreFieldDao.getAlias(id);
                VanityAssetBean wra = new VanityAssetBean(alias);

                if (LOG.isDebugEnabled())
                    LOG.debug("Loaded alias: " + id + " which resolved to " + wra.getId());
                return wra;
            } else {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading wra: " + id);
                return wraCoreFieldDao.getVanityWra(id);
            }
        } catch (IllegalArgumentException e) {
            throw new CSRuntimeException("Web-Referenceable Asset " + id + " is not valid", ftErrors.pagenotfound);
        }
    }

    /**
     * This method allows developers to override the automatically-selected
     * CallTemplate call style. By default, no override is done and the
     * CallTemplate class determines the best call style.
     * 
     * @param ct CallTemplate tag
     * @param targetPagename target pagename
     * @see CallTemplate #setStyle(CallTemplate.Style)
     */
    protected void imposeCallTemplateStyle(final CallTemplate ct, final String targetPagename) {
    }

    protected String massagePackedArgs(String packedargs) {
        return packedargs;
    }

    /**
     * Record compositional dependencies that are required for the controller
     */
    protected void recordCompositionalDependencies() {
        RenderUtils.recordBaseCompositionalDependencies(ics);
    }

    protected void renderPage() {

        String pa = ics.GetVar(PubConstants.PACKEDARGS);
        unpackPackedArgs();
        final AssetIdWithSite id = resolveAssetId();
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: '" + id + "' for url " + ics.pageURL(),
                    ftErrors.pagenotfound);
        }
        LOG.debug("RenderPage found a valid asset and site: " + id);

        if (ics.GetVar(PubConstants.CHILDPAGENAME) != null) {
            // preview UI support
            callPage(id, ics.GetVar(PubConstants.CHILDPAGENAME), pa);
        } else {

            VanityAsset wra = getWraAndResolveAlias(id);

            callTemplate(new AssetIdWithSite(wra.getId().getType(), wra.getId().getId(), id.getSite()),
                    wra.getTemplate());

        }
    }

    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id;
        if (goodString(ics.GetVar("virtual-webroot")) && goodString(ics.GetVar("url-path"))) {
            id = pathTranslationService.resolveAsset(ics.GetVar("virtual-webroot"), ics.GetVar("url-path"));
        } else if (goodString(ics.GetVar("c")) && goodString(ics.GetVar("cid"))) {
            // handle these to be nice
            // Look up site because we can't trust the wrapper's resarg.
            String site = wraCoreFieldDao.resolveSite(ics.GetVar("c"), ics.GetVar("cid"));

            if (site == null)
                throw new CSRuntimeException("No site found for asset (" + ics.GetVar("c") + ":" + ics.GetVar("cid")
                        + " ).", ftErrors.pagenotfound);

            id = new AssetIdWithSite(ics.GetVar("c"), Long.parseLong(ics.GetVar("cid")), site);
        } else if (goodString(ics.GetVar("virtual-webroot")) || goodString(ics.GetVar("url-path"))) {
            // (but not both)
            throw new CSRuntimeException("Missing required param virtual-webroot & url-path.", ftErrors.pagenotfound);
        } else {
            throw new CSRuntimeException("Missing required param c, cid.", ftErrors.pagenotfound);
        }
        return id;
    }

    protected void unpackPackedArgs() {
        // for RenderPage unpacking and throwing away packedargs seems the
        // correct thing to do
        final String packedargs = ics.GetVar(PACKEDARGS);
        if (StringUtils.isNotBlank(packedargs)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("packedargs is " + packedargs);
            }
            Map<String, String> map = new HashMap<String, String>();
            ics.decode(packedargs, map);
            for (Map.Entry<String, String> e : map.entrySet()) {
                ics.SetVar(e.getKey(), e.getValue());

            }
            ics.RemoveVar(PACKEDARGS);
        }

    }

}
