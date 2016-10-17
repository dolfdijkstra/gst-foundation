<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="string" uri="futuretense_cs/string.tld"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/>

<ics:removevar name="articleUrl" />
<%
// build URL to the given article (articleId - fall back on cid if not provided)
// return URL in a CS variable called "articleuUrl"
%>
<ics:ifempty variable="articleId">
<ics:then>
	<ics:setvar name="articleId" value='<%=ics.GetVar("cid") %>' />
</ics:then>
</ics:ifempty>

<asset:list type="AVIArticle" field1="id" value1='<%=ics.GetVar("articleId")%>' list="article" />
<ics:listget listname="article" fieldname="template" output="template" />

<ics:ifnotempty variable="template">
<ics:then>
<render:gettemplateurl	tname='<%= ics.GetVar("template")%>' c="AVIArticle" cid='<%=ics.GetVar("articleId") %>'
						ttype="CSElement"
						outstr="_articleUrl" />
<string:encode varname="articleUrl" variable="_articleUrl" />
</ics:then>
</ics:ifnotempty>
</cs:ftcs>