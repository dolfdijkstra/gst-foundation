/*
 * Copyright 2010 Metastratus Web Solutions Limited. All Rights Reserved.
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
package com.fatwire.gst.foundation.taglib.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.common.SiteAccessException;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.site.User;
import com.fatwire.assetapi.site.UserManager;
import com.fatwire.gst.foundation.facade.runtag.asset.AssetList;
import com.fatwire.gst.foundation.facade.runtag.publication.PublicationCreate;
import com.fatwire.gst.foundation.facade.runtag.publication.PublicationGather;
import com.fatwire.gst.foundation.facade.runtag.publication.PublicationGet;
import com.fatwire.gst.foundation.facade.runtag.publication.PublicationLoad;
import com.fatwire.gst.foundation.facade.runtag.publication.PublicationSave;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import com.fatwire.gst.foundation.tagging.CacheMgrTaggedAssetEventListener;
import com.fatwire.gst.foundation.tagging.TaggedAssetEventListener;
import com.fatwire.gst.foundation.tagging.db.TableTaggingServiceImpl;
import com.fatwire.gst.foundation.url.WraAssetEventListener;
import com.fatwire.gst.foundation.url.db.UrlRegistryDaoImpl;
import com.fatwire.system.SessionFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * General installer class to be used by JSP tag library.  Also exteranlly accessible.
 *
 * @author Tony Field
 * @since 2012-03-26
 */
public final class InstallerEngine {
    protected static final Log LOG = LogFactory.getLog(InstallStatus.class.getPackage().getName());

    public static final int STATUS_OK = 0x0;
    public static final int STATUS_NO_PUBLICATION = 0x1;
    public static final int STATUS_NO_TAG_REGISTRY = 0x2;
    public static final int STATUS_NO_URL_REGISTRY = 0x4;
    public static final int STATUS_NO_FLEX_FAMILY = 0x8;
    public static final int STATUS_NO_FLEX_ATTRIBUTES = 0x10;
    public static final int STATUS_NO_ATTRIBUTES_IN_FAMILY = 0x20;
    public static final int STATUS_NO_DEFINITIONS = 0x40;
    public static final int STATUS_NO_ASSEMBLER = 0x80;
    public static final int STATUS_NO_PAGEREF = 0x100;
    public static final int STATUS_NO_USER_IN_SITE = 0x200;
    public static final int STATUS_NO_TREETABS_IN_SITE = 0x400;
    public static final int STATUS_CSE_SE_DISABLED_IN_SITE = 0x800;
    public static final int STATUS_GST_DISPATCHER_MISSING = 0x1000;
    public static final String[] GST_SITE_TREETABS = {"Active List", "Admin", "Bookmarks", "Site Plan"};

    private final ICS ics;
    private final List<String> targetFlexFamilies;

    public InstallerEngine(ICS ics, List<String> targetFlexFamilies) {
        this.ics = ics;
        if (targetFlexFamilies == null || targetFlexFamilies.size() == 0) {
            this.targetFlexFamilies = Arrays.asList("GSTAttribute");
        } else {
            this.targetFlexFamilies = targetFlexFamilies;
        }
    }


    public int getInstallStatus() {
        savePubid();
        int status = STATUS_OK;
        status += _checkPublication();
        status += _checkTagRegistry();
        status += _checkUrlRegistry();
        status += _checkFlexFamily();
        status += _checkFlexAttributes();
        status += _checkAttributesInFamily();
        status += _checkDefinitions();
        status += _checkAssembler();
        status += _checkPageRef();
        status += _checkUserInSite();
        status += _checkTreetabsInSite();
        status += _checkCSElementSiteEntryInSite();
        status += _checkGSTDispatcher();
        restorePubid();
        return status;
    }

    private int _checkPublication() {
        //        <PUBLICATION.LOAD NAME="pubtest" FIELD="name" VALUE="GST"/>
        PublicationLoad load = new PublicationLoad();
        load.setName("pubtest");
        load.setField("name");
        load.setValue("GST");
        load.execute(ics);
        return ics.GetErrno() == -101 ? STATUS_NO_PUBLICATION : STATUS_OK;
    }

