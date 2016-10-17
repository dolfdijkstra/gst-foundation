<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="fn"
	uri="http://java.sun.com/jsp/jstl/functions"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="asset" attributes="relatedStories,relatedLinks" />
		<insite:slotlist field="relatedStories" tname="Summary/Highlight"
			countervar="articleNb"
			emptytext=" [ Drop Article #$(Variables.articleNb) Here ]">
			<div class="box">
				<h2 class="title">Related Stories</h2>
				<c:forEach var="article" items="${asset.relatedStories}" end="2">
					<insite:calltemplate c="AVIArticle" cid="${article.id}"
						cssstyle="highlight" />
				</c:forEach>
				<insite:ifedit>
					<%-- in edit mode, draw up to 2 empty slots with Summary/Highlight --%>
					<c:forEach begin="${fn:length(asset.relatedStories) + 1}" end="2">
						<%-- tag inherits all attributes from parent insite:slotlist tag--%>
						<insite:calltemplate cssstyle="highlight" />
					</c:forEach>
				</insite:ifedit>
			</div>
		</insite:slotlist>
		<insite:slotlist field="relatedLinks" tname="Summary/Link"
			countervar="articleNb"
			emptytext=" [ Drop Article #$(Variables.articleNb) Here ]">
			<div class="box">
				<h2 class="title">Related Links</h2>
				<ul>
					<c:forEach var="article" items="${asset.relatedLinks}">
						<li><insite:calltemplate c="AVIArticle" cid="${article.id}" /></li>
					</c:forEach>
					<insite:ifedit>
						<%-- in edit mode, draw up to 5 empty slots with Summary/SideBar --%>
						<c:forEach begin="${fn:length(asset.relatedLinks) + 1 }" end="6">
							<%-- tag inherits all attributes from parent insite:slotlist tag --%>
							<li><insite:calltemplate /></li>
						</c:forEach>
					</insite:ifedit>
				</ul>
			</div>
		</insite:slotlist>
	</gsf:root>
</cs:ftcs>