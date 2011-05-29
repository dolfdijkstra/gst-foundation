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

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ActionLocator that holds a Map with the Actions. The 'name' is keyed to a map
 * that holds the Actions in a key/value pair.
 * <p/>
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2011-03-15
 */

public abstract class MapActionLocator extends BaseActionLocator {

    protected static final Log LOG = LogFactory.getLog(MapActionLocator.class.getPackage().getName());
    private Map<String, Action> commandActionMap = new HashMap<String, Action>();

    public MapActionLocator() {
        super();
    }

    public void setActionMap(final Map<String, Action> map) {
        LOG.debug("Configured action mapping with " + (map == null ? 0 : map.size() + " entries."));
        this.commandActionMap = map;
    }

    @Override
    protected Action doFindAction(ICS ics, String name) {
        Action action;
        action = commandActionMap.get(name);
        if (action != null) {
            injectDependencies(ics, action);
        }
        return action;

    }

}
