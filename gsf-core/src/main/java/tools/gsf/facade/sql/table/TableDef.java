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

package tools.gsf.facade.sql.table;

import tools.gsf.facade.sql.table.TableColumn.Type;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The definition of a ContentServer database table
 *
 * @author Dolf Dijkstra
 */

public class TableDef {
    private final String name;

    private final String acl;

    private final String type;

    private final List<TableColumn> columns = new LinkedList<TableColumn>();

    /**
     * @param name the name of the table
     * @param acl  the acl for the table
     * @param type the type of the table
     */
    public TableDef(final String name, final String acl, final String type) {
        super();
        this.name = name;
        this.acl = acl;
        this.type = type;
    }

    /**
     * @param col the column to add
     * @return the current TableDef, this.
     */
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

    /**
     * Adds a column to this table.
     *
     * @param name    the name of the column
     * @param type    the type of the column
     * @param primary boolean flag for primary
     * @return the added TableColumn.
     */
    public TableColumn addColumn(final String name, final Type type, final boolean primary) {
        TableColumn col = new TableColumn(name, type, primary);
        if (col.isPrimary()) {
            for (TableColumn current : columns) {
                if (current.isPrimary()) {
                    throw new IllegalStateException("Table has already have a primary column");
                }
            }
        }
        this.columns.add(col);
        return col;
    }

    /**
     * Adds a non primary column to this table.
     *
     * @param name column name
     * @param type column type
     * @return the added TableColumn.
     */
    public TableColumn addColumn(final String name, final Type type) {

        return addColumn(name, type, false);
    }

    public Iterable<TableColumn> getColumns() {
        return Collections.unmodifiableCollection(columns);
    }

    /**
     * @return the acl value
     */
    public String getAcl() {
        return acl;
    }

    /**
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type value
     */
    public String getType() {
        return type;
    }

}
