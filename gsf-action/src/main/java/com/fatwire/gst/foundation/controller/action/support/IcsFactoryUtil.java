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
package com.fatwire.gst.foundation.controller.action.support;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;
import com.fatwire.gst.foundation.controller.support.WebContextUtil;

/**
 * Helper class to access the ICS based services through the {@link Factory}.
 * 
 * @author Dolf Dijkstra
 * @since 28 August 2012 (for some history: <a href="http://en.wikipedia.org/wiki/Siege_of_Groningen">28 August</a>.)
 *
 */
public class IcsFactoryUtil {

    /**
     * Method to access the Factory.
     * 
     * @param ics the current ICS.
     * @return the Factory as found on the configured AppContext.
     */
    public static Factory getFactory(ICS ics) {
        final Object o = ics.GetObj(Factory.class.getName());
        if (o instanceof Factory) {
            return (Factory) o;
        }
        Factory factory = null;
        @SuppressWarnings("deprecation")
        AppContext ctx = WebContextUtil.getWebAppContext(ics.getIServlet().getServlet().getServletContext());

        if (ctx instanceof FactoryProducer) {
            FactoryProducer fp = (FactoryProducer) ctx;
            factory = fp.getFactory(ics);
            ics.SetObj(Factory.class.getName(), factory);
        }
        if (factory == null)
            throw new IllegalStateException(
                    "Could not find a FactoryProducer. Is the WebAppContext correctly configured. We found a "
                            + (ctx == null ? "null" : ctx.getClass().getName()) + " AppContext.");
        return factory;

    }
}
