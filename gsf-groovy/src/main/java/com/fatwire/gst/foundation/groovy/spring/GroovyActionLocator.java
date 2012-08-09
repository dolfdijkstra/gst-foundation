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

package com.fatwire.gst.foundation.groovy.spring;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.controller.action.support.AbstractActionLocator;

import org.apache.commons.lang.StringUtils;

/**
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
public class GroovyActionLocator extends AbstractActionLocator {
    private GroovyLoader groovyLoader;

    public GroovyActionLocator() {
        super();

    }

    public GroovyActionLocator(ActionLocator fallbackActionLocator, Injector injector) {
        super(fallbackActionLocator, injector);
    }

    protected Action doFindAction(final ICS ics, final String name) {

        Action action = null;
        action = groovyAction(name);

        return action;
    }

    /**
     * Factory method for the Action
     * 
     * @param name the name of the action
     * @return the Action
     * @throws RuntimeException
     */
    private Action groovyAction(final String name) {

        if (StringUtils.isBlank(name))
            return null;
        Action action = null;
        final String script = name.endsWith(".groovy") ? name : name + ".groovy";
        try {

            if (getGroovyLoader().isValidScript(script)) {
                final Object o = getGroovyLoader().load(getGroovyLoader().toClassName(script));

                if (o instanceof Action) {
                    action = (Action) o;
                }
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug(script + " is not a valid script.");
            }
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return action;
    }

    /**
     * @return the groovyLoader
     */
    public final GroovyLoader getGroovyLoader() {
        return groovyLoader;
    }

    /**
     * @param groovyLoader the groovyLoader to set
     */
    public final void setGroovyLoader(final GroovyLoader groovyLoader) {
        this.groovyLoader = groovyLoader;
    }

}
