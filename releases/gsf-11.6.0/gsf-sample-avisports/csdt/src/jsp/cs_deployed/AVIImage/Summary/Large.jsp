<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><cs:ftcs><render:logdep cid='<%=ics.GetVar("tid")%>' c="Template"/>
<render:calltemplate tname="Summary" args="c,cid,thumbnail-field" style="element" />
</cs:ftcs>