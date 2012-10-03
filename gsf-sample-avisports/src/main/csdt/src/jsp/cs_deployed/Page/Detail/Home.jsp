<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="home" attributes="teaserText,teaserImages" />
		<c:forEach var="i" begin="0" end="3">
			<div class="post">
				<insite:calltemplate tname="Detail" c="AVIImage"
					cid="${empty home.teaserImages[i] ? null : home.teaserImages[i].id}"
					assettype="Page" assetid="${cs.cid }" field="teaserImages"
					index="${i+1}" cssstyle="aviHomeDetailImage"
					title="Teaser image #${i+1 }" />
				<div class="descr">
					<insite:edit value="${home.teaserText[i] }" editor="ckeditor"
						field="teaserText" index="${i+1 }"
						params="{noValueIndicator: '[ Enter Teaser Text #${i+1 } ]', width: '195px', toolbar: 'Home', customConfig: '../avisports/ckeditor/config.js'}" />
				</div>
			</div>
		</c:forEach>
	</gsf:root>
</cs:ftcs>