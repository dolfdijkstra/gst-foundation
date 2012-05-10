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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.AnnotationInjector;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.mapping.MappingInjector;

import org.springframework.util.Assert;

public class DefaultAnnotationInjector implements Injector {

    private FactoryProducer factoryFactory;

    public DefaultAnnotationInjector() {
        super();

    }

    public DefaultAnnotationInjector(FactoryProducer factoryFactory) {
        super();
        this.factoryFactory = factoryFactory;
    }

    @Override
    public void inject(ICS ics, Object action) {
        final Factory factory = getFactory(ics);
        AnnotationInjector.inject(action, factory);
        final AssetIdWithSite id = figureOutTemplateOrCSElementId(ics);
        if (id != null) {
            MappingInjector.inject(action, factory, id);
        }

    }

    private AssetIdWithSite figureOutTemplateOrCSElementId(final ICS ics) {
        String eid = ics.GetVar("eid");
        if (eid != null) {
            return new AssetIdWithSite("CSElement", Long.parseLong(eid), ics.GetVar("site"));
        }
        eid = ics.GetVar("tid");
        if (eid != null) {
            return new AssetIdWithSite("Template", Long.parseLong(eid), ics.GetVar("site"));
        }
        return null;
    }

    protected Factory getFactory(final ICS ics) {

        final Object o = ics.GetObj(Factory.class.getName());
        if (o instanceof Factory) {
            return (Factory) o;
        }
        Factory factory = null;

        factory = getFactoryFactory().getFactory(ics);

        ics.SetObj(Factory.class.getName(), factory);
        return factory;
    }

    public FactoryProducer getFactoryFactory() {
        return factoryFactory;
    }

    public void setFactoryFactory(FactoryProducer factoryFactory) {
        Assert.notNull(factoryFactory);
        this.factoryFactory = factoryFactory;
    }
}