    private int _checkTagRegistry() {
        if (!new TableTaggingServiceImpl(ics).isInstalled()) return STATUS_NO_TAG_REGISTRY;
        if (!new TaggedAssetEventListener().isInstalled(ics)) return STATUS_NO_TAG_REGISTRY;
        if (!new CacheMgrTaggedAssetEventListener().isInstalled(ics)) return STATUS_NO_TAG_REGISTRY;
        return STATUS_OK;
    }

    private int _checkUrlRegistry() {
        if (!new UrlRegistryDaoImpl(ics).isInstalled()) return STATUS_NO_URL_REGISTRY;
        if (!new WraAssetEventListener().isInstalled(ics)) return STATUS_NO_URL_REGISTRY;
        return STATUS_OK;
    }

    private int _checkFlexFamily() {
        for (String tbl : Arrays.asList("GSTFilter", "GSTAttribute", "GSTPDefinition", "GSTDefinition", "GSTParent", "GSTVirtualWebroot", "GSTAlias", "GSTProperty")) {
            if (SqlHelper.tableExists(ics, tbl) == false) {
                return STATUS_NO_FLEX_FAMILY;
            }
        }
        return STATUS_OK;
    }

    private int _checkFlexAttributes() {
        for (String name : Arrays.asList("env_name", "env_vwebroot", "master_vwebroot", "popup", "target_url", "value")) {
            if (!AssetList.assetExistsByName(ics, "GSTAttribute", name)) {
                return STATUS_NO_FLEX_ATTRIBUTES;
            }
        }
        return STATUS_OK;
    }

    private int _checkAttributesInFamily() {
        for (String family : targetFlexFamilies) {
            for (String attr : Arrays.asList("linktext", "linkimage", "gsttag", "h1title", "metadescription", "metakeyword", "metatitle")) {
                if (!AssetList.assetExistsByName(ics, family, attr)) {
                    return STATUS_NO_ATTRIBUTES_IN_FAMILY;
                }
            }
        }
        return STATUS_OK;
    }

    private int _checkDefinitions() {
        for (String def : Arrays.asList("GSTVirtualWebroot", "GSTAlias", "GSTProperty")) {
            if (!AssetList.assetExistsByName(ics, "GSTDefinition", def)) {
                return STATUS_NO_DEFINITIONS;
            }
        }
        return STATUS_OK;
    }

    private int _checkAssembler() {
        int result = STATUS_NO_ASSEMBLER;
        try {
            String srProps = ics.getIServlet().getServlet().getServletContext().getRealPath("/WEB-INF/classes/ServletRequest.properties");
            InputStream in = new FileInputStream(srProps);
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            int i = 1;
            for (; ; ) {

                String classname = prop.getProperty("uri.assembler." + i + ".classname");
                //out.write(classname +"<br/>");
                if (classname == null || classname.trim().length() == 0) {
                    i--;
                    break;
                }

                if ("com.fatwire.gst.foundation.url.WraPathAssembler".equals(classname)) {
                    result = STATUS_OK;
                }

                i++;
            }
        } catch (IOException e) {
            LOG.error("Failure checking installer status: " + e, e);
            result = STATUS_NO_ASSEMBLER;

        }
        return result;
    }

    private int _checkPageRef() {
        int status = STATUS_NO_PAGEREF;
        try {
            String inipath = ics.getIServlet().getServlet().getServletContext().getInitParameter("inipath");

            String srProps = Utilities.osSafeSpec(inipath + "/futuretense_xcel.ini");
            InputStream in = new FileInputStream(srProps);
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            String pageref = prop.getProperty("xcelerate.pageref");
            if ("com.fatwire.gst.foundation.url.WraPageReference".equals(pageref)) {
                status = STATUS_OK;
            }
        } catch (IOException e) {
            LOG.error("Failure installing PageRef: " + e, e);
        }
        return status;
    }

    private int _checkUserInSite() {
        UserManager um = (UserManager) SessionFactory.getSession(ics).getManager(UserManager.class.getName());
        try {
            for (User u : um.read(Arrays.asList(ics.GetSSVar("username")))) {
                List<String> roles = u.getRoles("GST");
                if (roles == null) return STATUS_NO_USER_IN_SITE;
                if (roles.containsAll(Arrays.asList("GeneralAdmin", "AdvancedUser"))) {
                    return STATUS_OK;
                } else {
                    return STATUS_NO_USER_IN_SITE;
                }
            }
            return STATUS_NO_USER_IN_SITE;
        } catch (SiteAccessException e) {
            LOG.error("Exception accessing user data for site: " + e, e);
            return STATUS_NO_USER_IN_SITE;
        }
    }

