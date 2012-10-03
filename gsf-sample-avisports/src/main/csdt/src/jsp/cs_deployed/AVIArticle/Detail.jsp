<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib
	prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="asset"
			attributes="headline,subheadline,author,body,relatedStories,relatedImage,postDate,category" />
		<fmt:formatDate value="${asset.postDate}" dateStyle="long" type="date"
			var="formattedDate" />

		<div class="top-section">
			<span class="section-title"> <insite:calltemplate
					field="Group_Category" tname="Link" c="ArticleCategory"
					cid='${not empty asset.Group_Category ? asset.Group_Category[0].id : ""}'
					emptytext="[ Drop Article Category ]" title="Category"
					cssstyle="aviArticleCategory" />
			</span> <span class="date"><insite:edit field="postDate"
					value='${formattedDate}'
					params="{constraints: {formatLength: 'long'}, noValueIndicator: '[ enter post date ]', width: '150px'}" /></span>
		</div>
		<div class="title-section">
			<h1>
				<insite:edit field="headline" value="${asset.headline}"
					params="{noValueIndicator: '[ enter headline ]'}" />
			</h1>
			<h2>
				<insite:edit field="subheadline" value="${asset.subheadline}"
					params="{noValueIndicator: '[ enter subheadline ]'}" />
			</h2>
			<span class="author">BY <insite:edit field="author"
					value="${asset.author}"
					params="{noValueIndicator: '[ enter author ]'}" /></span>
		</div>
		<div class="article">
			<div class="article-image">
				<insite:calltemplate field="relatedImage"
					c="${asset.relatedImage.type}" cid="${asset.relatedImage.id}"
					tname="Detail" emptytext="[ Drop Image ]" />
			</div>
			<insite:edit field="body" value="${asset.body}" editor="ckeditor"
				params="{noValueIndicator: 'Enter body ', width: '627px', height: '500px', toolbar: 'Article', customConfig: '../avisports/ckeditor/config.js'}" />
		</div>
	</gsf:root>
</cs:ftcs>