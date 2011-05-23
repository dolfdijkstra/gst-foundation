package page;

import com.fatwire.gst.foundation.include.IncludeService;

import COM.FutureTense.Interfaces.ICS

import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.controller.annotation.Mapping
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style
import com.fatwire.gst.foundation.include.IncludeService
import com.fatwire.gst.foundation.mapping.AssetName

class GTTopNav implements Action {

    @InjectForRequest public IncludeService includeService;

    @Mapping("TopNavJavaScript")  public AssetName  TopNavJavaScriptVar;
    @Mapping("TopNav") public AssetName TopNav;
    @Mapping("LocaleForm")  public AssetName LocaleForm;

    public void handleRequest(ICS ics){
        includeService.element("TopNavJavaScript",TopNavJavaScriptVar.getName()).argument("cid", ics.GetVar("cid"))
        includeService.page("TopNav",TopNav.getName(),Style.pagelet).argument("sitepfx", ics.GetVar("sitepfx")).argument("site", ics.GetVar("site")).argument("locale" ,ics.GetVar("locale"))
        includeService.page("LocaleForm",LocaleForm.getName(),Style.pagelet).argument("sitepfx", ics.GetVar("sitepfx")).argument("site", ics.GetVar("site")).argument("locale" ,ics.GetVar("locale"))
    }
}