    /**
     * Check if TreeTabs have GST site enabled or not. If tab is missing, that's ok (i.e. for Bookmarks or Active List tabs).
     * If all TreeTabs have the GST site enabled, then return STATUS_OK. Otherwise return STATUS_NO_TREETABS_IN_SITE.
     * @return
     */
    private int _checkTreetabsInSite() {
        LOG.debug("Checking for GST Site TreeTabs");
        FTValList list;
        for(String t : GST_SITE_TREETABS) {
            LOG.debug("Checking if tab '" + t + "' is enabled for GST site.");
//            <TTM.LOAD OBJVARNAME="tab" NAME="GSTSiteTabs.ITEM" />
//            <TREETAB.GETID NAME="tab" VARNAME="tabid"/>
//            <TREETAB.GETSITES NAME="tab" OBJVARNAME="tsites"/>
//            <SITELIST.HASSITE NAME="tsites" PUBID="1328078963506" VARNAME="hasSite"/>
            list = new FTValList();
            list.setValString("OBJVARNAME", "tab");
            list.setValString("NAME", t);
            ics.runTag("TTM.LOAD", list);

            if("0".equals(ics.GetVar("errno"))) {
                // Do we need to check for tab id here (to confirm it exists)?
                ics.SetVar("tabid", "");
                list = new FTValList();
                list.setValString("NAME", "tab");
                list.setValString("VARNAME", "tabid");
                ics.runTag("TREETAB.GETID", list);
                if(Utilities.goodString(ics.GetVar("tabid"))) {
                    LOG.debug("Tab '" + t + "' exists. Checking for GST site.");
                    list = new FTValList();
                    list.setValString("NAME", "tab");
                    list.setValString("OBJVARNAME", "tsites");
                    ics.runTag("TREETAB.GETSITES", list);
                    list = new FTValList();
                    list.setValString("NAME", "tsites");
                    list.setValString("PUBID", ics.GetSSVar("pubid"));
                    list.setValString("VARNAME", "hasSite");
                    ics.runTag("SITELIST.HASSITE", list);
                    if("true".equals(ics.GetVar("hasSite"))) {
                        LOG.debug("TreeTab '" + t + "' has GST site enabled.");
                    }
                    else {
                        LOG.debug("TreeTab '" + t + "' exists but does NOT have GST site enabled.");
                        return STATUS_NO_TREETABS_IN_SITE;
                    }
                }
            }
            else {
                LOG.debug("Unable to load TreeTab '" + t + "', skipping tab (this is OK). Errno=" + ics.GetVar("errno"));
            }
        }
        LOG.debug("All TreeTabs checked have GST site enabled. Returning STATUS_OK.");
        return STATUS_OK;
    }

