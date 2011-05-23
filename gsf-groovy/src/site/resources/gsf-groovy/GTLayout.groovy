import java.text.*

import COM.FutureTense.Interfaces.ICS
import COM.FutureTense.Util.ftMessage

import com.fatwire.assetapi.data.AssetId
import com.fatwire.gst.foundation.controller.action.*
import com.fatwire.gst.foundation.controller.annotation.*
import com.fatwire.gst.foundation.facade.assetapi.asset.*
import com.fatwire.gst.foundation.html.Img
import com.fatwire.gst.foundation.include.*
import com.fatwire.gst.foundation.mapping.*

class GTLayout implements Action {

	@InjectForRequest public IncludeService includeService;
	@InjectForRequest public ScatteredAssetAccessTemplate assetDao;
	@InjectForRequest public ICS ics;
    @InjectForRequest public Model model;

	@Mapping("BannerList") public AssetId bannerList;
	@Mapping("StyleSheetReco") public AssetId stylesheetId;
	@Mapping("BannerTemplate") public String bannerTemplate
	@Mapping("BottomNav") public String bottomNav
	@Mapping("Detail") public String detail
	@Mapping("Filter") public AssetName filter
	@Mapping("Head") public String head
	@Mapping("SideNav") public String sideNav
	@Mapping("StyleSheetResolver") public String styleSheetResolver
	@Mapping("TopNav") public String topNav

	public void handleRequest(ICS ics){

		//ics.StreamText("<h1>groovy was here: mapping "+filter+"</h1>")

		/* Execute the Dimension filter to look up the translated asset that
		 * corresponds to the locale that the visitor requested. 
		 */

		//global call, no special arguments: take the callelement short route
        includeService.element ("Filter", filter.getName()).include ics

		doPreviewSpecialStuff()

		AssetId pageId = assetDao.createAssetId("Page",ics.GetVar("p"))

		includeService.template("StyleSheetSlot", stylesheetId ,styleSheetResolver).element()

		includeService.template("Head",assetDao.currentId(),head); // p and locale are copied as part of pagecriteria

		includeService.template("TopNav", pageId,topNav)

		includeService.template("BannerSlot", bannerList,bannerTemplate).element()

		includeService.template("SideNav", assetDao.currentId(),sideNav).element()

		includeService.template("Detail", assetDao.currentId(),detail).pagelet()

		includeService.template("BottomNav", pageId,bottomNav).embedded()
        Img img = new Img();
        img.setSrc (ics.GetProperty("ft.cgipath") + ics.GetVar("site") +"/images/PoweredByFatWire.gif") 
        img.setAlt ("Powered by FatWire Software")

        model.add("PoweredBy",img)

	}
	def doPreviewSpecialStuff(){
		if (ics.LoadProperty("futuretense.ini;futuretense_xcel.ini"))  {
			/*
			 Disable caching if site preview is enabled. This disabling is required at the layout template
			 because, if the layout gets cached, subsequent requests for child pages will not be executed.
			 The cached layout page will always be returned.
			 */
			if(ics.GetProperty("cs.sitepreview").equals(ftMessage.cm)){
				ics.DisableFragmentCache();
			}

			/*
			 If a date value is available at this point, set it in the session so that it is available for
			 subsequent requests. For eg. when the user previews an October version of the site,
			 we need to preserve the date when he navigates to the Products, Shopping cart or other
			 sections of the site. This date value is required for properly loading the appropriate style sheets
			 that are required for rendering the page.
			 */
			if(ics.GetVar("__insiteDate")!= null) {
				ics.SetSSVar("__insiteDate",ics.GetVar("__insiteDate"));
			}
		}
	}
}
