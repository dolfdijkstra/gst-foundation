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


import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.support.BaseActionLocator;


/**
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
public class GroovyActionLocator extends BaseActionLocator implements ActionLocator {
    private GroovyLoader groovyLoader;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(
     * COM.FutureTense.Interfaces.ICS)
     */
    public Action getAction(final ICS ics) {
        Action action = null;
        final String script = getScriptName(ics);
        action = groovyAction(script);
        if (action == null) {
            action = getFallbackActionLocator().getAction(ics);
        }
        injectDependencies(ics, action);

        return action;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.controller.action.ActionLocator#getAction(
     * COM.FutureTense.Interfaces.ICS, java.lang.String)
     */
    public Action getAction(final ICS ics, final String name) {
        Action action = null;
        action = groovyAction(name);
        if (action == null) {
            return null;
        }
        CacheManager.RecordItem(ics, "gsf-action:" + name);
        injectDependencies(ics, action);

        return action;
    }

    /**
     * 
     * 
     * @param name
     * @param action
     * @return the Action
     * @throws RuntimeException
     */
    private Action groovyAction(final String name) {
        Action action = null;
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

    protected String getScriptName(final ICS ics) {
        final String name = ics.ResolveVariables("CS.elementname") + ".groovy";
        return name.startsWith("/") ? name.substring(1) : name;
    }

    /**
     * @return the groovyLoader
     */
    public GroovyLoader getGroovyLoader() {
        return groovyLoader;
    }

    /**
     * @param groovyLoader the groovyLoader to set
     */
    public void setGroovyLoader(final GroovyLoader groovyLoader) {
        this.groovyLoader = groovyLoader;
    }

}
