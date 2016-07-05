package com.fatwire.gst.foundation.controller;

import javax.servlet.ServletContext;

import com.fatwire.assetapi.data.BaseController;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.support.WebContextUtil;

import COM.FutureTense.Interfaces.DependenciesAwareModelAndView;
import COM.FutureTense.Interfaces.ICS;

public class InjectingController extends BaseController {
	
	public InjectingController(ICS ics)
	{
		super();
		this.setICS(ics);
		
	}

	public DependenciesAwareModelAndView handleRequest()
	{
		
		ServletContext srvCtx = ics.getIServlet().getServlet().getServletContext();
		AppContext ctx = WebContextUtil.getWebAppContext(srvCtx);
		Injector injector = ctx.getBean("Injector",Injector.class); 
		injector.inject(ics, this);		
		
		return super.handleRequest();
	}
	
}
