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

package tools.gsf.facade.search;

import com.fatwire.cs.core.search.data.ResultRow;
import com.fatwire.cs.core.search.engine.SearchResult;

import java.util.Iterator;

/**
 * Iterable wrapper over a SearchResult so that the API can be
 * used in a for-each loop.
 *
 * @author Tony Field
 * @since Feb 16, 2011
 */
public final class SearchResultIterable implements Iterable<ResultRow> {
    private final SearchResult<ResultRow> searchResult;

    public SearchResultIterable(SearchResult<ResultRow> searchResult) {
        this.searchResult = searchResult;
    }

    public Iterator<ResultRow> iterator() {
        return new Iterator<ResultRow>() {
            public boolean hasNext() {
                return searchResult.hasNext();
            }

            public ResultRow next() {
                return searchResult.next();
            }

            public void remove() {
                throw new RuntimeException("Can not remove");
            }
        };
    }
}