    /**
     * If CSElement and SiteEntry asset types are disabled in GST site, or if the GST/Dispatcher CSElement or SiteEntry
     * is not shared with GST Site, or if their Start Menus don't exist for the GST site,
     * then return status STATUS_CSE_SE_DISABLED_IN_SITE.
     * @return
     */
    private int _checkCSElementSiteEntryInSite() {
        LOG.debug("Starting checks for CSElement/SiteEntry.");
        String[] assetTypes = {"CSElement", "SiteEntry"};
        String[][] startMenuTypes = {{"ContentForm","New "}, {"Search","Find "}};
        FTValList list;

        // check Start Menus
        for(String at : assetTypes) {
            for(String[] sm : startMenuTypes) {
                LOG.debug("Doing Start Menu check for AssetType=" + at + ", StartMenu type=" + sm[0]);
//            <STARTMENU.LOAD NAME="Find AssetTypes.ITEM, GST" ITEMTYPE="Search" OBJVARNAME="miTest"/>
//            <STARTMENUITEM.GETID NAME="miTest" VARNAME="miExists"/>
                list = new FTValList();
                list.setValString("NAME", (sm[1] + at + ", GST"));
                list.setValString("ITEMTYPE", sm[0]);
                list.setValString("OBJVARNAME", "smTest");
                ics.runTag("STARTMENU.LOAD", list);
                LOG.debug("Start Menu loaded. Errno=" + ics.GetVar("errno"));
                list = new FTValList();
                list.setValString("NAME", "smTest");
                list.setValString("VARNAME", "smId");
                ics.runTag("STARTMENUITEM.GETID", list);
                if(Utilities.goodString(ics.GetVar("smId"))) {
                    LOG.debug("StartMenu found for AssetType=" + at + ", StartMenu type=" + sm[0] + ", id=" + ics.GetVar("smId"));
//                    <STARTMENUITEM.GETSITES NAME="smTest" OBJVARNAME="smSiteList" />
//                    <SITELIST.HASSITE NAME="smSiteList" PUBID="1328078963506" VARNAME="hasSite"/>
                    list = new FTValList();
                    list.setValString("NAME", "smTest");
                    list.setValString("OBJVARNAME", "smSiteList");
                    ics.runTag("STARTMENUITEM.GETLEGALSITES", list);
                    boolean hasSite = siteListHasSite("smSiteList", ics.GetSSVar("pubid"));
                    if(hasSite) {
                        LOG.debug("StartMenu '" + (sm[1] + at + ", GST") + "' has GST site enabled.");
                    }
                    else {
                        LOG.debug("StartMenu '" + (sm[1] + at + ", GST") + "' exists but does not have GST site enabled.");
                        return STATUS_CSE_SE_DISABLED_IN_SITE;
                    }
                }
                else {
                    LOG.debug("StartMenu not found for AssetType=" + at + ", StartMenu type=" + sm[0]);
                    return STATUS_CSE_SE_DISABLED_IN_SITE;
                }
            }
        }

        // check if CSElement, SiteEntry Asset Types are enabled
        LOG.debug("Done checking StartMenus. Checking if AssetTypes are enabled in GST site.");
        for(String at : assetTypes) {
            FTValList args = new FTValList();
            args.setValString("upcommand", "IsAssetTypeEnabled");
            args.setValString("assettype", at);
            args.setValString("pubid", ics.GetSSVar("pubid"));
            ics.CallElement("OpenMarket/Xcelerate/Actions/AssetMgt/EnableAssetTypePub", args);
            if("true".equals(ics.GetVar("IsAuthorized"))) {
                LOG.debug("GST Site is enabled for AssetType=" + at);
            }
            else {
                LOG.debug("GST Site is not enabled for AssetType=" + at + ", IsAuthorized=" + ics.GetVar("IsAuthorized"));
                return STATUS_CSE_SE_DISABLED_IN_SITE;
            }
        }

        LOG.debug("Finished checks for CSElement/SiteEntry. Returning STATUS_OK.");
        return STATUS_OK;
    }

    /**
     * Check if a CSElement and SiteEntry named "GST/Dispatcher" exist and are shared with the GST site.
     * @return
     */
    private int _checkGSTDispatcher() {
        String[] assetTypes = {"CSElement", "SiteEntry"};
        for(String type : assetTypes) {
            LOG.debug("Checking if " + type + " 'GST/Dispatcher' exists.");

            AssetId aid = AssetList.lookupAssetId(ics, type, "GST/Dispatcher");
            if (aid == null) {
                LOG.debug("Could not find " + type + " 'GST/Dispatcher'.");
                return STATUS_GST_DISPATCHER_MISSING;
            }

            LOG.debug("Found " + type + " 'GST/Dispatcher' with id=" + aid.getId() + ". Checking if it's shared with GST site (pubid " + ics.GetSSVar("pubid") + ")...");
            FTValList args = new FTValList();
            args.put("TYPE", type);
            args.put("OBJECTID", Long.toString(aid.getId()));
            args.put("LIST", "cseSiteList");
            args.put("PUBID", ics.GetSSVar("pubid"));
            ics.runTag("ASSET.SITES", args);
            if(Utilities.goodString(ics.GetVar("errno"))) {
                try {
                    int errno = Integer.parseInt(ics.GetVar("errno"));
                    if(errno >= 0) {
                        LOG.debug(type + " GST/Dispatcher is enabled in the GST site.");
                    }
                    else {
                        LOG.debug(type + " GST/Dispatcher is NOT enabled in the GST site. Errno=" + ics.GetVar("errno"));
                        return STATUS_GST_DISPATCHER_MISSING;
                    }
                } catch (NumberFormatException e) {
                    LOG.debug("Error parsing errno in GST/Dispatcher ASSET.SITES check.");
                    return STATUS_GST_DISPATCHER_MISSING;
                }
            }
            else {
                LOG.debug(type + " GST/Dispatcher is NOT enabled in the GST site.");
                return STATUS_GST_DISPATCHER_MISSING;
            }
        }

        return STATUS_OK;
    }

