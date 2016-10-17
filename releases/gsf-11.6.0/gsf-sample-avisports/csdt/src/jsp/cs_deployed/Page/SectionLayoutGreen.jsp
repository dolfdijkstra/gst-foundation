<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><cs:ftcs><render:logdep c="Template" cid='<%=ics.GetVar("tid") %>' /><%
%><render:calltemplate tname="SectionLayout" args="c,cid,bg-color" style="element" /></cs:ftcs>