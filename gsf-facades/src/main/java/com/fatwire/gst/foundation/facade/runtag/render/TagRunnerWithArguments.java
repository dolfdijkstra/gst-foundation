package com.fatwire.gst.foundation.facade.runtag.render;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

public abstract class TagRunnerWithArguments extends AbstractTagRunner {

	public TagRunnerWithArguments(final String tagName) {
		super(tagName);
	}

	public void setArgument(final String name, final String value) {
		this.set(name, value);
	}

}