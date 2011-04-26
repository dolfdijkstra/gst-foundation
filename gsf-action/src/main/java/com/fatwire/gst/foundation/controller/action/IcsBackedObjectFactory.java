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

package com.fatwire.gst.foundation.controller.action;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.AnnotationInjector.Factory;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

/**
 * @author Dolf Dijkstra
 * @since Apr 15, 2011
 */
public class IcsBackedObjectFactory implements Factory {

    final protected ICS ics;

    /**
     * @param ics
     */
    public IcsBackedObjectFactory(ICS ics) {
        super();
        this.ics = ics;
    }

    /**
     * Provides the data to be injected into the action
     * 
     * @param fieldName the name of the field to be set
     * @param fieldType the type of the field used by the action
     * @return object to inject or null if it is not known how to do it
     */
    @SuppressWarnings("unchecked")
    public Object getObject(String fieldName, @SuppressWarnings("rawtypes") Class fieldType) {
        if (ICS.class.isAssignableFrom(fieldType)) {
            return ics;
        }
        if (WraCoreFieldDao.class.isAssignableFrom(fieldType)) {
            return WraCoreFieldDao.getInstance(ics);
        }
        if (AliasCoreFieldDao.class.isAssignableFrom(fieldType)) {
            WraCoreFieldDao wraCoreFieldDao = WraCoreFieldDao.getInstance(ics);
            return new AliasCoreFieldDao(ics, wraCoreFieldDao);
        }
        if (WraPathTranslationService.class.isAssignableFrom(fieldType)) {
            return WraPathTranslationServiceFactory.getService(ics);
        }
        return null;
    }

}
