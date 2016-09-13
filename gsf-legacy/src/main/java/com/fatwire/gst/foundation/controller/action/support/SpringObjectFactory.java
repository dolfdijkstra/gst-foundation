/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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

import org.springframework.context.ApplicationContext;

import com.fatwire.gst.foundation.controller.action.Factory;

/**
 * Factory with access to Spring framework beans.
 * 
 * @author Dolf Dijkstra
 * @deprecated see "tools.gsf.config.SpringObjectFactory"
 */
public class SpringObjectFactory implements Factory {

    private final ApplicationContext app;

    /**
     * @param app application context
     */
    public SpringObjectFactory(ApplicationContext app) {
        super();
        this.app = app;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(String name, Class<T> requiredType) {
        return (T) app.getBean(name, requiredType);
    }

}
