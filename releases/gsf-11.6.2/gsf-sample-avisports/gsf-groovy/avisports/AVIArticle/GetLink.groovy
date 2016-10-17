package avisports.AVIArticle

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess;
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder

public class GetLink implements Action {

	@InjectForRequest
	public TemplateAssetAccess dao;

	@InjectForRequest
	public Model model;

	@Override
	public void handleRequest(ICS ics) {

		// build URL to the given article (articleId - fall back on cid if not provided)
		// return URL in a CS variable called "articleuUrl"

		String articleId = ics.GetVar("articleId")
		if(StringUtils.isBlank(articleId))	articleId= ics.GetVar("cid")
		AssetId id = dao.createAssetId("AVIArticle",articleId)
		TemplateAsset asset = dao.read(id, "template")
		String template = asset.asString("template");
		if(StringUtils.isNotBlank(template)){
			String uri=new TemplateUriBuilder(id,template).toURI(ics);
			model.add("articleUrl", StringEscapeUtils.escapeXml(uri));
		}


	}
}

