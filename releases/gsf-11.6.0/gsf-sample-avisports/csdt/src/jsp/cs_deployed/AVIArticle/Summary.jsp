<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><cs:ftcs>
	<gsf:root action="avisports/AVIArticle/GetLink">
		<gsf:asset-load name="asset"
			attributes="headline,relatedImage,abstract" />
		<c:if test="${not empty asset.relatedImage}">
			<a href='${articleUrl}'> <render:calltemplate tname="Summary"
					c="AVIImage" cid="${asset.relatedImage.id}" style="element"
					args="thumbnail" />
			</a>
		</c:if>

		<div class="descr ${cs.thumbnail}">
			<h3>
				<a href='${articleUrl}'><insite:edit field="headline"
						value="${asset.headline}" /></a>
			</h3>
			<insite:edit field="abstract" value='${asset["abstract"]}' />
		</div>
	</gsf:root>
</cs:ftcs>