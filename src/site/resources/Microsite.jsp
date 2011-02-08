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
<%@ taglib prefix="asset" uri="futuretense_cs/asset.tld" 
%><%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld" 
%><%@ taglib prefix="commercecontext" uri="futuretense_cs/commercecontext.tld" 
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" 
%><%@ taglib prefix="listobject" uri="futuretense_cs/listobject.tld" 
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld" 
%><%@ taglib prefix="siteplan" uri="futuretense_cs/siteplan.tld" 
%><%@ taglib prefix="searchstate" uri="futuretense_cs/searchstate.tld" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@ taglib uri="http://gst.fatwire.com/foundation/tags/gsf" prefix="gsf"
%><%@ page import="java.util.ArrayList,
                 java.util.Map,
                 com.fatwire.assetapi.data.AssetData,
                 com.fatwire.assetapi.data.AssetId,
                 com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate,
                 com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAsset,
                 com.fatwire.gst.foundation.taglib.NavNode,
                 com.fatwire.gst.foundation.taglib.NavigationHelper" %>
<gsf:root>
    <%-- gsf:root tags takes care of log deps --%>
<%

ScatteredAssetAccessTemplate assetTemplate = new ScatteredAssetAccessTemplate(ics);
ScatteredAsset asset = assetTemplate.readCurrent("metatitle", "metadescription", "metakeyword", "h1title", "linktext", "body");

%><c:set var="asset" value="<%= asset %>" />
    <html>
    <head>
		<meta name="title" content='${asset.metatitle}' />
		<meta name="description" content='${asset.metadescription}' />
		<meta name="keyword" content='${asset.metakeyword}' />
		<title>${asset.metatitle} | <ics:getvar name="site" /></title>
    </head>
    <body>
	
    <div id="nav"><% 
            NavigationHelper nh = new NavigationHelper(ics);
            NavNode nav = nh.getSitePlanByPage(1,"MainNav");
        %>
        <ul>
        <c:forEach var="kid" items="<%= nav.getChildren() %>">
        <%
            //for(NavNode kid : nav.getChildren()) {
        %><li>
                <a href='${kid.url}'>${kid.linktext}</a>
            </li>
        </c:forEach>
    </div>

	<p>Current date is <strong><%= new java.util.Date()%></strong></p>

    <h1>${asset.h1title}</h1>

    <div class="articlebody">${asset.body}</div>

    </body>
    </html>
</gsf:root>
