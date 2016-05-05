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
package com.fatwire.gst.foundation.controller.action.support;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;

/**
 * Factory that reads the producer methods of the passed in class names.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class ClassBasedFactory extends BaseFactory {

    private Class<?>[] clx;

    /**
     * @param ics Content Server context object
     * @param parent the parent factory
     * @param classnames the names of the classes that hold the factory methods
     */
    public ClassBasedFactory(ICS ics, Factory parent, String... classnames) {
        this(ics, parent, Thread.currentThread().getContextClassLoader(), classnames);
    }

    /**
     * @param ics Content Server context object
     * @param parent the parent factory
     * @param classLoader the classloader to load the classes of
     * @param classnames the names of the classes that hold the factory methods
     */
    public ClassBasedFactory(ICS ics, Factory parent, ClassLoader classLoader, String... classnames) {
        super(ics, parent);
        clx = new Class[classnames.length];
        for (int i = 0; i < classnames.length; i++) {
            try {
                clx[i] = classLoader.loadClass(classnames[i]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    @Override
    protected Class<?>[] factoryClasses(ICS ics) {
        return super.factoryClasses(ics);
    }

}
