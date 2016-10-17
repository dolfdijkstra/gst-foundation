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

package gsf.avisports

import COM.FutureTense.Interfaces.ICS

import com.fatwire.gst.foundation.controller.action.Factory
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.navigation.NavigationService
import com.fatwire.gst.foundation.navigation.support.SimpleNavigationHelper

public class ObjectFactory {

  @ServiceProducer(cache = false)
  static NavigationService createNavigationService(ICS ics, Factory f){
	  TemplateAssetAccess taa = f.getObject("templateAssetAccess", TemplateAssetAccess.class);
		  return new SimpleNavigationHelper(ics, taa, "title", "path");
  }
}
