package com.fatwire.gst.foundation.facade.runtag.render;


public class CallElement extends TagRunnerWithArguments {

	public static final String SCOPE_GLOBAL="global";
	public static final String SCOPE_LOCAL="local";
	public static final String SCOPE_STACKED="stacked";
	
	protected CallElement() {
		super("RENDER.CALLELEMENT");
	}

	public void setElementName(final String element) {
		this.set("ELEMENTNAME", element);
	}

	public void setScope(final String scope) {
		this.set("SCOPE", scope);
	}
}
