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

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.support.BaseFactory;

/**
 * 
 * Factory that dynamically loads other classes that provide producer methods.  
 * @author Dolf Dijkstra
 * @since September 23, 2012
 *
 */
public class GroovyFactory extends BaseFactory {

    private final ClassLoader classLoader;

    public GroovyFactory(ICS ics, ClassLoader gcl, Factory... roots) {
        super(ics, roots);
        this.classLoader = gcl;
    }

    protected Class<?>[] findClasses(ICS ics) {
        String site = ics.GetVar("site");
        Class<?> cs = null;
        Class<?> gc = null;

        if (StringUtils.isNotBlank(site)) {
            try {
                cs = classLoader.loadClass("gsf." + site.toLowerCase() + ".ObjectFactory");
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        try {
            gc = classLoader.loadClass("gsf.ObjectFactory");
        } catch (ClassNotFoundException e) {
            // ignore
        }
        if (cs == null) {
            if (gc == null) {
                return new Class[0];
            } else {
                return new Class[] { gc };
            }
        } else {
            if (gc == null) {
                return new Class[] { cs };
            } else {
                return new Class[] { cs, gc };
            }

        }
    }
}
