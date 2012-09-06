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

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.search.data.IndexData;
import com.fatwire.cs.core.search.data.ResultRow;
import com.fatwire.cs.core.search.query.Operation;
import com.fatwire.cs.core.search.query.QueryExpression;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.search.SimpleSearchEngine;

/**
 * @author Dolf Dijkstra
 * @since 6 sep. 2012
 *
 */
public class SearchAction implements Action {

    @InjectForRequest
    public SimpleSearchEngine lucene;

    @InjectForRequest
    public Model model;

    @Override
    public void handleRequest(ICS ics) {

        String query = ics.GetVar("q");

        QueryExpression qry = lucene.newQuery("name", Operation.CONTAINS, query);

        for (ResultRow row : lucene.search(qry, "Page")) {
            Map<String, String> result = new HashMap<String, String>();
            for (String name : row.getFieldNames()) {
                IndexData data = row.getIndexData(name);
                if (data != null) {
                    result.put(name, data.getData());
                }
            }
            model.list("results", result);
        }

    }
}
