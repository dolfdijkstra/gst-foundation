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
package com.fatwire.gst.foundation.groovy.context;

import org.apache.commons.lang3.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.support.BaseFactory;

/**
 * 
 * Factory that dynamically loads other classes that provide producer methods.
 * 
 * @author Dolf Dijkstra
 * @since September 23, 2012
 * 
 * 
 * @deprecated as of release 12.x, replace with WCS 12c's native Groovy support
 * 
 */
public class GroovyFactory extends BaseFactory {

    private final ClassLoader classLoader;
    private Class<?> generalFactoryClass;

    public GroovyFactory(ICS ics, ClassLoader gcl, Factory... roots) {
        super(ics, roots);
        this.classLoader = gcl;
        try {
            this.generalFactoryClass = classLoader.loadClass("gsf.ObjectFactory");
        } catch (ClassNotFoundException e) {
            // ignore
            this.generalFactoryClass = null;
        }

    }

    @Override
    protected Class<?>[] factoryClasses(ICS ics) {
        String site = ics.GetVar("site");
        Class<?> siteClass = null;

        if (StringUtils.isNotBlank(site)) {
            try {
                siteClass = classLoader.loadClass("gsf." + site.toLowerCase() + ".ObjectFactory");
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        if (siteClass == null) {
            if (generalFactoryClass == null) {
                return new Class[0];
            } else {
                return new Class[] { generalFactoryClass };
            }
        } else {
            if (generalFactoryClass == null) {
                return new Class[] { siteClass };
            } else {
                return new Class[] { siteClass, generalFactoryClass };
            }

        }
    }
}
