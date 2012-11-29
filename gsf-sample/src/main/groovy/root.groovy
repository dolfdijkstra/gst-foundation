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

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.mobile.action.DeviceAwareRenderPageAction 

/*
 * Action that just extends the DeviceAwareRenderPageAction. TYhe DeviceAwareRenderPageAction also handles translation of the requested asset.
 * The action is defined in root.groovy. To make this the default action it is easiest to set a default argument action=root at the GSF/Dispatcher SiteCatalog entry.
 * 
 */

public class RootAction extends DeviceAwareRenderPageAction {

}

