<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%><cs:ftcs>
	<gsf:root>
		<gsf:asset-load name="home"
			attributes="banner,topArticles,bannerTitle" />
		<insite:calltemplate tname="Detail" c="AVIImage"
			cid='${home.banner.id}' field="banner" cssstyle="aviHomeBanner" />
		<div class="top-stories">
			<h2>
				<insite:edit field="bannerTitle" value="${home.bannerTitle}"
					params="{noValueIndicator: '[ Enter Headline ]'}" />
			</h2>
			<ul>
				<c:if test="${!empty home.topArticles}">
					<c:forEach items="${home.topArticles }" end="2" var="article"
						varStatus="status">
						<li><insite:calltemplate tname="Summary" c="AVIArticle"
								cid='${article.id}' field="topArticles" index='${status.index}'
								title="Top Article - #${status.index}"
								cssstyle="aviHomeTopStories" /></li>
					</c:forEach>

				</c:if>
				<%--  make sure we have at least 2 empty slots --%>
				<insite:ifedit>
					<c:forEach begin='${fn:length(home.topArticles)+1}' end="2"
						varStatus="status">
						<li><insite:calltemplate tname="Summary" field="topArticles"
								index="${status.index}" title="Top Article - #${status.index}"
								emptytext="[ Drop Article #${status.index}"
								cssstyle="aviHomeTopStories" /></li>
					</c:forEach>
				</insite:ifedit>
			</ul>
		</div>
	</gsf:root>
</cs:ftcs>
