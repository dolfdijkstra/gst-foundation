/* Copyright 2012 Oracle Corporation. All Rights Reserved.
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

package com.yourcompany.owcs.config;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fatwire.gst.foundation.controller.AppContext;

/**
 * A sample of a AppContext with direct access to Spring beans. This means that
 * all the beans need to be configured in Spring.
 * <p/>
 * For instance the spring application context needs to have a FactoryProducer
 * implementation.
 * 
 * @author Dolf Dijkstra
 * 
 */
public class SpringAppContext implements AppContext {
    private final WebApplicationContext wac;

    public SpringAppContext(final ServletContext context) {
        wac = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return (T) wac.getBean(name, requiredType);

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

}
