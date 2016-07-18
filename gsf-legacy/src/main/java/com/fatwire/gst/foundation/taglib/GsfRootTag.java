/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.gst.foundation.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;


/**
 * 
 * @deprecated as of release 12.x
 *
 */
public class GsfRootTag extends BodyTagSupport {
	public static final String ICS_VARIABLE_NAME = "ics";
	public static final String VARIABLE_SCOPE_NAME = "cs";
	public static final String ASSET_DAO = "assetDao";
	/**
	 * 
	 */
	private static final long serialVersionUID = -5369419132504852400L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see COM.FutureTense.JspTags.Root#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();

		final ICS ics = getICS();

		if (ics != null) {
			pageContext.setAttribute(VARIABLE_SCOPE_NAME, new ICSAsMap(ics),
					PageContext.PAGE_SCOPE);
			final ScatteredAssetAccessTemplate assetTemplate = new ScatteredAssetAccessTemplate(
					ics);
			pageContext.setAttribute(ASSET_DAO, assetTemplate,
					PageContext.PAGE_SCOPE);
			if (ics.GetVar("tid") != null) {
				LogDep.logDep(ics, "Template", ics.GetVar("tid"));
			}
			if (ics.GetVar("seid") != null) {
				LogDep.logDep(ics, "SiteEntry", ics.GetVar("seid"));
			}
			if (ics.GetVar("eid") != null) {
				LogDep.logDep(ics, "CSElement", ics.GetVar("eid"));
			}
		} else {
			throw new JspException(
					"ics is not found on the page context. This tags needs to be nested in the <cs:ftcs> tag.");
		}
		return EVAL_BODY_INCLUDE;
	}

	protected ICS getICS() {
		final Object o = pageContext.getAttribute(ICS_VARIABLE_NAME,
				PageContext.PAGE_SCOPE);
		if (o instanceof ICS) {
			return (ICS) o;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		pageContext
				.removeAttribute(VARIABLE_SCOPE_NAME, PageContext.PAGE_SCOPE);
		pageContext.removeAttribute(ASSET_DAO, PageContext.PAGE_SCOPE);
		return Tag.EVAL_PAGE;
	}

}
