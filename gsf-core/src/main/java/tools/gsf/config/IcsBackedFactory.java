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
package tools.gsf.config;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.config.inject.AnnotationInjector;
import tools.gsf.config.inject.BindInjector;
import tools.gsf.config.inject.InjectForRequestInjector;
import tools.gsf.config.inject.Injector;
import tools.gsf.config.inject.MappingInjector;
import tools.gsf.mapping.IcsMappingService;
import tools.gsf.mapping.MappingService;
import tools.gsf.time.Stopwatch;

/**
 * @author Tony Field
 * @since 2016-08-05
 */
public class IcsBackedFactory extends AbstractDelegatingFactory<ICS> {

    private final ICS ics;

    public IcsBackedFactory(ICS ics, Factory delegate) {
        super(ics, delegate);
        this.ics = ics;
    }

    @ServiceProducer(cache = true)
    public BindInjector createBindInjector() {
        return new BindInjector(ics);
    }

    @ServiceProducer(cache = true)
    public InjectForRequestInjector createInjectForRequestInjector() {
        Factory factory = FactoryLocator.locateFactory(ics);
        return new InjectForRequestInjector(factory);
    }

    @ServiceProducer(cache = true)
    public MappingService createMappingService() {
        return new IcsMappingService(ics);
    }

    @ServiceProducer(cache = true)
    public MappingInjector createMappingInjector() {
        MappingService mappingService = getObject("mappingService", MappingService.class);
        return new MappingInjector(mappingService);
    }

    @ServiceProducer(cache = true)
    public Injector createInjector() {
        BindInjector bind = getObject("bindInjector", BindInjector.class);
        MappingInjector map = getObject("mappingInjector", MappingInjector.class);
        InjectForRequestInjector ifr = getObject("injectForRequestInjector", InjectForRequestInjector.class);
        Stopwatch stopwatch = getObject("stopwatch", Stopwatch.class);
        return new AnnotationInjector(ics, bind, map, ifr, stopwatch);
    }

}
