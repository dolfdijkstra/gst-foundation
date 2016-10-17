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
package com.yourcompany.owcs.action;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.yourcompany.owcs.search.SearchResults;
import com.yourcompany.owcs.search.SolrSearchService;

/**
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 *
 */
public class SampleServiceUseAction implements Action {
    @InjectForRequest
    public Model model;

    @InjectForRequest
    public SolrSearchService search;

    @Override
    public void handleRequest(ICS ics) {
        SearchResults results = search.search("my query");
        model.add("searchResults", results);

    }

}
