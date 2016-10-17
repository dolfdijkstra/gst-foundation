<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><cs:ftcs>
	<gsf:root>
		<!DOCTYPE html>
		<html lang="en">
<head>
<render:calltemplate tname="/Head" args="c,cid" style="element" />
</head>
<body class="article-layout left-col">
	<div id="main">
		<div id="header">
			<render:callelement elementname="avisports/navbar" />
		</div>
		<div id="container">
			<div class="content">
				<render:calltemplate tname="Detail" args="c,cid" style="element" />
			</div>
			<div class="sidebar">
				<render:calltemplate tname="SideBar" args="c,cid" style="element" />
			</div>
		</div>
		<div id="footer">
			<render:callelement elementname="avisports/footer" />
		</div>
	</div>
</body>
		</html>
	</gsf:root>
</cs:ftcs>