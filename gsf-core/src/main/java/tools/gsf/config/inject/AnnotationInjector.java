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
import tools.gsf.time.Stopwatch;

/**
 * @author Tony Field
 * @since 2016-07-21
 */
public class AnnotationInjector implements Injector {

    private final Stopwatch stopwatch;
    private final Injector[] injectors;

    public AnnotationInjector(Stopwatch stopwatch, Injector... injectors) {
        this.stopwatch = stopwatch;
        this.injectors = injectors;
    }

    @Override
    public void inject(Object dependent) {
        stopwatch.start();
        for (Injector injector : injectors) {
            injector.inject(dependent);
            stopwatch.split("AnnotationInjector: {} injection done", injector.getClass().getSimpleName());
        }
        stopwatch.elapsed("AnnotationInjector: injection complete");
    }
}
