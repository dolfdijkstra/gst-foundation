/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.mobile.mobiforce;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fatwire.gst.foundation.mobile.DeviceType;
import com.fatwire.gst.foundation.mobile.mobiforce.MobiForceDeviceDetector;

public class MobiForceDeviceDetectorTest {

	@Test
	public void testDetectDeviceTypeString_iphone() {
		String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";

		DeviceType type = new MobiForceDeviceDetector().detectDeviceType(ua);
		assertEquals(DeviceType.MOBILE, type);

	}

	@Test
	public void testDetectDeviceTypeString_ipad() {
		String ua = "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10";

		DeviceType type = new MobiForceDeviceDetector().detectDeviceType(ua);
		assertEquals(DeviceType.TABLET, type);

	}

	@Test
	public void testDetectDeviceTypeString_desktop() {
		String ua = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:13.0) Gecko/20100101 Firefox/13.0.1";

		DeviceType type = new MobiForceDeviceDetector().detectDeviceType(ua);
		assertEquals(DeviceType.DESKTOP, type);

	}

}
