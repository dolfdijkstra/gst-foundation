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

package gsf.mysite;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;

/**
 * This is a helper for the Factory. The idea is that based on a naming convention object are produced.
  *
 * 
 * <p/>
 * To find producer methods to the following
 * rules are used:
 * <ul>
 * <li>public <b>static</b> Foo createFoo(ICS ics, Factory factory){}</li>
 * <li>public Foo createFoo(ICS ics){}</li>
 * </ul>
 * If the non-static version is used the implementing class needs to have a
 * public constructor that takes {@see ICS} and {@see Factory} as arguments.
 *
*/


public class ObjectFactory {

   private Factory factory;
    
    ObjectFactory(ICS ics, Factory f){
     factory=f
    }

    @ServiceProducer(cache = false)
    static SolrSearchService createSolrSearchService(ICS ics, Factory factory) {
        //use factory.getObject() to access other services to compose the service if needed. Not shown here.
        return new DefaultSolrSearchService(ics)
    }

    @ServiceProducer(cache = false)
    SolrIndexService createSolrIndexService(ICS ics) {
        //use factory.getObject() to access other services to compose the service if needed. Not shown here.
        return new DefaultSolrIndexService(ics)
    }
    @ServiceProducer(cache=false)
    NavigationService createNavigationService(final ICS ics) {
            return new SimpleNavigationHelper(ics, factory.getObject("templateAssetAccess", TemplateAssetAccess.class),
                    "title", "path")
    }

}


