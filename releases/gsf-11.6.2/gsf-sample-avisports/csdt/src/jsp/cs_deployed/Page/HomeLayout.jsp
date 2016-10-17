<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><cs:ftcs>
	<gsf:root>
		<!DOCTYPE html>
		<html lang="en">
<head>
<render:calltemplate tname="/Head" args="c,cid" style="element" />
</head>
<body class="home">
	<div id="main">
		<div id="header">
			<render:satellitepage pagename="avisports/navbar" />
		</div>
		<div id="container">
			<div class="content">
				<div class="banner">
					<render:calltemplate tname="Banner/Home" args="c,cid"
						style="element" />
				</div>
			</div>
			<div class="center-column">
				<div class="post-wrapper">
					<render:calltemplate tname="Detail/Home" args="c,cid"
						style="element" />
				</div>
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