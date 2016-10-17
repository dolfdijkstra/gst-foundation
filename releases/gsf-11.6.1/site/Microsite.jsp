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
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://gst.fatwire.com/foundation/tags/gsf" prefix="gsf"%>
<cs:ftcs><gsf:root>
<gsf:asset-load name="wra" attributes="metatitle,metadescription,metakeyword,h1title,linktext,body" />
    <html>
    <head>
		<meta name="title" content='${wra.metatitle}' />
		<meta name="description" content='${wra.metadescription}' />
		<meta name="keyword" content='${wra.metakeyword}' />
		<title>${wra.metatitle} | ${cs.site}</title>
    </head>
    <body>
	
    <div id="nav"><gsf:navigation name="nav" depth="1" pagename="MainNav"/>
        <ul>
        <c:forEach var="kid" items="${nav.children}">
          <li><a href='${kid.url}'>${kid.linktext}</a></li>
        </c:forEach>
        </ul>
    </div>

	<p>Current date is <strong><%= new java.util.Date()%></strong></p>

    <h1>${wra.h1title}</h1>

    <div class="articlebody">${wra.body}</div>

    </body>
    </html>
</gsf:root>
</cs:ftcs>