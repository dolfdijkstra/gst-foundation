package avisports.AVIArticle

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils

import COM.FutureTense.Interfaces.ICS

import com.fatwire.assetapi.data.AssetId
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.facade.assetapi.asset.AssetMapAdapter
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder

public class Summary implements Action {

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
		TemplateAsset asset = dao.read(id, "template","headline","relatedImage","abstract","relatedStories")
		String template = asset.asString("template");
		if(StringUtils.isNotBlank(template)){
			String uri=new TemplateUriBuilder(id,template).toURI(ics);
			model.add("articleUrl", StringEscapeUtils.escapeXml(uri));
		}
		model.add("asset", new AssetMapAdapter(asset));


	}
}

