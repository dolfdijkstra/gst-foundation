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

package gsf

import COM.FutureTense.Interfaces.ICS

import com.fatwire.gst.foundation.controller.action.Factory
import com.fatwire.gst.foundation.mobile.DeviceDetector
import com.fatwire.gst.foundation.mobile.mobiforce.MobiForceDeviceDetector
import com.fatwire.gst.foundation.controller.annotation.ServiceProducer

/**
 * This is a helper for the Factory. The idea is that based on a naming convention object are produced.
  *
 * 
 * <p/>
 * To find producer methods to the following
 * rules are used:
 * <ul>
 * <li>public <b>static</b> Foo createFoo(ICS ics, Factory factory){}</li>
 * <li>public Foo createFoo(ICS ics){}</li>
 * </ul>
 * If the non-static version is used the implementing class needs to have a
 * public constructor that takes {@see ICS} and {@see Factory} as arguments.
 *
*/


public class ObjectFactory {

  @ServiceProducer(cache = true)
  static DeviceDetector createDeviceDetector(ICS ics, Factory f){
     return new MobiForceDeviceDetector()
  }
}
