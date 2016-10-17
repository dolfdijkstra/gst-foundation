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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.FactoryProducer;
import com.fatwire.gst.foundation.controller.action.support.SpringObjectFactory;

/**
 * A too simplistic example on how to get to service classes through Spring. This
 * implementation is silly because it does not provide access to any services
 * that need ICS.
 * 
 * @author dolf
 * 
 */
public class SpringFactoryProducer implements FactoryProducer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    
    /**
     * This is a very simplistic implementation.
     * More realistic would be to return something like:
     * <pre>
     * {@code
     * Factory root= new SpringObjectFactory(applicationContext);
     * return new MyExtendedObjectFactory(ics,root);
     * }
     * </pre>
     * 
     * 
     */
    @Override
    public Factory getFactory(ICS ics) {
        

        return new SpringObjectFactory(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
