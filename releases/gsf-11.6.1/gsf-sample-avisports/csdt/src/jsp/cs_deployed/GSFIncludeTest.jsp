<%@page trimDirectiveWhitespaces="true" %><%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><cs:ftcs>
	<gsf:root action="gsf/IncludeTest">
	 The title is '${title }'.<br/>
		<gsf:include name="sample"/>
	</gsf:root>
</cs:ftcs>