<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib
	prefix="string" uri="futuretense_cs/string.tld"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="asset"
			attributes="imageFile,caption,width,height,alternateText" />

		<render:getbloburl outstr="imageUrl" c="AVIImage" cid='${cs.cid }'
			field="imageFile" />
		<img src="<string:stream variable="imageUrl" />"
			alt="${asset.alternateText}" width="${asset.width}"
			height="${asset.height}" />
		<div class="caption">
			<insite:edit field="caption" value="${asset.caption}"
				params="{noValueIndicator: 'Enter Image Caption'}" />
		</div>
	</gsf:root>
</cs:ftcs>