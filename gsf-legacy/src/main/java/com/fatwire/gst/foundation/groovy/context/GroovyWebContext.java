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
package com.fatwire.gst.foundation.groovy.context;

import groovy.lang.GroovyClassLoader;

import javax.servlet.ServletContext;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.action.support.ClassActionLocator;
import com.fatwire.gst.foundation.controller.action.support.LegacyDefaultWebAppContext;
import com.fatwire.gst.foundation.controller.action.support.RenderPageActionLocator;
import com.fatwire.gst.foundation.groovy.GroovyElementCatalogLoader;
import com.fatwire.gst.foundation.groovy.GroovyLoader;
import com.fatwire.gst.foundation.groovy.action.GroovyActionLocator;

/**
 * WebAppContext that is using Groovy to load Actions.
 * 
 * @author Dolf Dijkstra
 * @since 11 mei 2012
 * 
 * @deprecated as of release 12.x, replace with WCS 12c's native Groovy support
 */
public class GroovyWebContext extends LegacyDefaultWebAppContext {
    private GroovyClassLoader classLoader;

    /**
     * This constructor was needed for the WebAppContextLoader.
     * 
     * @param context servlet context
     * @param app application context
     */
    public GroovyWebContext(ServletContext context, AppContext app) {
        super(context, app);
        classLoader = new GroovyClassLoader();
        String path = getServletContext().getRealPath("/WEB-INF/gsf-groovy");
        classLoader.addClasspath(path);

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.foundation.controller.action.support.LegacyDefaultWebAppContext#createActionLocator()
     */
    public ActionLocator createActionLocator() {
        // this method is expected to be called only once during the lifecycle
        // of the web app context, though more often does not need to be a
        // problem per se.

        // set up a chain of action locators
        // at the root level (if everything else fails), return a ActionLocator
        // that returns a RenderPage
        Injector injector = getBean("injector", Injector.class);
        ActionLocator root = getRootActionLocator(injector);

        GroovyLoader loader = getGroovyLoader();
        // next, set the groovy action loader
        GroovyActionLocator groovyLocator = new GroovyActionLocator(root, injector);
        groovyLocator.setGroovyLoader(loader);
        // and at last the class:<classname> loader
        final ClassActionLocator cal = new ClassActionLocator(groovyLocator, injector);
        return cal;

    }

    /**
     * @return GroovyLoader that looks at elementcatalog and /WEB-INF/gsf-groovy for groovy classes.
     */
    protected GroovyLoader getGroovyLoader() {
        return new GroovyElementCatalogLoader(getServletContext());
    }

    protected ActionLocator getRootActionLocator(Injector injector) {
        return new RenderPageActionLocator(injector);
    }

    @Override
    public Factory getFactory(ICS ics) {
        Factory base = super.getFactory(ics);
        GroovyFactory fg = new GroovyFactory(ics, getClassLoader(), base);
        return fg;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

}