    /*
     *
     *
     *
     *
     *
     *
     * Begin actual installation elements here
     *
     *
     *
     *
     *
     * 
     */

    /**
     * Perform the install.  Note this will only install components
     * that are not already properly installed, and this determination is made
     * based on the install status.
     *
     * @param status current install status code
     */
    protected void doInstall(int status) {
        savePubid();
        LOG.info("Installing missing GSF components");
        if (isInstallNeeded(STATUS_NO_PUBLICATION, status)) installPublication();
        if (isInstallNeeded(STATUS_NO_TAG_REGISTRY, status)) installTagRegistry();
        if (isInstallNeeded(STATUS_NO_URL_REGISTRY, status)) installUrlRegistry();
        if (isInstallNeeded(STATUS_NO_FLEX_FAMILY, status)) installFlexFamily();
        if (isInstallNeeded(STATUS_NO_FLEX_ATTRIBUTES, status)) installFlexAttributes();
        if (isInstallNeeded(STATUS_NO_ATTRIBUTES_IN_FAMILY, status)) installAttributesIntoFamily();
        if (isInstallNeeded(STATUS_NO_DEFINITIONS, status)) installDefinitions();
        if (isInstallNeeded(STATUS_NO_ASSEMBLER, status)) installAssembler();
        if (isInstallNeeded(STATUS_NO_PAGEREF, status)) installPageRef();
        if (isInstallNeeded(STATUS_NO_USER_IN_SITE, status)) installUserIntoSite();
        if (isInstallNeeded(STATUS_NO_TREETABS_IN_SITE, status)) installTreetabsIntoSite();
        if (isInstallNeeded(STATUS_CSE_SE_DISABLED_IN_SITE, status)) installCSElementSiteEntryInSite();
        if (isInstallNeeded(STATUS_GST_DISPATCHER_MISSING, status)) installGSTDispatcher();
        LOG.info("GSF missing component install complete.");
        restorePubid();
    }

    final boolean isInstallNeeded(int componentStatus, int systemStatus) {
        return (systemStatus & componentStatus) == componentStatus;
    }

    private void savePubid() {
        String s = ics.GetSSVar("pubid");
        if (StringUtils.isNotBlank(s)) {
            ics.SetVar("oldpubid", s);
            ics.RemoveSSVar("pubid");
        }

        PublicationLoad load = new PublicationLoad();
        load.setName("pubload");
        load.setField("name");
        load.setValue("GST");
        load.execute(ics);
        PublicationGet pGet = new PublicationGet();
        pGet.setName("pubload");
        pGet.setField("id");
        pGet.setOutput("pubid");
        pGet.execute(ics);
        ics.SetSSVar("pubid", ics.GetVar("pubid"));
    }

    private void restorePubid() {
        String s = ics.GetVar("oldpubid");
        if (StringUtils.isNotBlank(s)) {
            ics.RemoveVar("oldpubid");
            ics.SetSSVar("pubid", s);
        }
    }

