/*
 * Copyright (c) 2015 Function1 Inc. All rights reserved.
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
package com.fatwire.gst.foundation.facade.ilist;

import COM.FutureTense.Interfaces.IList;
import com.fatwire.gst.foundation.facade.sql.AbstractIList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tony Field
 * @since 2015-08-04 10:00 PM
 */
public class TwoColumnIList extends AbstractIList {

    private final String col1Name;
    private final String col2Name;
    private final List<Object[]> data;

    public TwoColumnIList(String name, String column1Name, String column2Name) {
        super(name);
        this.col1Name = column1Name;
        this.col2Name = column2Name;
        this.data = new ArrayList<Object[]>();
    }
    public void addRow(Object col1, Object col2) {
        Object[] row = {col1,col2};
        data.add(row);
    }
    public void addRows(Map map) {
        for(Object key : map.keySet()) {
            addRow(key, map.get(key));
        }
    }
    @Override
    public int numColumns() {
        return 2;
    }

    @Override
    public String getValue(String s) throws NoSuchFieldException {
        Object o = getObject(s);
        return o == null ? null : o.toString();
    }

    @Override
    public Object getObject(String s) throws NoSuchFieldException {
        Object[] row = data.get(currentRow() - 1);
        if(s != null && s.equals(col1Name)) {
            return row[0];
        }
        if (s != null && s.equals(col2Name)) {
            return row[1];
        }
        throw new NoSuchFieldException(s);
    }

    @Override
    public byte[] getFileData(String s) throws IllegalArgumentException, NoSuchFieldException {
        if (s == null) throw new NoSuchFieldException();
        if (s.equals(col1Name)) throw new IllegalArgumentException("Field "+s+" is not a file");
        if (s.equals(col2Name)) throw new IllegalArgumentException("Field "+s+" is not a file");
        throw new NoSuchFieldException(s);
    }

    @Override
    public String getFileString(String s) throws NoSuchFieldException {
        if (s == null) throw new NoSuchFieldException();
        if (s.equals(col1Name)) throw new IllegalArgumentException("Field "+s+" is not a file");
        if (s.equals(col2Name)) throw new IllegalArgumentException("Field "+s+" is not a file");
        throw new NoSuchFieldException(s);
    }

    @Override
    public void flush() {
        data.clear();
    }

    @Override
    public String getColumnName(int i) throws ArrayIndexOutOfBoundsException {
        if (i == 0) return col1Name;
        if (i == 1) return col2Name;
        throw new ArrayIndexOutOfBoundsException(i);
    }

    @Override
    public int numRows() {
        return data.size();
    }

    @Override
    public int numIndirectColumns() {
        return 0;
    }

    @Override
    public String getIndirectColumnName(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IList clone(String s) {
        TwoColumnIList clone = new TwoColumnIList(s, col1Name, col2Name);
        for (Object[] row : data) {
            clone.addRow(row[0], row[1]);
        }
        return clone;
    }

    @Override
    public boolean stringInList(String s) {
        for (Object[] row : data) {
            if (row[0] != null && row[0].equals(s)) return true;
            if (row[1] != null && row[1].equals(s)) return true;
        }
        return false;
    }
}
