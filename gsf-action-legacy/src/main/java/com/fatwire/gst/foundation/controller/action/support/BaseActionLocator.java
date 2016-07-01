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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.action.RenderPage;

/**
 * ActionLocator with support for Factory and a fall back ActionLocator.
 * <p>
 * Objects are created via a {@link Factory}, that can be configured via the
 * <tt>factoryClassname</tt>. That class needs to have a constructor accepting
 * ICS.
 * 
 * @author Dolf Dijkstra
 * @since Apr 27, 2011
 */
public abstract class BaseActionLocator extends AbstractActionLocator {

    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.foundation.controller.action.support.BaseActionLocator");

    private ReflectionFactoryProducer f = new ReflectionFactoryProducer();
    private Injector i;

    public BaseActionLocator() {
        super();
        this.setFallbackActionLocator(new ActionLocator() {

            public Action getAction(final ICS ics, final String name) {
                Action action = new RenderPage();
                injectDependencies(ics, action);
                return action;
            }

        });
        i = new DefaultAnnotationInjector(f);
        this.setInjector(i);
    }

    /**
     * @param factoryClassname the factoryClassname to set
     */
    public void setFactoryClassname(final String factoryClassname) {
        f.setFactoryClassname(factoryClassname);
    }
}
