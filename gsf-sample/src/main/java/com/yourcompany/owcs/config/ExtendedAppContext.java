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

package com.yourcompany.owcs.config;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.support.SpringObjectFactory;
import com.fatwire.gst.foundation.groovy.context.GroovyWebContext;

/**
 * A sample on how to extend the service factory in GSF. This class is an
 * extension of the groovy app context, giving access to another ObjectFactory.
 * This class needs to be registered to the file META-INF/gsf-contexts. </p>
 * 
 * This class is also a show case on how services from Spring can be injected
 * 
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 * 
 */
public class ExtendedAppContext extends GroovyWebContext {
    private Factory spring;

    public ExtendedAppContext(ServletContext context, AppContext app) {
        super(context, app);
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        spring = new SpringObjectFactory(wac);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.support.DefaultWebAppContext
     * #getFactory(COM.FutureTense.Interfaces.ICS)
     */
    @Override
    public Factory getFactory(ICS ics) {
        /*
         * This FactoryProducer is stacked with the Groovy, Spring and
         * MyExtended. The order to find objects is first the MyExtended factory, then Groovy
         * and thus IcsBackedObjectFactoryTemplate  and at last spring.
         * 
         * The GroovyFactory makes it possible to create factory methods 
         * in /WEB-INF/gsf-groovy/gsf/ObjectFactory.groovy 
         * and in /WEB-INF/gsf-groovy/gsf/<mysite>/ObjectFactory.groovy
         * 
         * This allows for factories per site. <mysite> is replaced with ics.GetVar("site").
         * If gsf/<mysite>/ObjectFactory.groovy is not found or if that file does not contain the right 
         * producer method, fallback is to the generic gsf/ObjectFactory.groovy.
         * 
         */
        Factory root = super.getFactory(ics);
        return new MyExtendedObjectFactory(ics, root, spring);
    }

}
