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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.DependenciesAwareModelAndView;
import com.fatwire.assetapi.data.BaseController;
import tools.gsf.config.Factory;
import tools.gsf.config.FactoryLocator;
import tools.gsf.config.inject.Injector;
import tools.gsf.time.Stopwatch;

/**
 * Extension of Oracle's <code>BaseController</code> that invokes the
 * {@link Injector} to inject dependencies into itself. Injection is
 * done in the <code>handleRequest()</code> method. As most implementing
 * classes of <code>BaseController</code> are meant to override the
 * <code>doWork(Models models)</code> method, objects will be injected by
 * the time doWork is executed.
 *
 * The injector is configured in the {@link Factory}. Additional injection
 * capabilities can therefore be added without having to alter this object.
 *
 * This class also times the execution of the handleRequest() method using
 * the {@link Stopwatch} class.
 */
public class InjectingController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(InjectingController.class);

    public DependenciesAwareModelAndView handleRequest() {

        if (LOG.isDebugEnabled()) {
        	LOG.debug("These are all the vars available inside InjectingController for the current ICS:");
	        java.util.Enumeration allVars = ics.GetVars();
	        while (allVars.hasMoreElements()) {
	        	String varName = (String) allVars.nextElement();
	        	LOG.debug("ICS variable " + varName + " = " + ics.GetVar(varName));
	        }
        }

        Factory factory = FactoryLocator.locateFactory(ics);

        Stopwatch stopwatch = factory.getObject("stopwatch", Stopwatch.class);
        stopwatch.start();

        Injector injector = factory.getObject("compositeInjector", Injector.class);
        injector.inject(this);
        stopwatch.split("InjectingController: injecting into controller {}", this.getClass().getSimpleName());

        DependenciesAwareModelAndView result = super.handleRequest();
        stopwatch.elapsed("Executed controller {}", this.getClass().getSimpleName());

        return result;
    }
}
