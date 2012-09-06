<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"%><%@ taglib
	prefix="gsf" uri="http://gst.fatwire.com/foundation/tags/gsf"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><cs:ftcs>
	<gsf:root action="ProductInfoAction">
		<h1>${product.brandName}</h1>
		<h2>${product.shortDescription}</h2>
		<h3>
			<!-- CatalogCodes: ${product.catalogCodes}-->
			MediaCode: ${product.mediaCode}
		</h3>

		<div style="float: left; overflow: hidden; width: 50%;">

			function replaceimg(imgtag){ imgtag.src="${imgurlprodpri}";
			imgtag.onerror=null; }
			</script>
			<img src="${productURL}" onerror="replaceimg(this)" />
			<p>${product.longDescription}
		</div>
		<div style="overflow: hidden;">
			<!--  include pricing block -->
			<p:include name="pricing" />
			<p>
				<c:forEach var="item" items="${product.moreInfoAssoc}">
					<div style="overflow: hidden">
						<c:if test="${item.image}">
							<img src="${item.url}" />
							<br>
						</c:if>
						${item.linkText}
					</div>
				</c:forEach>
			<p>
				<%-- replacement for render:callelement --%>
				<!--  include selectorset block -->
				<p:include name="selectorset" />
		</div>
		<div>
			SKUS: <select>
				<c:forEach var="sku" items="${product.skus}">
					<option>${sku}</option>
				</c:forEach>
			</select>
			<c:forEach var="item" items="${product.items}">
				<br />${item.itemNumber}:${item.colorCodes}:${item.sizeCodes}:${item.description}</c:forEach>
		</div>
	</gsf:root>
</cs:ftcs>