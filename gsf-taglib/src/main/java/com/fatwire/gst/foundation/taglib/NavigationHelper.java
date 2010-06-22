package com.fatwire.gst.foundation.taglib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.asset.Children;
import com.fatwire.gst.foundation.facade.runtag.render.GetTemplateUrl;
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to retrieve the Navigation Bar data. See the description of
 * getSitePlanAsMap(String pageid) for more details.
 * <p/>
 *
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public final class NavigationHelper {
    private final ICS ics;
    private final Log LOG = LogFactory.getLog(NavigationHelper.class);

    /**
     * Constructor
     *
     * @param ics object
     */
    public NavigationHelper(ICS ics) {
        this.ics = ics;
    }

    /**
     * Name of the page subtype indicating that this page is NOT rendered on the
     * site but is instead merely used to group navigation components on the
     * site.
     */
    public static final String NAVBAR_NAME = "GSTNavName";

    /**
     * Get a Map<String,Object> object of the site plan tree containing all the
     * attributes necessary to create a nav bar. The Map contains the following
     * keys: - url: String containing the href for the page (as defined in the
     * GST Site Foundation design doc) - linktext: String containing the text to
     * be used to represent the link. Uses the "linktext" attribute of the
     * page's asset, or if that's empty use the "h1text" attribute - pageid: id
     * of the page - subtype: subtype of the page - level: the number of levels
     * down the site plan tree of the page asset (starting with the pageid you
     * originally pass in = level 0) - children (a List<Map<String,Object>> of
     * the children (in the site plan tree) of the page, where each Map contains
     * the above attributes
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p/>
     * Aliases are resolved.
     *
     * @param pageid AssetId of (usually a page) in the site plan tree to start
     *               with. Typically this would be a nav name. The nav name would
     *               be included in the output object. Recursion is automatic
     * @return Map<String,Object> of the site plan tree (see above)
     */
    public Map<String, Object> getSitePlanAsMap(String pageid) {
        return getSitePlanAsMap(pageid, 0);
    }

    /**
     * Called from public Map<String,Object> getSitePlanAsMap(String pageid).
     * See that function's description for details
     *
     * @param pageid id of the page assest
     * @param level  starting level number when traversing the site plan tree
     * @return Map<String,Object> of the site plan tree
     * TODO: handle missing unnamed association gracefully (rather than throwing an exception)
     */
    private Map<String, Object> getSitePlanAsMap(String pageid, int level) {
        AssetData pageData = AssetDataUtils.getAssetData("Page", pageid, "subtype");
        String subtype = pageData.getAttributeData("subtype").getData().toString();
        final boolean isNavigationPlaceholder = NAVBAR_NAME.equals(subtype);
        String linkText = null;
        String url = null;
        // get the link text from the "linktext" attribute of the asset in the
        // page's unnamed association
        if (!isNavigationPlaceholder) {
            AssetId pageAssetId = Children.getSingleAssociation(ics, "Page", pageid, "-");
            AssetData pageAssetData = AssetDataUtils.getAssetData(pageAssetId, "linktext", "h1title", "template");
            linkText = AttributeDataUtils.getWithFallback(pageAssetData, "linktext", "h1title");
            String tname = pageAssetData.getAttributeData("template").getData().toString();
            String wrapper = ics.GetProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher", "ServletRequest.properties", true);
            if (!Utilities.goodString(wrapper)) {
                wrapper = "GSF/Dispatcher";
            }
            url = _getURL(pageAssetId.getType(), Long.toString(pageAssetId.getId()), tname, wrapper, "nav");
        }

        // get the children in the Site Plan
        List<AssetId> childrenIDs = ListPages.getChildPages(ics, Long.parseLong(pageid));
        List<Map<String, Object>> navChildren = new ArrayList<Map<String, Object>>();
        for (AssetId aid : childrenIDs) {
            String childPageID = Long.toString(aid.getId());
            navChildren.add(getSitePlanAsMap(childPageID, level + 1));
        }
        return createPageLink(url, linkText, pageid, navChildren, subtype, level);
    }

    /**
     * runs the GetTemplateUrl and with the given attributes and returns the
     * resulting url as a String
     *
     * @param c        asset type
     * @param cid      asset id
     * @param tname    template name
     * @param wrapper  wrapper name
     * @param slotname slot name
     * @return String containing the resulting href
     */
    private String _getURL(String c, String cid, String tname, String wrapper, String slotname) {
        GetTemplateUrl gtu = new GetTemplateUrl(ics, c, cid, tname, wrapper, slotname);
        ics.RemoveVar("gspal-url");
        gtu.setOutstr("gspal-url");
        gtu.execute(ics);
        String URL = ics.GetVar("gspal-url");
        ics.RemoveVar("gspal-url");
        return URL;
    }

    /**
     * Get a PageLinkData object from the site plan tree. If the linktext is
     * derived from an associated asset, specify the association name. If the
     * linktet is derived from the page asset itself, leave detailAssocName as
     * null. Either way, the linktext comes from the appropriate asset's
     * linktextAttr.
     * <p/>
     * Links are not populated for Navigation Placeholders, but it is often very
     * convenient to pass a navigation placeholder into this function in order
     * to return all children under a specific placeholder.
     * <p/>
     * Aliases are resolved.
     *
     * @param id                AssetId of (usually a page) in teh site plan tree to start
     *                          with. Typically this would be a nav name. The nav name would
     *                          be included in the output object. Recursion is automatic
     * @param dimensionSetId    DimensionSet id
     * @param preferredLocaleid ID of locale asset to use to calculate the links
     *                          for the navigation. Null is allowed but only if locale is not
     *                          used (obviously)
     * @return PageLinkData
     */
    public Map<String, Object> getSitePlanAsMap(String id, String dimensionSetId, String preferredLocaleid) {
        return getSitePlanAsMap(id); // TODO: use dimensionSetId and
        // preferredLocaleid instead of calling
        // this
    }

    /**
     * Puts the page attributes into a map
     *
     * @param URL      href
     * @param linkText link text
     * @param pageId   page id
     * @param children list of children pages in site plan
     * @param subtype  page subtype
     * @param level    level in the site plan tree
     * @return Map containing these attributes
     */
    private Map<String, Object> createPageLink(String URL, String linkText, String pageId, List<Map<String, Object>> children, String subtype, int level) {
        Map<String, Object> pageLink = new HashMap<String, Object>();

        pageLink.put("url", URL);
        pageLink.put("linktext", linkText);
        pageLink.put("pageid", pageId);
        pageLink.put("subtype", subtype);
        pageLink.put("level", Integer.toString(level));
        pageLink.put("children", children);

        return pageLink;
    }

    static final PreparedStmt FIND_P = new PreparedStmt("SELECT art.oid\n\tFROM AssetRelationTree art, AssetPublication ap, Publication p\n\tWHERE ap.assetid = art.oid\n\t" + "AND ap.assettype = 'Page'\n\t" + "AND ap.pubid = p.id\n\t" + "AND p.name = ?\n\t" + "AND art.otype = ap.assettype\n\t" + "AND art.nid in (\n\t\t" + "SELECT nparentid FROM AssetRelationtree WHERE otype=? AND oid=? AND ncode='-'\n\t) ORDER BY ap.id", Arrays.asList("AssetRelationTree", "AssetPublication", "Publication"));

    static {
        FIND_P.setElement(0, "Publication", "name");
        FIND_P.setElement(1, "AssetRelationTree", "otype");
        FIND_P.setElement(2, "AssetRelationTree", "oid");
    }

    /**
     * Locate the page that contains the specified Web-Referenceable Asset.
     * <p/>
     * A WRA is supposed to just be placed on one page (in the unnamed association
     * block), and this method locates it.  If it is not found, 0L is returned.
     * <p/>
     * If multiple matches are found, a warning is logged and the first one is
     * returned.
     *
     * @param site_name  name of the site to search within
     * @param wraAssetId the asset id of the web-referenceable asset
     * @return page asset ID or 0L.
     */
    public long findP(String site_name, AssetId wraAssetId) {
        final StatementParam param = FIND_P.newParam();
        param.setString(0, site_name);
        param.setString(1, wraAssetId.getType());
        param.setLong(2, wraAssetId.getId());
        long result = 0L;
        for (final Row r : SqlHelper.select(ics, FIND_P, param)) {
            if (result != 0L)
                LOG.warn("Asset " + wraAssetId + " was found as the primary content on multiple pages in the site.  Web-referenceable assets should only be the primary content on one Page to comply with SEO rules.  Automatically selecting the oldest page");
            else result = r.getLong("oid");
        }
        if (result == 0L) {
            // could not locate
        }
        return result;

    }
}
