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

import javax.servlet.ServletContext;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.action.support.ClassActionLocator;
import com.fatwire.gst.foundation.controller.action.support.DefaultWebAppContext;
import com.fatwire.gst.foundation.controller.action.support.RenderPageActionLocator;
import com.fatwire.gst.foundation.controller.support.WebAppContextLoader;
import com.fatwire.gst.foundation.groovy.spring.GroovyActionLocator;
import com.fatwire.gst.foundation.groovy.spring.GroovyLoader;

/**
 * WebAppContext that is using Groovy to load Actions.
 * 
 * @author Dolf Dijkstra
 * @since 11 mei 2012
 * 
 */
public class GroovyWebContext extends DefaultWebAppContext {
    /**
     * This constructor is needed for the {@link WebAppContextLoader}.
     * 
     * @param context
     * @param app
     */
    public GroovyWebContext(ServletContext context, AppContext app) {
        super(context, app);
    }

    public ActionLocator createActionLocator() {
        // this method is expected to be called only once during the lifecycle
        // of the web app context, though more often does not need to be a
        // problem per se.

        // set up a chain of action locators
        // at the root level (if everything else fails), return a ActionLocator
        // that returns a RenderPage
        Injector injector = getBean("injector", Injector.class);
        ActionLocator root = getRootActionLocator(injector);

        GroovyLoader loader = new GroovyLoader(getServletContext());
        // next, set the groovy action loader
        GroovyActionLocator l = new GroovyActionLocator(root, injector);
        l.setGroovyLoader(loader);
        // and at last the class:<classname> loader
        final ClassActionLocator cal = new ClassActionLocator(l, injector);
        return cal;

    }

    protected ActionLocator getRootActionLocator(Injector injector) {
        return new RenderPageActionLocator(injector);
    }
}
