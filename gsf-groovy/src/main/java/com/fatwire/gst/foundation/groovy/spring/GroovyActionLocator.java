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
import com.fatwire.gst.foundation.controller.action.support.BaseActionLocator;

import org.apache.commons.lang.StringUtils;

/**
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
public class GroovyActionLocator extends BaseActionLocator {
    private GroovyLoader groovyLoader;

    protected Action doFindAction(final ICS ics, final String name) {

        Action action = null;
        action = groovyAction(name);
        if (action != null) {
            injectDependencies(ics, action);
        }

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
        Action action = null;
        if (StringUtils.isBlank(name))
            return null;
        final String script = name.endsWith(".groovy") ? name : name + ".groovy";
        try {

            if (groovyLoader.isValidScript(script)) {
                final Object o = groovyLoader.load(groovyLoader.toClassName(script));

                if (o instanceof Action) {
                    action = (Action) o;
                }
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
