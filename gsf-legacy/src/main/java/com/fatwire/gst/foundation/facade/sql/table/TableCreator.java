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

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import com.fatwire.gst.foundation.CSRuntimeException;

/**
 * Facade over table create and delete CatalogManager operations
 *
 * @author Dolf Dijkstra
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */

public class TableCreator {

    private final ICS ics;

    public TableCreator(final ICS ics) {
        super();
        this.ics = ics;
    }

    /**
     * Delete a table
     *
     * @param name the name of the table to delete
     */
    public void delteTable(String name) {
        ics.ClearErrno();
        final FTValList list = new FTValList();

        list.setValString("ftcmd", "deletetable");
        list.setValString("tablename", name);
        if (!ics.CatalogManager(list)) {
            throw new CSRuntimeException("Error deleting table " + name, ics.GetErrno());
        }

    }

    /**
     * Create a table
     *
     * @param table the table to create as defined by its TableDef
     */
    public void createTable(TableDef table) {
        ics.ClearErrno();
        final FTValList list = new FTValList();

        list.setValString("ftcmd", "createtable");
        list.setValString("tablename", table.getName());
        list.setValString("systable", table.getType());
        list.setValString("aclList", table.getAcl());
        int i = 0;
        for (TableColumn col : table.getColumns()) {

            list.setValString("colname" + i, col.getName());
            StringBuilder val = new StringBuilder();
            val.append(ics.GetProperty(col.getType().getProperty()));
            switch (col.getType()) {
                case ccdouble:
                case ccchar:
                case ccnumeric:
                case ccvarchar:
                case ccdatetime:
                    if (col.getLength() > 0) {
                        val.append(" (");
                        val.append(Integer.toString(col.getLength()));
                        switch (col.getType()) {
                            case ccdouble:
                                val.append(",").append(Integer.toString(col.getDecimal()));
                                break;
                            default:
                                break;
                        }

                        val.append(")");
                    }
                    break;
                default:
                    break;

            }
            if (col.isPrimary()) {
                val.append(" ");
                val.append(ics.GetProperty("cc.primary"));
            } else if (col.isNullable()) {
                val.append(" ");
                val.append(ics.GetProperty("cc.null"));
            } else {
                val.append(" NOT NULL");
            }

            list.setValString("colvalue" + i, val.toString());
            i++;
        }

        if (!ics.CatalogManager(list)) {
            throw new CSRuntimeException("Error creating table " + table.getName(), ics.GetErrno());
        }

    }

}
