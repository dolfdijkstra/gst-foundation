package avisports.AVIArticle

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils

import COM.FutureTense.Interfaces.ICS
import COM.FutureTense.Interfaces.IList

import com.fatwire.assetapi.query.SimpleQuery
import com.fatwire.gst.foundation.CSRuntimeException
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadById
import com.fatwire.gst.foundation.facade.runtag.asset.GetSiteNode
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages
import com.fatwire.gst.foundation.facade.runtag.siteplan.SitePlanLoad
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder


import com.fatwire.gst.foundation.navigation.*

public class GetHome implements Action {

	@InjectForRequest
	public TemplateAssetAccess dao;

	@InjectForRequest
	public Model model;

	@InjectForRequest
	public NavigationService navigation;


	@Override
	public void handleRequest(ICS ics) {
		// AVIHome subtype pages for all sites??
		SimpleQuery query = new SimpleQuery( "Page", "AVIHome" ,null,["title","template"]);
		Collection<NavigationNode> nodes = navigation.getNodeByName(ics.GetVar("site"), "AVIHome",1)
			

		for ( TemplateAsset page: dao.query(query) ) {
			boolean isPlaced = false;
			String title= page.asString("title");
			model.add("AVIHomeId", page.getAssetId().getId() );
			model.add("title", title);
			String template = page.asString("template");
			if(StringUtils.isNotBlank(template)){
				String uri=new TemplateUriBuilder(page.getAssetId(),template).toURI(ics);
				model.add("pageUrl", StringEscapeUtils.escapeXml(uri));
			}

			break;
		}

	}
}



