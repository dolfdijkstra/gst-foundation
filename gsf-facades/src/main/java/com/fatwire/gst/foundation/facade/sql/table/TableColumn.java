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
        ccchar("cc.char"), ccvarchar("cc.varchar"), ccbigtext("cc.bigtext"), ccsmallint(
                "cc.smallint"), ccinteger("cc.integer"), ccbigint("cc.bigint"), ccnumeric(
                "cc.numeric"), ccdouble("cc.double"), ccdatetime("cc.datetime"), ccblob(
                "cc.blob");

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
