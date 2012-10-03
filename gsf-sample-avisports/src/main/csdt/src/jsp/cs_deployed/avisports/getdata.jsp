<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><%@ taglib prefix="asset" uri="futuretense_cs/asset.tld"
%><%@ taglib prefix="assetset" uri="futuretense_cs/assetset.tld"
%><%@ taglib prefix="ics" uri="futuretense_cs/ics.tld"
%><%@ taglib prefix="render" uri="futuretense_cs/render.tld"
%><%@ page import="	com.fatwire.assetapi.data.*,
                   com.fatwire.assetapi.*,
                   com.fatwire.system.*,
                   com.openmarket.xcelerate.asset.*,
                   java.util.*,
                   org.apache.commons.lang.*"
%><cs:ftcs>
<render:logdep cid='<%=ics.GetVar("eid")%>' c="CSElement"/><%
// A Utility element which scatters asset data in the request scope
// for consumption by EL
Session ses = SessionFactory.getSession(ics);
AssetDataManager mgr = (AssetDataManager)ses.getManager(AssetDataManager.class.getName());
String type = ics.GetVar("type");
String id = ics.GetVar("id");
id = (StringUtils.isEmpty(id) ? ics.GetVar("cid") : id);
type = (StringUtils.isEmpty(type) ? ics.GetVar("c") : type);
String prefix = ics.GetVar("prefix");
String attributes = ics.GetVar("attributes");
String parentPrefix = "Group_";
if (StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(attributes)) {
	AssetId assetId = new AssetIdImpl( type, Long.valueOf(id));
	String[] attributeNames = StringUtils.split(attributes, ",");
	prefix = StringUtils.isNotEmpty(prefix) ? prefix : "asset";
	List<String> attributeList = new ArrayList<String>(); //Arrays.asList(attributeNames);
	for (String name: attributeNames) {
		if (!name.startsWith(parentPrefix)) attributeList.add(name);
	}
	AssetData data = mgr.readAttributes(assetId, attributeList);
	Map map = new HashMap();
	for (String name: attributeNames) {
		if (name.startsWith(parentPrefix)) {
			List<AssetId> parents = data.getImmediateParents(name.substring(parentPrefix.length()));
			if (parents == null && parents.size() == 0) {
				map.put(name, Collections.emptyList());
			}
			else {
				map.put(name, (parents != null && parents.size() == 1 ? parents.get(0) : parents));
			}
		}
		else {
			AttributeData attributeData = data.getAttributeData(name);
			if (attributeData != null) {
				map.put(name, attributeData.getData());
			}
		}

	}
	request.setAttribute(prefix, map);
}
else {
	request.removeAttribute(prefix);
}
%></cs:ftcs>