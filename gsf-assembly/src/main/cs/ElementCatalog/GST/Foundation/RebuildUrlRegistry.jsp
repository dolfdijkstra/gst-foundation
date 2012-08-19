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
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld" %>
<%@ taglib prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf" %>
<%
    //
    // GST/Foundation/RebuildUrlRegistry
    //
%>
<cs:ftcs><gsf:root>
    <html>
    <head>
        <title>GSF URL Registry Rebuild Tool</title>
        <link href="/cs/Xcelerate/data/css/en_US/common.css" rel="styleSheet" type="text/css">
        <link href="/cs/Xcelerate/data/css/en_US/content.css" rel="styleSheet" type="text/css">
    </head>
    <body>
    <h1>GSF URL Registry Rebuild Tool</h1>

    <h2>Rebuilding In progress...</h2>
    <%
        com.fatwire.gst.foundation.url.db.UrlRegistry2.lookup(ics).rebuild(true);
    %>
    <h2>Rebuild Completed.</h2>
    <satellite:link pagename="GST/Foundation/Dashboard" />
    <p>Return to <a href="${cs.referURL}">GSF Dashboard</a>.</p>

    </body>
    </html>
</gsf:root></cs:ftcs>