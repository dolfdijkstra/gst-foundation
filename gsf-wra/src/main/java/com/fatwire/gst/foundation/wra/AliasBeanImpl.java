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
package com.fatwire.gst.foundation.wra;

import com.fatwire.assetapi.data.AssetId;

/**
 * Simple Alias bean
 *
 * @author Larissa Kowaliw
 * @since Jul 27, 2010
 */
public class AliasBeanImpl extends WraBeanImpl implements Alias {

	private AssetId target;
	private String targetUrl;
	private String popup;
	private AssetId linkImage;

	public AssetId getLinkImage() {
		return linkImage;
	}

	public String getPopup() {
		return popup;
	}

	public AssetId getTarget() {
		return target;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTarget(AssetId target) {
		this.target = target;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public void setPopup(String popup) {
		this.popup = popup;
	}

	public void setLinkImage(AssetId linkImage) {
		this.linkImage = linkImage;
	}
}
