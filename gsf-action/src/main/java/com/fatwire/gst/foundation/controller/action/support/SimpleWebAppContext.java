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

import javax.servlet.ServletContext;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.support.WebAppContext;

/**
 * This is the WebAppContext with accessor to the injector but without accessors
 * to the ActionLocator and ActionNameResolver, with the companion FactoryProducer.
 * 
 * Developers are expected to either subclass this class or use as a reference
 * for their own implementations.
 * 
 * In most cases they would only like to override {@link #getFactory(ICS)} for
 * their own Service factory.
 * 
 * @author Freddy Villalba
 * 
 */
public class SimpleWebAppContext extends WebAppContext implements FactoryProducer {

    public SimpleWebAppContext(final ServletContext context) {
        super(context);

    }

    public SimpleWebAppContext(final ServletContext context, final AppContext parent) {
        super(context, parent);

    }

    // NO createActionLocator method implemented in this base class

    // NO createActionNameResolver method implemented in this base class

    public Injector createInjector() {
        FactoryProducer fp = getBean("factoryProducer", FactoryProducer.class);
        return new DefaultAnnotationInjector(fp);
    }

    public FactoryProducer createFactoryProducer() {
        return this;
    }

    @Override
    public Factory getFactory(final ICS ics) {
        // called very often; once per request/pagelet, scoped per ICS context
        return new SimpleIcsBackedObjectFactoryTemplate(ics);
    }

}
