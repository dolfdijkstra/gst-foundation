<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib
	prefix="ics" uri="futuretense_cs/ics.tld"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><cs:ftcs>
	<gsf:root>
		<!DOCTYPE html>
		<ics:setvar name="context" value='SectionLayout4:"${cs.cid}' />
		<gsf:asset-load name="asset" attributes="banner,content1,content2,titleContent1,titleContent2" />
		<html lang="en">
<head>
<render:calltemplate tname="/Head" args="c,cid" style="element" />
</head>
<body class="section">
	<div id="main">
		<div id="header">
			<render:satellitepage pagename="avisports/navbar" />
		</div>
		<div id="container">
			<div class="content">
				<insite:calltemplate tname="Detail" c="AVIImage" cid='${asset.banner.id}' field="banner" cssstyle="aviSectionBanner"
					emptytext="[ Drop Banner Image ]" />
			</div>
			<div class="center-column blue">
				<div class="post-wrapper">
					<h2 class="title">
						<insite:edit field="titleContent1" value="${asset.titleContent1}"
							params='{noValueIndicator: "[ Enter Headline ]"}' />
					</h2>
					<c:forEach var="article" items="${asset.content1}" varStatus="status">
						<div class="post-large">
							<insite:calltemplate tname="Summary/Feature" c="AVIArticle" cid="${article.id}" field="content1"
								index="${status.index + 1}" title="Main List - Article &#35;${status.index}"
								emptytext="[ Drop Article &#35;${status.index} ]" />
						</div>
					</c:forEach>
					<insite:ifedit>
						<%-- in edit mode, draw up to 2 empty slots --%>
						<c:forEach begin="${index + 1}" end="2" varStatus="status">
							<div class="post-large">
								<insite:calltemplate tname="Summary/Feature" field="content1" index="${status.index}"
									title="Main List - Article &#35;${status.index}" emptytext="[ Drop Article &#35;${status.index} ]" />
							</div>
						</c:forEach>
					</insite:ifedit>
				</div>
				<div class="post-wrapper">
					<h2 class="title">
						<insite:edit field="titleContent2" value="${asset.titleContent2}" params='{noValueIndicator: "Enter Headline"}' />
					</h2>
					<c:forEach var="article" items="${asset.content2}">
						<div class="post-large">
							<insite:calltemplate tname="Summary/Feature" c="AVIArticle" cid="${article.id}" field="content2"
								index="${status.index + 1}" title="Secondary Article - Article &#35;${index}"
								emptytext="[ Drop Article &#35;${index} ]" />
						</div>
					</c:forEach>
					<insite:ifedit>
						<%-- in edit mode, draw up to 5 empty slots --%>
						<c:forEach begin="${index + 1}" end="5" varStatus="status">
							<div class="post-large">
								<insite:calltemplate tname="Summary/Feature" field="content2" index="${status.index}"
									title="Secondary Article List" emptytext="[ Drop Article #${status.index} ]" />
							</div>
						</c:forEach>
					</insite:ifedit>
				</div>
			</div>
		</div>
		<div id="footer">
			<render:callelement elementname="avisports/footer" />
		</div>
	</div>
</body>
		</html>
	</gsf:root>
</cs:ftcs>