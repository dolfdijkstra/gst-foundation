<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib prefix="insite" uri="futuretense_cs/insite.tld"%><%@ taglib
	prefix="ics" uri="futuretense_cs/ics.tld"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib
	prefix="render" uri="futuretense_cs/render.tld"%><cs:ftcs>
	<gsf:root><!DOCTYPE html>
		<ics:setvar name="context" value='SectionLayout:"${cs.cid}:${cs.bg-color}' />
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
			<div class='center-column ${cs.bg-color}'>
				<div class="post-wrapper">
					<h2 class="title">
						<insite:edit field="titleContent1" value="${asset.titleContent1}"
							params='{noValueIndicator: "[ Enter Headline ]"}' />
					</h2>
					<div class="post">
						<c:set var="index" value="1" />
						<insite:calltemplate tname="Summary/Feature" c="AVIArticle" cid='${asset.content1[0].id}'
							slotname="SectionLayoutSlotLeft" field="content1" title="Main List - Article #1" emptytext="[ Drop Article #1 ]"
							variant="Summary.*" index="1" />
					</div>
					<c:forEach var="article" items="${asset.content1}" begin="1" varStatus="status">
						<c:set var="index" value="${status.index + 1}" />
						<div class="post">
							<insite:calltemplate tname="Summary" c="AVIArticle" cid='${article.id}' slotname="SectionLayoutSlotLeft"
								field="content1" title="Main List - Article &#35;${index}" emptytext="[ Drop Article &#35;${index} ]"
								variant="Summary.*" index='${index}' />
						</div>
					</c:forEach>
					<insite:ifedit>
						<%-- in edit mode, draw up to 4 extra empty slots --%>
						<c:forEach begin='${index + 1 }' end="5" varStatus="status">
							<div class="post">
								<insite:calltemplate tname="Summary" slotname="SectionLayoutSlotLeft" field="content1"
									title="Main List - Article &#35;${status.index}" emptytext="[ Drop Article &#35;${status.index}"
									variant="Summary.*" index="${status.index}" />
							</div>
						</c:forEach>
					</insite:ifedit>
				</div>
				<div class="post-wrapper">
					<h2 class="title">
						<insite:edit field="titleContent2" value="${asset.titleContent2}"
							params='{noValueIndicator: "[ Enter Headline ]"}' />
					</h2>
					<c:forEach var="article" items="${asset.content2}" varStatus="status">
						<c:set var="index" value="${status.index + 1}" scope="page" />
						<div class="post">
							<insite:calltemplate tname="Summary" c="AVIArticle" cid='${article.id}' slotname="SectionLayoutSlotRight"
								field="content2" title="Secondary List - Article &#35;${index}" emptytext="[ Drop Article &#35;${index} ]"
								variant="Summary.*" index='${index}' />
						</div>
					</c:forEach>
					<insite:ifedit>
						<%-- in edit mode, draw up to 5 empty slots --%>
						<c:forEach begin='${index + 1}' end="5" varStatus="status">
							<div class="post">
								<insite:calltemplate tname="Summary" slotname="SectionLayoutSlotRight" field="content2"
									title="Secondary List - Article &#35;${status.index}" emptytext="[ Drop Article &#35;${status.index} ]"
									variant="Summary.*" index="${status.index}" />
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