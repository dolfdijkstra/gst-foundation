/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller.action;

import COM.FutureTense.Interfaces.ICS;

/**
 * A factory for a Factory to provide access to services that need access to ICS.
 * <p>
 *
 * @author Dolf.Dijkstra
 * @deprecated see {@link tools.gsf.config.FactoryProducer}
 */
public interface FactoryProducer {

    /**
     * Method to produce a {@link tools.gsf.config.Factory} to access services that need access to ICS
     *
     * @param ics Content Server context object
     * @return the Factory to create services that need access to ics.
     */
    Factory getFactory(final ICS ics);
}
