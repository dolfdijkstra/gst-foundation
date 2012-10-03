<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="home" attributes="bannerText,banner" />
		<insite:calltemplate tname="Detail" c="AVIImage"
			cid="${home.banner.id}" field="banner" cssstyle="aviHome2Banner" />
		<div class="teaser">
			<insite:edit field="bannerText" value="${home.bannerText }"
				params="{noValueIndicator: '[ Enter Text ]', toolbarStartupExpanded: false, width: '430px', height: '90px', toolbar: 'Home', customConfig: '../avisports/ckeditor/config.js'}"
				editor="ckeditor" />
		</div>
	</gsf:root>
</cs:ftcs>