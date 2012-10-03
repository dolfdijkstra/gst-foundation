<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/>
<ics:callelement element="avisports/Page/GetHome" />
<ics:ifnotempty variable="AVIHomeId">
<ics:then>
	<render:callelement elementname="avisports/Page/GetLink" scoped="global">
		<render:argument name="assetid" value='<%=ics.GetVar("AVIHomeId")%>' />
	</render:callelement>
	<a href="<ics:getvar name="pageUrl" />">HOME</a>
</ics:then>
</ics:ifnotempty>
</cs:ftcs>