<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="render"
	uri="futuretense_cs/render.tld"%><cs:ftcs>
	<gsf:root action="avisports/AVIArticle/Summary">
		<c:if test="${not empty asset.relatedImage}">
			<a href='${articleUrl}'><render:calltemplate
					tname="Summary/Large" c="AVIImage" cid="${asset.relatedImage.id}"
					style="element" /></a>
		</c:if>
		<h3 class="title">
			<a href='${articleUrl}'><insite:edit field="headline"
					value="${asset.headline}" /></a>
		</h3>
		<p class="descr">
			<insite:edit field="abstract" value='${asset["abstract"]}' />
		</p>
	</gsf:root>
</cs:ftcs>