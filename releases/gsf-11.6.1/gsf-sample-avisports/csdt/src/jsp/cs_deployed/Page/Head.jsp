<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="head" attributes="metaTitle,metaDescription" />
		<title>${head.metaTitle}</title>
		<meta name="description" content="${head.metaDescription}" />
	</gsf:root>
</cs:ftcs>