<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib prefix="gsf"
	uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><cs:ftcs>
	<gsf:root action="+">
		<div class="header-bar">
			<h1 class="logo">
				<a href="#">AviSports logo</a>
			</h1>
		</div>
		<ul class="navigation">
			<li class="active"><c:if test="${not empty navNode}">
					<a href="${navNode.url}">HOME</a>
				</c:if>
			</li>
			<c:forEach var="node" items="${navNode.children}">
				<li><a href="${node.url}"><span>${node.linktext}</span> <em class="arrow">&nbsp;</em></a></li>
			</c:forEach>
		</ul>
	</gsf:root>
</cs:ftcs>