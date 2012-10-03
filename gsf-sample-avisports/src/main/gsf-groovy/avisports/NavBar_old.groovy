package avisports

import COM.FutureTense.Interfaces.ICS
import COM.FutureTense.Interfaces.IList

import com.fatwire.assetapi.query.SimpleQuery
import com.fatwire.gst.foundation.CSRuntimeException
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.facade.runtag.asset.AssetLoadById
import com.fatwire.gst.foundation.facade.runtag.asset.GetSiteNode
import com.fatwire.gst.foundation.facade.runtag.siteplan.ListPages
import com.fatwire.gst.foundation.facade.runtag.siteplan.SitePlanLoad
import com.fatwire.gst.foundation.navigation.*

public class NavBar implements Action {

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
		Collection<NavigationNode> nodes = navigation.getNodeByName(ics.GetVar("site"), "AVIHome",1, "title")
		
		model.add("navNodes", nodes)

	}
	public String getNode(ICS ics, long p) {
		//select oid from SitePlanTree where oid in (SELECT id from Page where subtype='AVIHome') and ncode='Placed'

		final String LOADED_PAGE_NAME = "__thePage";
		final String LOADED_SITE_PLAN_NODE = "__siteplan";
		final String CURRENT_PAGE_NODE_ID = "__nodeId";
		final String PLACED_LIST = "__placedList";

		try {
			AssetLoadById assetLoad = new AssetLoadById();
			assetLoad.setAssetId(p);
			assetLoad.setAssetType("Page");
			assetLoad.setName(LOADED_PAGE_NAME);
			assetLoad.execute(ics);
			if (ics.GetErrno() < 0) {
				throw new CSRuntimeException("Failed to load page identified by Page:" + p, ics.GetErrno());
			}

			GetSiteNode getSiteNode = new GetSiteNode();
			getSiteNode.setName(LOADED_PAGE_NAME);
			getSiteNode.setOutput(CURRENT_PAGE_NODE_ID);
			getSiteNode.execute(ics);
			if (ics.GetErrno() < 0) {
				throw new CSRuntimeException("Could not get site node for page identified by Page:" + p, ics.GetErrno());
			}

			SitePlanLoad sitePlanLoad = new SitePlanLoad();
			sitePlanLoad.setName(LOADED_SITE_PLAN_NODE);
			sitePlanLoad.setNodeid(ics.GetVar(CURRENT_PAGE_NODE_ID));
			sitePlanLoad.execute(ics);
			if (ics.GetErrno() < 0) {
				throw new CSRuntimeException("Could not load site plan tree for page identified by Page:" + p,
				ics.GetErrno());
			}

			ListPages listPages = new ListPages();
			listPages.setName(LOADED_SITE_PLAN_NODE);
			listPages.setPlacedList(PLACED_LIST);
			listPages.setLevel(1);
			listPages.execute(ics);
			if (ics.GetErrno() < 0) {
				throw new CSRuntimeException("Could not get list child pages for Page:" + p, ics.GetErrno());
			}

			// otype/oid are what we care about
			IList placedList = ics.GetList(PLACED_LIST);
			if (ics.GetErrno() < 0 || placedList == null || !placedList.hasData()) {
				ics.ClearErrno();
				return null;
			}
			return placedList.getValue("ncode");
		} finally {
			ics.SetObj(LOADED_PAGE_NAME, null); // just to be safe
			ics.RemoveVar(CURRENT_PAGE_NODE_ID);
			ics.SetObj(LOADED_SITE_PLAN_NODE, null);
			ics.RegisterList(PLACED_LIST, null);
		}
	}
}

