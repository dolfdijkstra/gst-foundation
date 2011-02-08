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

package com.fatwire.gst.foundation.facade.sql.table;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The definition of a ContentServer database table
 * 
 * @author Dolf Dijkstra
 * 
 */

public class TableDef {
    private final String name;

    private final String acl;

    private final String type;

    private final List<TableColumn> columns = new LinkedList<TableColumn>();

    public TableDef(final String name, final String acl, final String type) {
        super();
        this.name = name;
        this.acl = acl;
        this.type = type;
    }

    public TableDef addColumn(TableColumn col) {
        if (col.isPrimary()) {
            for (TableColumn current : columns) {
                if (current.isPrimary()) {
                    throw new IllegalStateException("Table has already have a primary column");
                }
            }
        }
        this.columns.add(col);
        return this;
    }

    Iterable<TableColumn> getColumns() {
        return Collections.unmodifiableCollection(columns);
    }

    public String getAcl() {
        return acl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
