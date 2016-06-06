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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @deprecated ICS usage and access should be managed by the calling classes.  Specifically, creating a new ICS
 * object should be avoided.
 */
@Deprecated
public final class ICSFactory {

	protected static final Logger LOG = LoggerFactory.getLogger("com.function1.gsf.foundation.facade.ics.ICSFactory");

    /**
     * Create a new instance of ICS. Expensive operation. Should be used
     * sparingly. TODO: low priority: Document lifecycle restrictions
     *
     * @deprecated Creates a new un-backed ICS instance, which can very easily be mistaken for a regular ICS object
     * @return ICS instance, not backed by servlet.
     */
    @Deprecated
    public static ICS newICS() {
        try {
            LOG.debug("Creating new ICS object");
            ICS ics = Factory.newCS();
            LOG.warn("A new ICS object has just been created.  This activity is deprecated.", new Exception());
            return ics;
        } catch (Exception e) {
            throw new RuntimeException("Could not create new ICS instance: " + e, e);
        }
    }

    /**
     * Returns the ICS object from current IPSRegistry or creates a new one if
     * non is found on the IPSRegistry.
     *
     * @deprecated returns an ICS object using an undocumented API, and creates a new one that is only partially-formed
     * if none is found.  Misuse of this ICS object can result in very tricky bugs.
     * @return ICS object
     */
    @Deprecated
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
