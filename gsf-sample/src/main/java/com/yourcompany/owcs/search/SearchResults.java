package com.yourcompany.owcs.search;

import java.util.LinkedList;
import java.util.List;

import com.fatwire.gst.foundation.facade.sql.Row;

public class SearchResults {

    private List<Row> rows = new LinkedList<Row>();

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public int getSize() {
        return rows.size();
    }

}
