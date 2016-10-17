<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld"
%><%@ taglib prefix="commercecontext" uri="futuretense_cs/commercecontext.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="listobject" uri="futuretense_cs/listobject.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="siteplan" uri="futuretense_cs/siteplan.tld"
%><cs:ftcs>
<ics:if condition='<%=ics.GetVar("seid")!=null%>'><ics:then><render:logdep cid='<%=ics.GetVar("seid")%>' c="SiteEntry"/></ics:then></ics:if>
<ics:if condition='<%=ics.GetVar("eid")!=null%>'><ics:then><render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/></ics:then></ics:if>
	<ics:ifempty variable="pageId">
	<ics:then>
		<ics:setvar name="pageId" value='<%=ics.GetVar("cid") %>' />
	</ics:then>
	</ics:ifempty>
	<asset:load name="page" type="Page" objectid='<%=ics.GetVar("pageId") %>' />
	<asset:getsitenode name="page" output="pageNodeId"/>
	<siteplan:load name="pageNode" nodeid='<%=ics.GetVar("pageNodeId") %>' />
	<siteplan:children name="pageNode" list="level1Children" code="Placed" order="nrank" />
	<ics:listloop listname="level1Children">
		<ics:listget listname="level1Children" fieldname="oid" output="childId" />
		<assetset:setasset name="child" type="Page" id='<%=ics.GetVar("childId")%>' />
		<assetset:getattributevalues name="child" attribute="title" listvarname="title" typename="PageAttribute" />
		<asset:list list="pageList" field1="id" value1='<%=ics.GetVar("childId") %>' type="Page" />
		<render:callelement elementname="avisports/Page/GetLink" scoped="global">
			<render:argument name="assetid" value='<%=ics.GetVar("childId") %>' />
		</render:callelement>
		<ics:listget listname="title" fieldname="value" output="title" />
		<ics:ifempty variable="title">
		<ics:then>
 			<ics:listget listname="pageList" fieldname="name" output="title" />
		</ics:then>
		</ics:ifempty>
		<li><a href="<ics:getvar name="pageUrl" />"><span><ics:getvar name="title" /></span><em class="arrow">&nbsp;</em></a></li>
		<ics:removevar name="title" />
	</ics:listloop>
</ul>

</cs:ftcs>