    void installPublication() {
        LOG.info("Installing Publication...");
        //     <SETVAR NAME="Publication:name" VALUE="GST"/>
        //     <SETVAR NAME="Publication:description" VALUE="GST site"/>
        //     <SETVAR NAME="Publication:cs_preview" VALUE="Variables.empty"/>
        ics.SetVar("Publication:name", "GST");
        ics.SetVar("Publicatin:description", "GST Site");
        ics.SetVar("Publication:cs_preview", "");
        //     <SETVAR NAME="errno" VALUE="0"/>
        ics.ClearErrno();
        //     <PUBLICATION.CREATE NAME="publication"/>
        PublicationCreate pc = new PublicationCreate();
        pc.setName("publication");
        pc.execute(ics);
        //     <PUBLICATION.GATHER NAME="publication" PREFIX="Publication"/>
        PublicationGather pg = new PublicationGather();
        pg.setName("publication");
        pg.setPrefix("Publication");
        pg.execute(ics);
        //     <PUBLICATION.SAVE NAME="publication"/>
        PublicationSave ps = new PublicationSave();
        ps.setName("publication");
        ps.execute(ics);
        //     <PUBLICATION.GET NAME="publication" FIELD="id" OUTPUT="pubid"/>
        PublicationGet pGet = new PublicationGet();
        pGet.setName("publication");
        pGet.setField("id");
        pGet.setOutput("pubid");
        //     <h2>Created GST site with id: <CSVAR NAME="Variables.pubid"/></h2>
        LOG.info("...publication install complete.  Pubid: " + ics.GetVar("pubid"));
    }

    void installTagRegistry() {
        LOG.info("Installing tag registry...");
        new TableTaggingServiceImpl(ics).install();
        new TaggedAssetEventListener().install(ics);
        new CacheMgrTaggedAssetEventListener().install(ics);
        LOG.info("...tag registry install complete");
    }

    void installUrlRegistry() {
        LOG.info("Installing url registry...");
        new UrlRegistryDaoImpl(ics).install();
        new WraAssetEventListener().install(ics);
        LOG.info("...url registry install complete");
    }

    void installFlexFamily() {
        LOG.info("Installing flex family...");
        ics.CallElement("GST/Foundation/FlexFamilyInstaller", null);
        LOG.info("...flex family install complete");

    }

    void installFlexAttributes() {
        LOG.info("Installing flex attributes...");
        ics.CallElement("GST/Foundation/InstallAttributes", null);
        LOG.info("...flex attributes install complete");
    }

    /**
     * Install the flex attributes into any flex family that is desired.
     * By default, if no otherwise specified, they are installed into the
     * GSTAttribute flex family.
     */
    void installAttributesIntoFamily() {
        for (String family : targetFlexFamilies) {
            LOG.info("Installing flex attributes into family " + family + "...");
            FTValList vl = new FTValList();
            vl.setValString("AttributeType", family);
            ics.CallElement("GST/Foundation/InstallAttributesIntoFamily", vl);
            LOG.info("...flex attribute installation into family " + family + " complete.");
        }
    }

    void installDefinitions() {
        LOG.info("Installing flex definitions...");
        ics.CallElement("GST/Foundation/InstallDefinitions", null);
        LOG.info("...flex definition install complete");
    }

    void installAssembler() {
        LOG.info("Installing URL Assembler...");
        try {
            String srProps = ics.getIServlet().getServlet().getServletContext().getRealPath("/WEB-INF/classes/ServletRequest.properties");
            InputStream in = new FileInputStream(srProps);
            Properties prop = new Properties();
            prop.load(in);
            in.close();
            int i = 1;
            boolean hasGSFAssembler = false;
            for (; ; ) {

                String classname = prop.getProperty("uri.assembler." + i + ".classname");
                //out.write(classname +"<br/>");
                if (classname == null || classname.trim().length() == 0) {
                    i--;
                    break;
                }

                if ("com.fatwire.gst.foundation.url.WraPathAssembler".equals(classname)) {
                    hasGSFAssembler = true;
                }

                i++;
            }

            int max = i;
            //ics.StreamEvalBytes("max:"+i +"<br/>");
            if (!hasGSFAssembler) {
                //                ics.StreamEvalBytes("Registring the GSF assembler in ServletRequest.properties.<br/>");
                for (i = max + 1; i > 1; i--) {
                    String classname = prop.getProperty("uri.assembler." + (i - 1) + ".classname");
                    String shortform = prop.getProperty("uri.assembler." + (i - 1) + ".shortform");
                    //                    ics.StreamEvalBytes("" + classname + "<br/>");
                    //                    ics.StreamEvalBytes("" + shortform + "<br/>");
                    prop.setProperty("uri.assembler." + i + ".classname", classname);
                    prop.setProperty("uri.assembler." + i + ".shortform", shortform);
                }
                prop.setProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher", "GST/Dispatcher");
                prop.setProperty("uri.assembler.1.shortform", "wrapath");
                prop.setProperty("uri.assembler.1.classname", "com.fatwire.gst.foundation.url.WraPathAssembler");
                File orig = new File(srProps);
                File bk = new File(srProps + "." + System.currentTimeMillis() + ".bk");
                if (orig.exists()) {
                    orig.renameTo(bk);
                    StringWriter sw = new StringWriter();
                    FileOutputStream fout = new FileOutputStream(orig);
                    prop.store(fout, "Modified by GSF installer.");
                    fout.close();

                }

            }
        } catch (IOException e) {
            LOG.error("Failure installing URL Assembler: " + e, e);
        }
        LOG.info("...URL Assembler installation complete.");
    }

