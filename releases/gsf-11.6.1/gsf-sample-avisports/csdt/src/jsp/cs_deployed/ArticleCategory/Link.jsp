<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><cs:ftcs>
	<gsf:root action="ArticleCategory/Link">
		<c:if test="${not empty pageUrl }">
			<a href="${pageUrl}">${title}</a>
		</c:if>
	</gsf:root>
</cs:ftcs>
