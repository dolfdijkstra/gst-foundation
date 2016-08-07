/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.config.inject;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.config.Factory;
import tools.gsf.config.FactoryProducer;
import tools.gsf.facade.assetapi.AssetIdWithSite;

/**
 * @author Tony Field
 * @since 2016-07-21
 */
public class AnnotationInjector implements Injector {

    private final FactoryProducer factoryProducer;
    private final BindInjector bindInjector;
    private final InjectForRequestInjector ifrInjector;
    private final MappingInjector mappingInjector;

    public AnnotationInjector(FactoryProducer factoryProducer) {
        this.factoryProducer = factoryProducer;
        this.bindInjector = new BindInjector(factoryProducer);
        this.ifrInjector = new InjectForRequestInjector();
        this.mappingInjector = new MappingInjector();
    }

    @Override
    public void inject(ICS ics, Object dependent) {
        Factory factory = factoryProducer.getFactory(ics);
        ifrInjector.inject(dependent, factory);
        bindInjector.bind(dependent, ics);
        AssetIdWithSite id = figureOutTemplateOrCSElementId(ics);
        if (id != null) {
            mappingInjector.inject(dependent, factory, id);
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
}
