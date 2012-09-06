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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.groovy.context.GroovyWebContext;

/**
 * A sample on how to extend the service factory in GSF. This class is an extention of the groovy app context, giving access to another ObjectFactory.
 * This class needs to be registered to the file META-INF/gsf-contexts.
 * 
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 *
 */
public class ExtendedAppContext extends GroovyWebContext {

    public ExtendedAppContext(ServletContext context, AppContext app) {
        super(context, app);

    }

    @Override
    public Factory getFactory(ICS ics) {
        return new MyExtendedObjectFactory(ics);// super.getFactory(ics);
    }

}
