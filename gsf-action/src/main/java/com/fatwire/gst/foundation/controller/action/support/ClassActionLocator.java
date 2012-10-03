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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.ActionLocator;
import com.fatwire.gst.foundation.controller.action.Injector;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

/**
 * ActionLocator that loads actions based on a naming convention; if the action name is prefixed with <tt>class:</tt>
 * and the rest of the action name is a java class that implements the Action interface , an Action from this class will be created.
 
 * 
 * @author Dolf Dijkstra
 *
 */
public class ClassActionLocator extends AbstractActionLocator {
    private static final Log LOG = LogUtil.getLog(ClassActionLocator.class);
    private static final String CLASS_PREFIX = "class:";

    public ClassActionLocator(ActionLocator fallbackActionLocator, Injector injector) {
        super(fallbackActionLocator, injector);

    }

    public ClassActionLocator() {
        super();
    }

    @Override
    protected Action doFindAction(ICS ics, String name) {
        if (StringUtils.startsWith(name, CLASS_PREFIX)) {
            String c = StringUtils.trim(StringUtils.substringAfter(name, CLASS_PREFIX));
            if (StringUtils.isEmpty(c)) {
                LOG.warn("Passed in classname with the " + CLASS_PREFIX + " is null or empty.");
            } else {
                try {
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(c);
                    if (Action.class.isAssignableFrom(clazz)) {
                        LOG.debug("Creating Action for class " + clazz);
                        Action action = (Action) clazz.newInstance();
                        return action;
                    } else {
                        throw new RuntimeException("Class " + c + " is not an Action.");
                    }

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class " + c + " cannot be found.", e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Class " + c + " " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Class " + c + " " + e.getMessage(), e);
                }
            }

        }
        return null;
    }

}
