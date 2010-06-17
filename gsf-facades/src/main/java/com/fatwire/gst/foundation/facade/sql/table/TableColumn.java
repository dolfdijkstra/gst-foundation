package com.fatwire.gst.foundation.facade.sql.table;

public class TableColumn {

    public enum Type {
        propchar("cc.char"), propvarchar("cc.varchar"), proplongvarchar(
                "cc.bigtext"), propsmallint("cc.smallint"), propinteger(
                "cc.integer"), propbigint("cc.bigint"), propnumeric(
                "cc.numeric"), propdouble("cc.double"), proptimestamp(
                "cc.datetime"), proplongvarbinary("cc.blob");
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

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
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

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

}
