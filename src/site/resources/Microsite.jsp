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
<%@ taglib prefix="asset" uri="futuretense_cs/asset.tld" %>
<%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld" %>
<%@ taglib prefix="commercecontext" uri="futuretense_cs/commercecontext.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%@ taglib prefix="listobject" uri="futuretense_cs/listobject.tld" %>
<%@ taglib prefix="render" uri="futuretense_cs/render.tld" %>
<%@ taglib prefix="siteplan" uri="futuretense_cs/siteplan.tld" %>
<%@ taglib prefix="searchstate" uri="futuretense_cs/searchstate.tld" %>
<%@ page import="java.util.ArrayList,
                 java.util.Map,
                 com.fatwire.assetapi.data.AssetData,
                 com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils,
                 com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils,
                 com.fatwire.gst.foundation.taglib.NavigationHelper" %>

<cs:ftcs>
    <ics:if condition='<%=ics.GetVar("tid")!=null%>'><ics:then><render:logdep cid='<%=ics.GetVar("tid")%>' c="Template"/></ics:then></ics:if>
    <%
        AssetData ad = AssetDataUtils.getAssetData(ics.GetVar("c"), ics.GetVar("cid"), 
                "metatitle", "metadescription", "metakeyword", "h1title", "linktext", "body");
    %>
    <html>
    <head>
		<meta name="title" content='<%=AttributeDataUtils.asString(ad.getAttributeData("metatitle"))%>' />
		<meta name="description" content='<%=AttributeDataUtils.asString(ad.getAttributeData("metadescription"))%>' />
		<meta name="keyword" content='<%=AttributeDataUtils.asString(ad.getAttributeData("metakeyword"))%>' />
		<title><%=AttributeDataUtils.asString(ad.getAttributeData("metatitle"))%> | <ics:getvar name="site" /></title>
    </head>
    <body>
	
    <div id="nav">
        <asset:load name="main-nph" type="Page" field="name" value="MainNav"/>
        <asset:get name="main-nph" field="id" output="main-nph-id"/>
        <%
            NavigationHelper nh = new NavigationHelper(ics);
            Map<String, Object> nav = nh.getSitePlanAsMap(ics.GetVar("main-nph-id"));
        %>
        <ul>
        <%
            for(Map<String,Object> kid : (ArrayList<Map<String,Object>>)nav.get("children")) {
        %>
            <li>
                <a href='<%=kid.get("url")%>'>
                    <%=kid.get("linktext")%>
                </a>
            </li>
        <%
            }
           %>
    </div>

	<p>Current date is <strong><%= new java.util.Date()%></strong></p>

    <h1><%=AttributeDataUtils.asString(ad.getAttributeData("h1title"))%></h1>

    <div class="articlebody">
        <%=AttributeDataUtils.asString(ad.getAttributeData("body"))%>
    </div>

    </body>
    </html>

</cs:ftcs>