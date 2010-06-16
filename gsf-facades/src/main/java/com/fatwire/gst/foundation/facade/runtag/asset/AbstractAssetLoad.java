package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

public abstract class AbstractAssetLoad extends AbstractTagRunner {

	public static final String DEPTYPE_EXACT = "exact";
	public static final String DEPTYPE_EXISTS = "exists";
	public static final String DEPTYPE_GREATER = "greater";
	public static final String DEPTYPE_NONE = "none";

	public static final String OPTION_EDITABLE = "editable";
	public static final String OPTION_READ_ONLY = "readonly";
	public static final String OPTION_READ_ONLY_COMPLETE = "readonly_complete";

	public AbstractAssetLoad() {
		super("asset.load");
	}

	public void setName(final String name) {
		this.set("NAME", name);
	}

	public void setAssetType(final String type) {
		this.set("TYPE", type);
	}

	public void setSite(final String site) {
		this.set("SITE", site);
	}

	public void setDepType(final String type) {
		this.set("DEPTYPE", type);
	}

	public void setEditable(final boolean value) {
		this.set("EDITABLE", value);
	}

	public void setOption(final String option) {
		this.set("OPTION", option);
	}

	public void setFlushOnVoid(final boolean flush) {
		this.set("FLUSHONVOID", flush);
	}

}