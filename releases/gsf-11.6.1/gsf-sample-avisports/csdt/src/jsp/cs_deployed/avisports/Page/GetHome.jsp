<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ taglib prefix="siteplan" uri="futuretense_cs/siteplan.tld"
%><%@ page import="COM.FutureTense.Interfaces.*"
%><%@ page import="com.fatwire.system.*"
%><%@ page import="com.fatwire.assetapi.data.*"
%><%@ page import="com.fatwire.assetapi.query.Query"
%><%@ page import="com.fatwire.assetapi.query.SimpleQuery"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/><%
//
// This element outputs a variable named AVIHomeId.
// It contains the id of the first placed Page asset of subtype AVIHome found in the site plan.
// This page will be assumed to be the home page.
//
Session ses = SessionFactory.getSession();
AssetDataManager mgr = (AssetDataManager) ses.getManager( AssetDataManager.class.getName() );
Query query = new SimpleQuery( "Page", "AVIHome" );
for ( AssetData pageData: mgr.read( query ) ) {
	boolean isPlaced = false;
	AttributeData attrData = pageData.getAttributeData("title");%>
	<asset:load name="currentPage" type="Page" objectid='<%=String.valueOf(pageData.getAssetId().getId())%>' />
	<asset:getsitenode name="currentPage" output="nodeId" />
	<siteplan:load name="node" nodeid='<%=ics.GetVar("nodeId") %>' />
	<siteplan:get name="node" field="ncode" output="ncode" /><%
	isPlaced = "Placed".equals(ics.GetVar("ncode"));
	// if this Page asset is placed, stop here - we found our home page
	if (isPlaced) {
		ics.SetVar("AVIHomeId", String.valueOf(pageData.getAssetId().getId() ) );
		break;
	}
}
%>
</cs:ftcs>