package com.fatwire.gst.foundation.facade.sql.table;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    public void addColumn(TableColumn col) {
        if (col.isPrimary()) {
            for (TableColumn current : columns) {
                if (current.isPrimary()) {
                    throw new IllegalStateException(
                            "Table has already have a primary column");
                }
            }
        }
        this.columns.add(col);
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
