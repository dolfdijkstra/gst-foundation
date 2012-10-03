<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%>
<cs:ftcs><gsf:root>
<gsf:asset-load name="wra" attributes="metaTitle,metaDescription,banner,bannerTitle,bannerText,banners,title" />

<html>
<head>
     <meta name="title" content='${wra.metaTitle}' />
     <meta name="description" content='${wra.metaDescription}' />
     <title>${wra.metaTitle} | ${cs.site}</title>
</head>
<body>
             
<div id="nav"><gsf:navigation name="nav" depth="1" pagename="Home"/>
	<ul>
	<c:forEach var="kid" items="${nav.children}">
	  <li><a href='${kid.url}'>${kid.linktext}</a></li>
	</c:forEach>
	</ul>
</div>
     <p>Current date is <strong><%= new java.util.Date()%></strong></p>
 
    <h1>${wra.title}</h1>
    <div class="articlebody">${wra.bannerText}</div>
</body>
</html>
</gsf:root>
</cs:ftcs>