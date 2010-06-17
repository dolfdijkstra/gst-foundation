/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.render;

import com.fatwire.assetapi.data.AssetId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CallTemplate tag
 * <p/><tt>
 * &lt;RENDER.CALLTEMPLATE SITE="site name" SLOTNAME="name of slot"
 * TID="caller Template or CSElement id" [TTYPE="caller Template or CSElement"]
 * [C="asset type"] [CID="asset id"] [TNAME="target Template or CSElement name"]
 * [CONTEXT="context override"] [STYLE="pagelet or element"]
 * [VARIANT="template variant name"] [PACKEDARGS="packed arguments"]&gt;
 * <p/>
 * [&lt;RENDER.ARGUMENT NAME="variable1" VALUE="value1"/&gt;]
 * <p/>
 * &lt;/RENDER.CALLTEMPLATE&gt;
 * </tt>
 * @author Tony Field
 * @since Jun 10, 2010
 */
public class CallTemplate extends TagRunnerWithArguments {
	private static Log LOG = LogFactory.getLog(GetTemplateUrl.class);

	public enum Style {
		element, pagelet, embedded
	}

	public enum Type {
		Template, CSElement
	}

	public CallTemplate() {
		super("RENDER.CALLTEMPLATE");
	}

	
	/**
	 * Sets up CallTemplate with default <tt>Style.element</tt>
	 * 
	 * @param slotname
	 * @param tname
	 * @param type
	 */
	public CallTemplate(String slotname, String tname, Type type) {
		super("RENDER.CALLTEMPLATE");
		setSlotname(slotname);
		setTname(tname);
		setTtype(type);
		setStyle(Style.element);
		setContext("");

	}

	public void setSite(String s) {
		set("SITE", s);
	}

	public void setSlotname(String s) {
		set("SLOTNAME", s);
	}

	public void setTid(String s) {
		set("TID", s);
	}

	public void setTtype(Type s) {
		set("TTYPE", s.toString());
	}

	public void setC(String s) {
		set("C", s);
	}

	public void setCid(String s) {
		set("CID", s);
	}

	public void setTname(String s) {
		set("TNAME", s);
	}

	public void setContext(String s) {
		set("CONTEXT", s);
	}

	public void setStyle(Style s) {
		set("STYLE", s.toString());
	}

	public void setVariant(String s) {
		set("VARIANT", s);
	}

	public void setPackedargs(String s) {
		set("PACKEDARGS", s);
	}

	public void setAsset(AssetId id) {
		setC(id.getType());
		setCid(Long.toString(id.getId()));
	}

}
