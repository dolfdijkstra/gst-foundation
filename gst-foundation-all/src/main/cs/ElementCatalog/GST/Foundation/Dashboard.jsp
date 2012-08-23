<%--

    Copyright 2010 FatWire Corporation. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld" %>
<%@ taglib prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf" %>
<cs:ftcs><gsf:root><!DOCTYPE html>
    <html>
    <head>
        <title>GST Site Foundation Dashbaord</title>
        <meta name="title" content="GST Site Foundation Dashbaord"/>
        <meta name="description" content="GST Site Foundation dashboard and control panel, showing the status of the application."/>
        <meta http-equiv="Pragma" content="No-cache">
		<link href="/cs/Xcelerate/data/css/en_US/common.css" rel="styleSheet" type="text/css">
		<link href="/cs/Xcelerate/data/css/en_US/content.css" rel="styleSheet" type="text/css">
		<link href="/cs/Xcelerate/data/css/en_US/wemAdvancedUI.css" rel="stylesheet" type="text/css">
		<link href="/cs/Xcelerate/data/css/en_US/toolbar.css" rel="styleSheet" type="text/css">
		<style type="text/css">
		.logoGSF {
        	margin-top: 3px;
            font-size: 18px;
            color: #EFEFEF;
        }
        .menuItem a {
            color: #EFEFEF;
        }
        body a:visited {
        	color: #006699;
        }
		</style>
    </head>
    <body>
	<div class="topMenuBar">
		<div class="topMenuBarLeft">
			<div class="logoGSF">GST Site Foundation</div>
			<div class="topMenuBarMenu">
				<div class="menuItem">
					<satellite:link pagename="GST/Foundation/Dashboard" satellite="false"/>
					<a href="${cs.referURL}">Dashboard</a>
				</div>
				<div class="menuItem">
					<a href="http://www.gst-foundation.org/">GSF Website</a>
				</div>
			</div>
		</div>
		<div class="topMenuBarRight">
		</div>
	</div>
    <div class="title-value-text">
        <h1>GST Site Foundation Dashboard</h1>
    </div>
    <%-- ----------- process forms first --------------- --%>
    <ics:if condition='${cs.cmd == "install"}'> <ics:then>
        <h2>Installing GSF Components...</h2>
        <gsf:install output="gsfInstallStatus"/>
        <p>Complete. Result: ${cs.gsfInstallStatus} (0 means success).</p>
    </ics:then> </ics:if>

    <%-- --------------- Display dashboard -------------- --%>
    <gsf:install-status output="gsfInstallStatus"/>
    <ics:if condition="${cs.gsfInstallStatus != 0}"> <ics:then>
        <table border="1" cellspacing="0" cellpadding="0">
            <tr>
                <td class="message-bg-color">
                    <table border="0" cellspacing="0" cellpadding="3">
                        <tr>
                            <td valign="top">
                                <img src="/cs/Xcelerate/graphics/common/msg/error.gif" width="25" height="22">
                            </td>
                            <td>
                                <H3> GSF Installation Incomplete (status=${cs.gsfInstallStatus})</H3>

                                <p>The GST Site Foundation was not completely installed. The error code defines the
                                    components that are not yet installed. Unfortunately, at the moment there is no
                                    utility to decode the error code but if you really must know, have a look in the
                                    source code for the InstallStatus class and decipher the bitmask yourself. Don't
                                    worry, eventually we'll sort this out. For now, please just try to figure out what
                                    is missing on your own. Have a nice day.</p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <p>&nbsp;</p>
    </ics:then> <ics:else>
        <h3>GSF is properly installed and is healthy.</h3>
    </ics:else></ics:if>

    <ics:if condition='<%=System.getProperty("com.fatwire.gst.foundation.env-name") == null%>'><ics:then>
        <table border="1" cellspacing="0" cellpadding="0">
            <tr>
                <td class="message-bg-color">
                    <table border="0" cellspacing="0" cellpadding="3">
                        <tr>
                            <td valign="top">
                                <img src="/cs/Xcelerate/graphics/common/msg/warning.gif" width="25" height="22">
                            </td>
                            <td>
                                <H3>Pretty URL Environment Not Identified</H3>

                                <p>The pretty URL environment property name has not been set. In order for URL rewriting
                                    to occur, the system property identifying this particular environment must be set.
                                    This is usually done in the <code>catalina.sh</code> script for Tomcat (other places
                                    for other application servers). Typically, adding
                                    <code>-Dcom.fatwire.gst.foundation.env-name=YOURNAME</code> where
                                    <code>YOURNAME</code> is the environment name is sufficient. It should of course
                                    match the appropriate entry in the Virtual Webroot assets.  Consult the
                                    GSF documentation, wiki, or mailing list for more information.
                                    </p>
                                <p>The GSF sites will continue to render without this set, but they pages will not have
                                    pretty URLs.
                                </p>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </ics:then> </ics:if>

    <h2>GSF Control Panel</h2>
    <ul>
        <li>
            <satellite:link pagename="GST/Foundation/Dashboard" satellite="false"/><a href="${cs.referURL}">Refresh GSF Dashboard</a>
        </li>
        <li>
            <satellite:link pagename="GST/Foundation/Dashboard" satellite="false">
                <satellite:parameter name="cmd" value="install"/> </satellite:link>
            <a href="${cs.referURL}">Install/reinstall GSF components</a>
        </li>
        <li>
            <satellite:form satellite="false">
                <input type="hidden" name="pagename" value="GST/Foundation/Dashboard"/> Add WRA attributes to flex family
                <select name="families">
                    <option>coming soon...</option>
                </select> <input type="submit" name="cmd" value="install"/> </satellite:form>
        </li>
        <li>
            <satellite:link pagename="GST/Foundation/RebuildUrlRegistry" satellite="false"/>
            <a href="${cs.referURL}">Rebuild URL Registry</a> (requires loading/saving all WRA assets and can be long)
        </li>
        <li>
            <satellite:link pagename="GST/Foundation/RewriteGenerator" satellite="false"/>
            <a href="${cs.referURL}">Generate mod_rewrite rules</a> for Virtual Webroots
        </li>
    </ul>
    </body> </html> </gsf:root> </cs:ftcs>