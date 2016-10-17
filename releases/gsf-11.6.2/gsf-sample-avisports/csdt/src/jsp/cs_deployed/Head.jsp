<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="insite" uri="futuretense_cs/insite.tld"
%><cs:ftcs><render:logdep cid='<%=ics.GetVar("tid")%>' c="Template"/>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<link type="text/css" rel="stylesheet" href="avisports/css/all.css"/>
<link type="text/css" rel="stylesheet" href="avisports/css/form.css"/>
<insite:ifedit>
<style>
.fw .aviHome2TopArticle .emptyIndicator {height: 40px !important;}
.fw .aviHome2Banner .emptyIndicator, .fw .aviHomeBanner .emptyIndicator {height: 300px !important;}
.fw .aviHomeDetailImage .emptyIndicator {height: 170px !important;}
.fw .aviArticleCategory {float:left; width: 130px !important; margin: 0}
.fw .aviArticleCategory .emptyIndicator {height: 15px !important; width: 130px !important; float: left}
.fw .aviSectionBanner .emptyIndicator {height: 216px !important;}
.fw .post .fwSlot .slotOverlay {min-height: 90px !important}
.fw .aviHomeTopStories {width: 360px; height: 90px !important;}
</style>
</insite:ifedit>
<!--[if lt IE 7]>
	<link type="text/css" rel="stylesheet" href="css/lt7.css"/>
	<script type="text/javascript" src="avisports/js/DD_belatedPNG_0.0.8a-min.js"></script>
<![endif]-->
<render:calltemplate tname="Head" args="c,cid" style="element" />
</cs:ftcs>