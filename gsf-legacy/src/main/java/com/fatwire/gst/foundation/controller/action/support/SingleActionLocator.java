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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Injector;

/**
 * Class that provides access to a single action, ie the name is ignored and it
 * will always return the same new Action, from the actionClass field.
 * 
 * @author Dolf Dijkstra
 * 
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public class SingleActionLocator extends AbstractActionLocator {

    private Class<Action> actionClass;

    public SingleActionLocator() {
        super();
    }

    public SingleActionLocator(Injector injector, String actionClass) {
        super(injector);
        setActionClass(actionClass);
    }

    @Override
    protected Action doFindAction(ICS ics, String name) {

        try {
            return actionClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<Action> getActionClass() {
        return actionClass;
    }

    @SuppressWarnings("unchecked")
    public void setActionClass(String actionClass) {
        try {
            this.actionClass = (Class<Action>) Thread.currentThread().getContextClassLoader().loadClass(actionClass);
            this.actionClass.newInstance(); // test if this class can be
                                            // instantiated
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
