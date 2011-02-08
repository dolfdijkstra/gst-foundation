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

/**
 * 
 * 
 * A column definition for a table
 * 
 * @author Dolf Dijkstra
 * 
 */
public class TableColumn {

    public enum Type {
        ccchar("cc.char"), ccvarchar("cc.varchar"), ccbigtext("cc.bigtext"), ccsmallint("cc.smallint"), ccinteger(
                "cc.integer"), ccbigint("cc.bigint"), ccnumeric("cc.numeric"), ccdouble("cc.double"), ccdatetime(
                "cc.datetime"), ccblob("cc.blob");

        private final String prop;

        Type(String prop) {
            this.prop = prop;
        }

        public String getProperty() {
            return prop;
        }

    }

    private final String name;

    private final Type type;

    private final boolean primary;

    private boolean unique;

    private int length;

    private int decimal;

    private boolean nullable;

    public TableColumn(final String name, final Type type) {
        this(name, type, false);
    }

    public TableColumn(final String name, final Type type, final boolean primary) {
        super();
        this.name = name;
        this.primary = primary;
        this.type = type;
    }

    public int getDecimal() {
        return decimal;
    }

    public TableColumn setDecimal(int decimal) {
        this.decimal = decimal;
        return this;
    }

    public int getLength() {
        return length;
    }

    public TableColumn setLength(int length) {
        this.length = length;
        return this;
    }

    public boolean isNullable() {
        return nullable;
    }

    public TableColumn setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isUnique() {
        return unique;
    }

    public TableColumn setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

}
