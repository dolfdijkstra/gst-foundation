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

package com.fatwire.gst.foundation.facade.sql;

import java.util.Iterator;

import COM.FutureTense.Interfaces.IList;

/**
 * Wrapper for an IList that turns an <tt>IList</tt> into a <tt>Iterable</tt>.
 * 
 * Sample usage:
 * 
 * <pre>
 * SortedSet&lt;VirtualWebroot&gt; result = new TreeSet&lt;VirtualWebroot&gt;(new UrlInfoComparator());
 * for (Row listRow : new IListIterable(ics.GetList(&quot;pr-out&quot;))) {
 *     result.add(getVirtualWebroot(listRow.getLong(&quot;id&quot;)));
 * }
 * </pre>
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */
public class IListIterable implements Iterable<Row> {
    private final IList list;

    private final int numRows;

    public IListIterable(final IList list) {
        super();
        this.list = list;
        if (list != null) {
            numRows = list.numRows();
        } else {
            numRows = 0;
        }
    }

    public Iterator<Row> iterator() {
        if (list == null || !list.hasData()) {
            return new Iterator<Row>() {

                public boolean hasNext() {
                    return false;
                }

                public Row next() {
                    return null;
                }

                public void remove() {
                    throw new RuntimeException("Can not remove");
                }

            };
        }
        return new Iterator<Row>() {
            private int rowNum = 0;

            public boolean hasNext() {
                return rowNum < numRows;
            }

            public Row next() {
                rowNum++;
                list.moveTo(rowNum);
                return new SingleRow(list);
            }

            public void remove() {
                throw new RuntimeException("Can not remove");
            }
        };
    }

    public int size() {
        return numRows;
    }

    public void flush() {
        if (list != null) {
            list.flush();
        }
    }

    public String getColumnName(final int i) {
        if (list != null) {
            return list.getColumnName(i);
        }
        return "";
    }

    public String getIndirectColumnName(final int index) {
        if (list != null) {
            return list.getIndirectColumnName(index);
        }
        return "";
    }

    public int numColumns() {
        if (list != null) {
            return list.numColumns();
        }
        return 0;
    }

    public int numIndirectColumns() {
        if (list != null) {
            return list.numIndirectColumns();
        }
        return 0;
    }

}
