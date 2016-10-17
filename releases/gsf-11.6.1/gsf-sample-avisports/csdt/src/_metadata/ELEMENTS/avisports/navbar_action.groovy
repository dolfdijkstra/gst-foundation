import COM.FutureTense.Interfaces.ICS

import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.navigation.NavigationNode
import com.fatwire.gst.foundation.navigation.NavigationService


public class NavBar implements Action {

	@InjectForRequest
	public Model model;

	@InjectForRequest
	public NavigationService navigation;


	@Override
	public void handleRequest(ICS ics) {
		NavigationNode node = navigation.getNodeByName(ics.GetVar("site"),"Home",1, "title")
		model.add("navNode", node)

	}
	
}

