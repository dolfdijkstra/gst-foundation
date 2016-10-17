package ArticleCategory

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData
import com.fatwire.assetapi.data.AssetId
import com.fatwire.assetapi.data.AttributeData
import com.fatwire.assetapi.query.Condition
import com.fatwire.assetapi.query.ConditionFactory
import com.fatwire.assetapi.query.OpTypeEnum
import com.fatwire.assetapi.query.Query
import com.fatwire.assetapi.query.SimpleQuery
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder

public class Link implements Action {

	static Log LOG = LogFactory.getLog("ArticleCategory/Link");

	@InjectForRequest
	public TemplateAssetAccess dao;

	@InjectForRequest
	public Model model;


	@Override
	public void handleRequest(ICS ics) {


		Condition c = ConditionFactory.createCondition( "tag", OpTypeEnum.EQUALS, ics.GetVar("cid") );
		Query query = new SimpleQuery("Page", "AVISection" , c, ["title","template"] );
		
		boolean found = false;
		
		for ( TemplateAsset asset : dao.query( query) ) {
			found = true;
			String title = asset.asString("title");
			model.add("title", title);
			AssetId id = asset.getAssetId()
			String template = asset.asString("template");
			if(StringUtils.isNotBlank(template)){
				String uri=new TemplateUriBuilder(id,template).toURI(ics);
				model.add("pageUrl", StringEscapeUtils.escapeXml(uri));
			}

			// in case more there is more than 1 page tied to the parent category
			break;
		}
		if (!found) {
			query = new SimpleQuery( "Page", "AVIHome" ,null,["title","template"]);
			for ( TemplateAsset asset : dao.query( query) ) {
				String title = asset.asString("title");
				model.add("title", title);
				AssetId id = asset.getAssetId()
				String template = asset.asString("template");
				if(StringUtils.isNotBlank(template)){
					String uri=new TemplateUriBuilder(id,template).toURI(ics);
					model.add("pageUrl", StringEscapeUtils.escapeXml(uri));
				}
	
			}
		}
	}
}
