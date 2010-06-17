package com.fatwire.gst.foundation.facade.sql.table;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.CSRuntimeException;

/**
 * Facade over table create and delete CatalogManager operations
 * 
 * @author Dolf Dijkstra
 * 
 */

public class TableCreator {

    private final ICS ics;

    public TableCreator(final ICS ics) {
        super();
        this.ics = ics;
    }

    /**
     * 
     * Delete a table
     * 
     * @param name
     *            the name of the table to delete
     */
    public void delteTable(String name) {
        ics.ClearErrno();
        final FTValList list = new FTValList();

        list.setValString("ftcmd", "deletetable");
        list.setValString("tablename", name);
        if (!ics.CatalogManager(list)) {
            throw new CSRuntimeException("Error deleting table " + name, ics
                    .GetErrno());
        }

    }

    /**
     * Create a table
     * 
     * @param table
     *            the table to create as defined by its TableDef
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
            throw new CSRuntimeException("Error creating table "
                    + table.getName(), ics.GetErrno());
        }

    }

}
