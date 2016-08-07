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
package tools.gsf.config2.inject;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.facade.assetapi.AssetIdWithSite;
import tools.gsf.time.Stopwatch;

/**
 * @author Tony Field
 * @since 2016-07-21
 */
public class AnnotationInjector implements Injector {

    private final ICS ics;
    private final BindInjector bindInjector;
    private final InjectForRequestInjector ifrInjector;
    private final MappingInjector mappingInjector;
    private final Stopwatch stopwatch;

    public AnnotationInjector(ICS ics, BindInjector bind, MappingInjector mapping, InjectForRequestInjector ifr, Stopwatch stopwatch) {
        this.ics = ics;
        this.bindInjector = bind;
        this.mappingInjector = mapping;
        this.ifrInjector = ifr;
        this.stopwatch = stopwatch;
    }

    @Override
    public void inject(Object dependent) {
        stopwatch.start();

        bindInjector.bind(dependent);
        stopwatch.split("AnnotationInjector: Bind injection done");

        AssetIdWithSite idWithSite = figureOutTemplateOrCSElementId(ics);
        if (idWithSite != null) {
            mappingInjector.inject(dependent, idWithSite);
            stopwatch.split("AnnotationInjector: Mapping injection done");
        }

        ifrInjector.inject(dependent);
        stopwatch.split("AnnotationInjector: InjectForRequest injection done");
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
