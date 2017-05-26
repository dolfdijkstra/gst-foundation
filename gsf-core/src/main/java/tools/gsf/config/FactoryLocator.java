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
import COM.FutureTense.Interfaces.IServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

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
        Object o = servletContext.getAttribute(ServletContextLoader.GSF_FACTORY_PRODUCER);
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
     * @param ics the ics object (which, should of course, hold a pointer to the servlet context). If the
     *            specified ICS object does NOT hold a pointer to the servlet context, this locator method
     *            will fall back to the <code>DefaultFactoryProducer</code> class, as there is no defined
     *            configuration capability for creating custom factory producers without a servlet context.
     * @return the factory producer, never null
     */
    public static FactoryProducer locateFactoryProducer(ICS ics) {
        if (ics == null) throw new IllegalArgumentException("No ICS found - cannot locate factory without a scope");

        ServletContext servletContext;
        try {
            servletContext = ics.getIServlet().getServlet().getServletContext();
        } catch (RuntimeException e) {
            servletContext = null;
        }

        if (servletContext == null) {
            Object o = ics.GetObj(ServletContextLoader.GSF_FACTORY_PRODUCER);
            if (o == null) {
                // There is no defined configuration capability for a factory producer that does not involve
                // the servlet context. Rather than fail, return the default factory producer instead.
                o = new DefaultFactoryProducer();
                ics.SetObj(ServletContextLoader.GSF_FACTORY_PRODUCER, o);
            }
            return (FactoryProducer)o;
        } else {
            return locateFactoryProducer(servletContext);
        }
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
