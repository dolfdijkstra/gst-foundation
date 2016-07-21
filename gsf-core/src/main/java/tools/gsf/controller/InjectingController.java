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
package tools.gsf.controller;

import COM.FutureTense.Interfaces.DependenciesAwareModelAndView;
import com.fatwire.assetapi.data.BaseController;
import tools.gsf.config.AppContext;
import tools.gsf.config.Factory;
import tools.gsf.config.FactoryProducer;
import tools.gsf.config.WebAppContextUtil;
import tools.gsf.config.inject.Injector;
import tools.gsf.time.Stopwatch;

import javax.servlet.ServletContext;

public class InjectingController extends BaseController {

    public DependenciesAwareModelAndView handleRequest() {

        ServletContext srvCtx = ics.getIServlet().getServlet().getServletContext();
        AppContext ctx = WebAppContextUtil.getWebAppContext(srvCtx);

        Injector injector = ctx.getBean("Injector", Injector.class);
        FactoryProducer fp = ctx.getBean("FactoryProducer", FactoryProducer.class);
        Factory factory = fp.getFactory(ics);
        Stopwatch stopwatch = factory.getObject("stopwatch", Stopwatch.class);

        stopwatch.start();

        injector.inject(ics, this);
        stopwatch.split("Injecting into controller {}", this.getClass().getSimpleName());

        DependenciesAwareModelAndView result = super.handleRequest();
        stopwatch.elapsed("Executed controller {}", this.getClass().getSimpleName());

        return result;
    }
}
