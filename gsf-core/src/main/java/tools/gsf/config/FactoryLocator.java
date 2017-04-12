/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.config;

import COM.FutureTense.Interfaces.ICS;

import javax.servlet.ServletContext;

/**
 * Utility class for working with the factory producer and factories.
 *
 * @author Tony Field
 * @since 2016-08-06
 */
public final class FactoryLocator {
    private FactoryLocator() {
    }

    /**
     * Convenience method for locating the factory producer that resides in the servlet context.
     * Never returns null.
     *
     * @param servletContext the servlet context for this application
     * @return the factory producer, never null
     */
    public static FactoryProducer locateFactoryProducer(ServletContext servletContext) {
        Object o = servletContext.getAttribute(ServletContextLoader.FACTORY_PRODUCER);
        if (o instanceof FactoryProducer) {
            return (FactoryProducer) o;
        } else {
            throw new IllegalStateException("No factory producer found");
        }
    }

    /**
     * Convenience method for locating the factory producer that resides in the servlet context.
     * Never returns null.
     *
     * @param ics the ics object (which of course holds a pointer to the servlet context)
     * @return the factory producer, never null
     */
    public static FactoryProducer locateFactoryProducer(ICS ics) {
        ServletContext servletContext = ics.getIServlet().getServlet().getServletContext();
        return locateFactoryProducer(servletContext);
    }

    /**
     * Convenience method to locate the factory for the ICS scope.
     *
     * @param ics ics context
     * @return the factory for the ics scope. Never null
     */
    public static Factory locateFactory(ICS ics) {
        return locateFactoryProducer(ics).getFactory(ics);
    }
}