    void installPageRef() {
        LOG.info("Installing PageRef...");
        try {
            String inipath = ics.getIServlet().getServlet().getServletContext().getInitParameter("inipath");

            String srProps = Utilities.osSafeSpec(inipath + "/futuretense_xcel.ini");
            InputStream in = new FileInputStream(srProps);
            Properties prop = new Properties();
            prop.load(in);
            in.close();

            //            ics.StreamEvalBytes("Registring the GSF pageref in futuretense_xcel.ini.<br/>");
            prop.setProperty("xcelerate.pageref", "com.fatwire.gst.foundation.url.WraPageReference");
            File orig = new File(srProps);
            File bk = new File(srProps + "." + System.currentTimeMillis() + ".bk");
            if (orig.exists()) {
                orig.renameTo(bk);
                FileOutputStream fout = new FileOutputStream(orig);
                prop.store(fout, "Modified by GSF installer.");
                fout.close();
            }

        } catch (IOException e) {
            LOG.error("Failure installing PageRef: " + e, e);
        }
        LOG.info("...PageRef installation complete.");
    }

    void installUserIntoSite() {
        UserManager um = (UserManager) SessionFactory.getSession(ics).getManager(UserManager.class.getName());
        List<User> us = new ArrayList<User>();
        try {
            for (User u : um.read(Arrays.asList(ics.GetSSVar("username")))) {
                List<String> existingRoles = u.getRoles("GST");
                List<String> roles = Arrays.asList("AdvancedUser", "GeneralAdmin");
                roles.addAll(existingRoles);
                u.setRoles("GST", roles);
                us.add(u);
            }
            um.update(us);
        } catch (SiteAccessException e) {
            LOG.error("Error adding user to site: " + e, e);
        }
    }

    void installTreetabsIntoSite() {
        LOG.info("Enabling TreeTabs for GST site.");
        ics.CallElement("GST/Foundation/InstallTreeTabs", null);
        LOG.info("Done enabling TreeTabs for GST site.");
    }

    void installCSElementSiteEntryInSite() {
        LOG.info("Enabling TreeTabs for GST site.");
        ics.CallElement("GST/Foundation/InstallCSElementSiteEntry", null);
        LOG.info("Done enabling TreeTabs for GST site.");
    }

    void installGSTDispatcher() {
        LOG.info("Creating GST/Dispatcher CSElement and SiteEntry");
        ics.CallElement("GST/Foundation/InstallGSTDispatcher", null);
        LOG.info("Done creating GST/Dispatcher CSElement and SiteEntry.");
    }

    /**
     * Checks if a SiteList contains the specified pubid
     * @param siteListObjName name of ICS object holiding the SiteList
     * @param pubid pubid to check for
     * @return true if SiteList contains the pubid specified by 'pubid'
     */
    private boolean siteListHasSite(String siteListObjName, String pubid) {
        FTValList list = new FTValList();
        list.setValString("NAME", siteListObjName);
        list.setValString("PUBID", pubid);
        list.setValString("VARNAME", "tempHasSiteCheck");
        ics.runTag("SITELIST.HASSITE", list);
        boolean hasSite = "true".equals(ics.GetVar("tempHasSiteCheck"));
        ics.RemoveVar("tempHasSiteCheck");
        return hasSite;
    }

    // todo: check jstl core
}
