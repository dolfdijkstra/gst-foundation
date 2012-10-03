<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="ics" uri="futuretense_cs/ics.tld"%><%@ taglib prefix="render"
	uri="futuretense_cs/render.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><cs:ftcs>
	<gsf:root action="avisports/AVIArticle/GetLink">
		<gsf:asset-load name="article" attributes="headline" />
		<a href="${articleUrl}">${article.headline}</a>
	</gsf:root>
</cs:ftcs>