package com.fatwire.gst.foundation;

import java.util.Date;

import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.cs.core.db.Util;

/**
 * Miscellaneous IList Utilities
 * 
 * @author Tony Field
 * @since 21-Nov-2008
 */
public final class IListUtils {
    private IListUtils() {
    }

    /**
     * Return a string value for a column. Wraps the possible checked exception
     * NoSuchFieldException with an IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return string value
     * @throws IllegalArgumentException if the column name is not found
     */
    public static String getStringValue(IList list, String colname) {
        try {
            return list.getValue(colname);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field: " + colname, e);
        }
    }

    /**
     * Return a long value for a column. Wraps the possible checked exception
     * NoSuchFieldException with an IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return long value
     * @throws IllegalArgumentException if the column name is not found
     * @throws NumberFormatException if the column being queried does not
     *             contain a long.
     */
    public static long getLongValue(IList list, String colname) {
        try {
            String s = list.getValue(colname);
            return Long.valueOf(s);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field: " + colname, e);
        }
    }

    /**
     * Return a Date value for a column. The date must be in standard JDBC date
     * format. Wraps possible checked exception NoSuchFieldException with an
     * IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return date value
     */
    public static Date getDateValue(IList list, String colname) {
        String s = getStringValue(list, colname);
        return !Utilities.goodString(s) ? null : Util.parseJdbcDate(s);
    }
}
