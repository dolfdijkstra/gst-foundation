<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="insite" uri="futuretense_cs/insite.tld"
%><%@ page import="COM.FutureTense.Interfaces.*"
%><cs:ftcs>><render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/>
<assetset:setasset name="home" type="Page" id='<%=ics.GetVar("cid") %>' />
<assetset:getattributevalues name="home" attribute="teaserText" listvarname="teasers" typename="PageAttribute" />
<assetset:getattributevalues name="home" attribute="bannerText" listvarname="teaserImages" typename="PageAttribute" /><%
IList teaserImages = ics.GetList("teaserImages");
IList teasers = ics.GetList("teasers");%>
<div class="post">
	<a href="#" class="photo">  
		<ics:listget listname="teaserImages" fieldname="value" output="imageId" />
		<insite:calltemplate tname="Detail" c="AVIImage" cid='<%=ics.GetVar("imageId") %>' 
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>'
							 field="teaserImages" index="1" />
		<ics:removevar name="imageId" />
	</a>
	<div class="descr">
		<insite:edit column="value" list="teasers" editor="ckeditor" 
					 assettype="Page" assetid='<%=ics.GetVar("cid") %>'
					 field="teaserText" index="1" />
	</div>
</div>

<div class="post">
	<a href="#" class="photo">
		<%if (teaserImages != null && teaserImages.moveToRow(IList.next, 0)) {
		%>
			<ics:listget listname="teaserImages" fieldname="value" output="imageId" />
		<%}%>
		<insite:calltemplate tname="Detail" c="AVIImage" cid='<%=ics.GetVar("imageId") %>'
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>'
							 field="teaserImages" index="2" />
	</a>
	<div class="descr">
		<%if (teasers != null && teasers.moveToRow(IList.next, 0)) {%>
			<ics:listget listname="teasers" fieldname="value" output="teaser" />
		<%} %>
		
		<insite:edit field="teaserText" index="2" variable="teaser" editor="ckeditor"
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>' />
		<ics:removevar name="teaser" />
		<ics:removevar name="imageId" />
		
	</div>
</div>

<div class="post">
	<a href="#" class="photo">
		<% if (teaserImages != null && teaserImages.moveToRow(IList.next, 0)) {%>
			<ics:listget listname="teaserImages" fieldname="value" output="imageId" />
		<%} %>
		<insite:calltemplate tname="Detail" c="AVIImage" cid='<%=ics.GetVar("imageId") %>'
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>'
							 field="teaserImages" index="3" />
	</a>
	<div class="descr">
		<%if (teasers != null && teasers.moveToRow(IList.next, 0)) {%>
			<ics:listget listname="teasers" fieldname="value" output="teaser" />
		<%}%>
			<insite:edit field="teaserText" index="3" variable="teaser" editor="ckeditor" 
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>' />
		<ics:removevar name="teaser" />
		<ics:removevar name="imageId" />
	</div>
</div>
<div class="post">
	<a href="#" class="photo">
		<%if (teaserImages != null && teaserImages.moveToRow(IList.next, 0)) {
		%>
		<ics:listget listname="teaserImages" fieldname="value" output="imageId" />
		<%} %>
		<insite:calltemplate tname="Detail" c="AVIImage" cid='<%=ics.GetVar("imageId") %>' 
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>'
							 field="teaserImages" index="4" />
		<ics:removevar name="imageId" />
	</a>
	<div class="descr">
		<%if (teasers != null && teasers.moveToRow(IList.next, 0)) {%>
			<ics:listget listname="teasers" fieldname="value" output="teaser" />
		<%}%>
		<insite:edit field="teaserText" index="4" variable="teaser" editor="ckeditor" 
							 assettype="Page" assetid='<%=ics.GetVar("cid") %>' />
	</div>
</div>
</cs:ftcs>