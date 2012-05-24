/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.ics;

import COM.FutureTense.CS.Factory;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.Servlet.IPSRegistry;

/**
 * Factory class for creating new ICS instances. This process is very expensive
 * so use it sparingly.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jul 29, 2010
 */
public final class ICSFactory {

    /**
     * Create a new instance of ICS. Expensive operation. Should be used
     * sparingly. TODO: low priority: Document lifecycle restrictions
     * 
     * @return ICS instance, not backed by servlet.
     */
    public static ICS newICS() {
        try {
            return Factory.newCS();
        } catch (Exception e) {
            throw new RuntimeException("Could not create new ICS instance: " + e, e);
        }
    }

    /**
     * Returns the ICS object from current IPSRegistry or creates a new one if
     * non is found on the IPSRegistry.
     * 
     * @return ICS object
     */
    public static ICS getOrCreateICS() {
        ICS ics = null;

        IPS ips = IPSRegistry.getInstance().get();
        ics = (ips != null) ? ips.GetICSObject() : null;

        if (ics == null) {
            ics = newICS();
        }
        return ics;
    }
}
