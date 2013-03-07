<%--

    Copyright 2010 Metastratus Web Solutions Limited. All Rights Reserved.

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
<%@ page import="java.net.URI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="com.fatwire.gst.foundation.url.WraPathAssembler" %>
<%@ page import="com.fatwire.gst.foundation.vwebroot.AssetApiVirtualWebrootDao" %>
<%@ page import="com.fatwire.gst.foundation.vwebroot.VirtualWebroot" %>
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld" %>
<%@ taglib prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf" %>
<%
    //
    // GST/Foundation/RewriteGenerator
    //
%>
<cs:ftcs><gsf:root>
    <html>
    <head>
        <title>GSF Rewrite Generator</title>
        <link href="<%=ics.GetProperty("ft.cgipath")%>Xcelerate/data/css/en_US/common.css" rel="styleSheet" type="text/css">
        <link href="<%=ics.GetProperty("ft.cgipath")%>Xcelerate/data/css/en_US/content.css" rel="styleSheet" type="text/css">
    </head>
    <body>
    <h1>GSF Rewrite Generator</h1>

    <h2>Rewrite Rules for Apache mod_rewrite</h2>

    <p>The following settings should be added to your apache configuration file (usually httpd.conf).
        The rules are grouped by &quot;environment&quot; (e.g.: &quot;dev&quot;,
        &quot;staging&quot;, &quot;prod&quot;). Usually this means that the rules will have to be applied
        to a different configuration file for each environment. Within each environment, the rules are further
        grouped by VirtualHost, and so should be added to the VirutalHost section of the configuration file
        corresponding to to the VirtualHost specified, and above the application server's url mapping section.
        The application server plugin (e.g.: mod_ajp or mod_jk)
        should be already be configured within the virtual host as well. If this is not the case, work with
        your web server administrator to determine the best configuration for your system.</p>
    <%

        // get the base path for Satellite Server
        String satelliteServerBasePath = ics.GetProperty("path.SatelliteServer", "ServletRequest.properties", true);
        String dispatcherPageName = ics.GetProperty(WraPathAssembler.DISPATCHER_PROPNAME, "ServletRequest.properties", true);
        if (dispatcherPageName == null || dispatcherPageName.length() == 0) dispatcherPageName = "GST/Dispatcher";

        // get all of the virtual webroots together so that they can be presented easily by environment instead of by name
        Map<String, SortedSet<VirtualWebroot>> webrootsByEnvironment = new HashMap<String, SortedSet<VirtualWebroot>>();
        for (VirtualWebroot vw : new AssetApiVirtualWebrootDao(ics).getAllVirtualWebroots()) {
            SortedSet<VirtualWebroot> envSet = webrootsByEnvironment.get(vw.getEnvironmentName());
            if (envSet == null) {
                envSet = new TreeSet<VirtualWebroot>(new AssetApiVirtualWebrootDao.UrlInfoComparator());
                webrootsByEnvironment.put(vw.getEnvironmentName(), envSet);
            }
            envSet.add(vw);

            SortedSet<VirtualWebroot> masterSet = webrootsByEnvironment.get("GST-MASTER");
            if (masterSet == null) {
                masterSet = new TreeSet<VirtualWebroot>(new AssetApiVirtualWebrootDao.UrlInfoComparator());
                webrootsByEnvironment.put("GST-MASTER", masterSet);
            }
            masterSet.add(vw);
        }

        // loop over everything, displaying results.

        // First, for each environment
        for (String environment : webrootsByEnvironment.keySet()) {
            out.println("<h3>Environment: " + environment + "</h3>");
            // Second, for each root in the environemnt
            String currentAuthority = "";
            boolean addHeader = true;
            for (VirtualWebroot vw : webrootsByEnvironment.get(environment)) {
                String sVW = "GST-MASTER".equals(environment) ? vw.getMasterVirtualWebroot() : vw.getEnvironmentVirtualWebroot();
                URI uri = new URI(sVW);
                if ("GST-MASTER".equals(environment)) {
                } else if (!currentAuthority.equals(uri.getAuthority())) {
                    currentAuthority = uri.getAuthority();
                }
                if (addHeader) {
                    out.println("<p>VirtualHost: <em>" + uri.getAuthority() + "</em></p>");
                    out.println("<pre>");
                    out.println("# GST Site Foundation rewrite conditions blocking rewriting of URLs matching standard servlet paths");
                    out.println("RewriteCond %{REQUEST_URI} !^" + ics.GetProperty("ft.cgipath") + "*");
                    out.println("RewriteCond %{REQUEST_URI} !^/cas/*");
                    out.println("# GST Site Foundation rew rite rules mapping this environment's VirtualWebroot assets to Satellite Server");
                    addHeader = false;
                }
                out.println("RewriteRule ^" + uri.getPath() + "(.*)$ " + satelliteServerBasePath + "?virtual-webroot=" + sVW + "&pagename=" + dispatcherPageName + "&url-path=$1 [QSA,PT]");
            }
            if (addHeader == false) {
                out.println("</pre>");
            }
        }
    %>

    <%-- TODO: add Tuckey rules --%>

    <h2>Rule generation complete.</h2>
    <satellite:link pagename="GST/Foundation/Dashboard" />
    <p>Return to <a href="${cs.referURL}">GSF Dashboard</a>.</p>

    </body>
    </html>
</gsf:root></cs:ftcs>