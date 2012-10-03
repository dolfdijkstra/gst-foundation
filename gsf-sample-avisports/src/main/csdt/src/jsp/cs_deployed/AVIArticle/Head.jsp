<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"
	%><cs:ftcs><gsf:root><gsf:asset-load name="article" attributes="headline,subheadline" />
<title>${article.headline}</title>
<meta name="description" content="${article.subheadline}" />
</gsf:root>
</cs:ftcs>