import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.mobile.action.DeviceAwareRenderPageAction 

/*
 * Just extended the DeviceAwareRenderPageAction. That action also handles translation of the requested asset.
 * The action is defined in root.groovy. To make this the default action it is easiest to set a default argument action=root at the GSF/Dispatcher SiteCatalog entry.
 * 
 */

public class RootAction extends DeviceAwareRenderPageAction {

}

