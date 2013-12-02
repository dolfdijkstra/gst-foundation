/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.search;

import java.util.Arrays;
import java.util.List;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.cs.core.search.data.ResultRow;
import com.fatwire.cs.core.search.engine.SearchEngine;
import com.fatwire.cs.core.search.engine.SearchEngineConfig;
import com.fatwire.cs.core.search.engine.SearchEngineException;
import com.fatwire.cs.core.search.engine.SearchResult;
import com.fatwire.cs.core.search.query.Operation;
import com.fatwire.cs.core.search.query.QueryExpression;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.search.util.SearchUtils;

/**
 * Simplified SearchEngine class optimized for common use cases
 *
 * @author Tony Field
 * @since Feb 16, 2011
 */
public class SimpleSearchEngine {

    private final SearchEngine searchEngine;
    private final SearchEngineConfig seConfig;

    public SimpleSearchEngine(String engineName) {
        try {
            seConfig = SearchUtils.getSearchEngineConfig();
        } catch (SearchEngineException e) {
            throw new CSRuntimeException("Could not get search engine config", ftErrors.exceptionerr, e);
        }
        searchEngine = seConfig.getEngine(engineName);
    }

    /**
     * Return an instance of the search engine. This is typically done by passing "lucene" as the engineName
     * parameter because that is the default search engine that ships with Content Server 7.5.
     *
     * @param engineName search engine name as configured in Content Server.  Typically set to "lucene".
     * @return SimpleSearchEngine instance
     */
    public static SimpleSearchEngine getInstance(String engineName) {
        return new SimpleSearchEngine(engineName);
    }

    public SearchResultIterable search(QueryExpression query, List<String> indexNames) {
        SearchResult<ResultRow> sr;
        try {
            sr = searchEngine.search(indexNames, query);
        } catch (SearchEngineException e) {
            throw new CSRuntimeException("Search failed with an exception. Index: " + indexNames + ", Query: " + query, ftErrors.exceptionerr, e);
        }
        return new SearchResultIterable(sr);
    }

    /**
     * @param query
     * @param indexNames
     * @return search results based on query and indexes provided.
     */
    public SearchResultIterable search(QueryExpression query, String... indexNames) {
        return search(query, Arrays.asList(indexNames));
    }


    /**
     * @param stringValue
     * @return a new query based on the passed in string.
     * @see SearchUtils#newQuery(String)
     */
    public QueryExpression newQuery(String stringValue) {
        return SearchUtils.newQuery(stringValue);
    }

    /**
     * @param fieldName
     * @param op
     * @param values
     * @return a query based on the fields, operation and values.
     */
    public QueryExpression newQuery(String fieldName, Operation op, Object... values) {
        return SearchUtils.newQuery(fieldName, op, Arrays.asList(values));
    }

}
