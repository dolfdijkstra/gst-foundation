<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="string" uri="futuretense_cs/string.tld"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/>
<ics:ifempty variable="assetid">
<ics:then>
	<ics:setvar name="assetid" value='<%=ics.GetVar("cid") %>' />
</ics:then>
</ics:ifempty>
<asset:list type="Page" field1="id" value1='<%=ics.GetVar("assetid")%>' list="page" />
<ics:listget listname="page" fieldname="template" output="template" />
<render:gettemplateurl	outstr="_pageUrl" tname='<%= ics.GetVar("template")%>' c="Page" cid='<%=ics.GetVar("assetid") %>'
						ttype="CSElement" />
<string:encode varname="pageUrl" variable="_pageUrl"/>
</cs:ftcs>