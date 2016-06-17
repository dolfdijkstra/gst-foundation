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

import java.lang.reflect.Constructor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;

/**
 * Producer for a {@link Factory} that makes use of reflection to construct the
 * Factory.
 * 
 * @author Dolf.Dijkstra
 * 
 */
public class ReflectionFactoryProducer implements FactoryProducer {
    protected static final Logger LOG = LoggerFactory.getLogger("com.function1.gsf.foundation.controller.action.support.ReflectionFactoryProducer");
    
    private Constructor<Factory> constructor;

    public Factory getFactory(final ICS ics) {
        Factory factory = null;
        try {
            factory = getInjectionFactory(ics);
        } catch (final Exception e) {
            LOG.warn("Error whilst getting the injection factory", e);
        }
        if (factory == null) {
            factory = new IcsBackedObjectFactoryTemplate(ics);
        }
        return factory;
    }

    public final Factory getInjectionFactory(final ICS ics) throws Exception {
        Factory factory = null;
        if (constructor != null) {
            factory = constructor.newInstance(ics);
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private void findConstructor(final String factoryClassname) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException {
        if (StringUtils.isNotBlank(factoryClassname)) {
            final Class<Factory> c = (Class<Factory>) Class.forName(factoryClassname);
            constructor = c.getConstructor(ICS.class);
        } else {
            throw new IllegalArgumentException("factoryClassname cannot be blank.");
        }
    }

    /**
     * @param factoryClassname the factoryClassname to set
     */
    public void setFactoryClassname(final String factoryClassname) {
        try {
            findConstructor(factoryClassname);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new IllegalArgumentException("factoryClassname: " + factoryClassname + " is illegal. "
                    + e.getMessage(), e);
        }
    }
}
