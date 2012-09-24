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

package com.yourcompany.owcs.config;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Factory;
import com.fatwire.gst.foundation.controller.action.support.IcsBackedObjectFactoryTemplate;
import com.yourcompany.owcs.search.SolrSearchService;
import com.yourcompany.owcs.search.solr.DefaultSolrSearchService;

/**
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 * 
 */
public class MyExtendedObjectFactory extends IcsBackedObjectFactoryTemplate {

    public MyExtendedObjectFactory(ICS ics) {
        super(ics);

    }

    public MyExtendedObjectFactory(ICS ics, Factory[] roots) {
        super(ics, roots);
    }

    public SolrSearchService createSolrSearchService(ICS ics) {
        return new DefaultSolrSearchService(ics);
    }

}
