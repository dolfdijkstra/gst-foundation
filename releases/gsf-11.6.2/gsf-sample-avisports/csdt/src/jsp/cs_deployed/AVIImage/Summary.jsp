<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><%@ taglib
	prefix="string" uri="futuretense_cs/string.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%>
<cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="asset"
			attributes='alternateText,${cs["thumbnail-field"]}' />
		<render:getbloburl outstr="imageUrl" c="AVIImage" cid='${cs.cid}'
			field='${cs["thumbnail-field"]}' />
		<img class="photo ${cs.thumbnail}"
			src="<string:stream variable="imageUrl" />"
			alt="${image.alternateText}" />
	</gsf:root>
</cs:ftcs>
