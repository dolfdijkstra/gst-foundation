<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld"
%><%@ taglib prefix="commercecontext" uri="futuretense_cs/commercecontext.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="listobject" uri="futuretense_cs/listobject.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="searchstate" uri="futuretense_cs/searchstate.tld"
%><%@ taglib prefix="siteplan" uri="futuretense_cs/siteplan.tld"
%><%@ page import="COM.FutureTense.Interfaces.*"
%><%@ page import="com.fatwire.system.*"
%><%@ page import="com.fatwire.assetapi.data.*"
%><%@ page import="com.fatwire.assetapi.query.Query"
%><%@ page import="com.fatwire.assetapi.query.Condition"
%><%@ page import="com.fatwire.assetapi.query.ConditionFactory"
%><%@ page import="com.fatwire.assetapi.query.OpTypeEnum"
%><%@ page import="com.fatwire.assetapi.query.SimpleQuery"
%><%@ page import="java.util.*"
%><%@ page import="com.openmarket.xcelerate.asset.*"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/>
<%
Session ses = SessionFactory.getSession();
AssetDataManager mgr = (AssetDataManager) ses.getManager( AssetDataManager.class.getName() );
AssetId id = new AssetIdImpl( "AVIArticle", Long.valueOf( ics.GetVar("cid") ) );
Iterable<AssetData> list = mgr.read(Arrays.asList(id));
AssetData data = new AssetDataImpl(id);
List<AssetId> parents = data.getImmediateParents("Category");
AssetId parentId = null;
// we have the article category
if ( parents != null && !parents.isEmpty() ) {
	parentId =  parents.get(0);
	// now try to find a section page which is tagged with the same category
	Condition c = ConditionFactory.createCondition( "tag", OpTypeEnum.EQUALS, parentId.getId() );
	Query query = new SimpleQuery( "Page", "AVISection", c, Collections.singletonList( "title" ) );
	AttributeData attrData = null;
	boolean found = false;
	for ( AssetData assetData : mgr.read( query ) ) {
		found = true;
		attrData = assetData.getAttributeData("title");
		// we found our section page, build a hyperlink to the page
		%>
		<render:callelement elementname="avisports/Page/GetLink" scoped="global">
			<render:argument name="assetid" value='<%=String.valueOf(assetData.getAssetId().getId())%>' />
		</render:callelement>
		<a href="<ics:getvar name="pageUrl" />"><%=attrData.getData() %></a> <%
		// in case more there is more than 1 page tied to the parent category
		break;
	}
	if (!found) {
		// no section page tagged with the article category - link back to home
		%>
		<render:callelement elementname="avisports/Page/GetHomeLink" />
		<%
	}
}%></cs:ftcs>