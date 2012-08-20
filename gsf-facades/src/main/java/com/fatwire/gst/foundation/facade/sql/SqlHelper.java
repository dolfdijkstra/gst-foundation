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

package com.fatwire.gst.foundation.facade.sql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Iterator;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A helper class over <tt>ICS.SQL</tt>
 * 
 * @author Dolf Dijkstra
 * @see ICS#SQL(String, String, String, int, boolean, boolean, StringBuffer)
 */
public class SqlHelper {

    private static final Log LOG = LogFactory.getLog(SqlHelper.class.getPackage().getName());

    private SqlHelper() {
    }

    /**
     * facade over ICS.SQL
     * <p/>
     * limit =-1;
     * <p/>
     * bCache=true;
     * <p/>
     * clears errno before ics.SQL
     * <p/>
     * no IList registered in ics variable space
     * 
     * @param ics
     * @param table tablename
     * @param sql the sql statement, needs to start with 'select'
     * @return never null, always an IListIterable
     * @throws RuntimeException if errno is not zero or not -101
     * @see SqlHelper#select(ICS, String, String, int)
     */

    public static final IListIterable select(final ICS ics, final String table, final String sql) {
        return select(ics, table, sql, -1);
    }

    /**
     * Executes an ICS.SQL operation with a limit.
     * 
     * @param ics
     * @param table tablename
     * @param sql the sql statement, needs to start with 'select'
     * @param limit maximum number of rows to return
     * @return never null, always an IListIterable
     * @see ICS#SQL(String, String, String, int, boolean, StringBuffer)
     */
    public static final IListIterable select(final ICS ics, final String table, final String sql, final int limit) {
        final StringBuffer errstr = new StringBuffer();
        ics.ClearErrno();
        if (sql == null) {
            throw new NullPointerException("sql can not be null");
        }
        if (!sql.toLowerCase().trim().startsWith("select")) {
            throw new IllegalArgumentException("Can only do select statements:" + sql);
        }

        final IList i = ics.SQL(table, sql, null, limit, true, errstr);
        if (ics.GetErrno() == -101) {
            ics.ClearErrno();
        } else if (ics.GetErrno() != 0) {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " and errstr: '" + errstr.toString()
                    + "' for " + sql);
        }

        return new IListIterable(i);
    }

    /**
     * Executes sql statements, other then SELECT statements.
     * <p/>
     * flushes the table (ics.FlushCatalog()) after the statement execution
     * 
     * @param ics
     * @param table tablename
     * @param sql the sql statement, can not start with "select"
     */
    public static final void execute(final ICS ics, final String table, final String sql) {
        final StringBuffer errstr = new StringBuffer();
        ics.ClearErrno();
        if (sql == null) {
            throw new NullPointerException("sql can not be null");
        }
        if (sql.toLowerCase().trim().startsWith("select")) {
            throw new IllegalArgumentException("Can not do select statements:" + sql);
        }

        ics.SQL(table, sql, null, -1, false, true, errstr);
        if (ics.GetErrno() == 0) {
            if (ics.FlushCatalog(table)) {
                ics.ClearErrno();
            } else {
                LOG.warn("Flushing failed for table " + table + ". (" + ics.GetErrno() + ")");
                ics.ClearErrno();
            }
        } else if (ics.GetErrno() == -502) { // update statements do not
            // return an IList, cs signals
            // this via errno -502
            if (ics.FlushCatalog(table)) {
                ics.ClearErrno();
            } else {
                // throw exception??
                LOG.warn("Flushing failed for table " + table + ". (" + ics.GetErrno() + ")");
                ics.ClearErrno();
            }
        } else {
            LOG.warn("ics.SQL returned " + ics.GetErrno() + " and errstr: " + errstr.toString() + " for " + sql);
        }
    }

    /**
     * Executes a PreparedStatement
     * 
     * @param ics
     * @param stmt the PreparedStatement
     * @param param the statement parameters
     * @return never null, always an IListIterable
     */

    public static final IListIterable select(final ICS ics, final PreparedStmt stmt, final StatementParam param) {

        final IList i = ics.SQL(stmt, param, true);
        if (ics.GetErrno() != -101) { // no rows if fine
            ics.ClearErrno();
        } else if (ics.GetErrno() != 0) {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " and errstr: " + " for "
                    + stmt.toString());
        }

        return new IListIterable(i);

    }

    /**
     * Executes a PreparedStatement in a simple form. The values are simply
     * mapped based on order and type to prepared statement parameters.
     * 
     * @param ics
     * @param table tablename
     * @param sql the sql statement
     * @param value the values for the prepared statement parameters.
     * @return never null, always an IListIterable
     */

    public static final IListIterable selectSimplePrepared(final ICS ics, String table, String sql, Object... value) {

        final PreparedStmt stmt = new PreparedStmt(sql, Collections.singletonList(table));
        for (int i = 0; value != null && i < value.length; i++) {
            stmt.setElement(i, toJdbcType(value[i]));
        }
        final StatementParam param = stmt.newParam();
        for (int i = 0; value != null && i < value.length; i++) {
            Object o = value[i];

            if (o instanceof String) {
                param.setString(i, (String) o);
            } else if (o instanceof BigDecimal) {
                param.setBigDecimal(i, (BigDecimal) o);
            } else if (o instanceof Boolean) {
                param.setBoolean(i, (Boolean) o);
            } else if (o instanceof Integer) {
                param.setInt(i, (Integer) o);
            } else if (o instanceof Long) {
                param.setLong(i, (Long) o);
            } else if (o instanceof Float) {
                param.setFloat(i, (Float) o);
            } else if (o instanceof Double) {
                param.setDouble(i, (Double) o);
            } else if (o instanceof Byte) {
                param.setByte(i, (Byte)o);
            } else if (o instanceof java.sql.Date) {
                param.setDate(i, (Date) o);
            } else if (o instanceof java.sql.Time) {
                param.setTime(i, (Time) o);
            } else if (o instanceof java.sql.Timestamp) {
                param.setTimeStamp(i, (Timestamp) o);
            } else if (o instanceof Clob) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            } else if (o instanceof Blob) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            }else if (o.getClass().isArray()){
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            } else if (o instanceof Array) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            } else if (o instanceof Struct) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            } else if (o instanceof Ref) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            } else if (o instanceof java.net.URL) {
                throw new IllegalArgumentException("Can't search for " + o.getClass().getName());
            }
        }
        return select(ics, stmt, param);

    }

    private static int toJdbcType(Object o) {
        if (o instanceof String) {
            return Types.VARCHAR;
        } else if (o instanceof java.math.BigDecimal) {
            return Types.NUMERIC;
        } else if (o instanceof Boolean) {
            return Types.BOOLEAN;
        } else if (o instanceof Integer) {
            return Types.INTEGER;
        } else if (o instanceof Long) {
            return Types.BIGINT;
        } else if (o instanceof Float) {
            return Types.REAL;
        } else if (o instanceof Double) {
            return Types.DOUBLE;
        } else if (o instanceof byte[]) {
            return Types.LONGVARBINARY;
        } else if (o instanceof java.sql.Date) {
            return Types.DATE;
        } else if (o instanceof java.sql.Time) {
            return Types.TIME;
        } else if (o instanceof java.sql.Timestamp) {
            return Types.TIMESTAMP;
        } else if (o instanceof Clob) {
            return Types.CLOB;
        } else if (o instanceof Blob) {
            return Types.BLOB;
        } else if (o instanceof Array) {
            return Types.ARRAY;
        } else if (o instanceof Struct) {
            return Types.STRUCT;
        } else if (o instanceof Ref) {
            return Types.REF;
        } else if (o instanceof java.net.URL) {
            return Types.DATALINK;
        } else {
            throw new IllegalArgumentException(o == null ? "o must not be nulll" : "Can't handle type "
                    + o.getClass().getName());
        }

    }

    /**
     * Executes a PreparedStatement, returning a single row
     * 
     * @param ics the Content Server context
     * @param stmt the PreparedStatement
     * @param param the statement parameters
     * @return Row if resultset is returned, otherwise null
     */

    public static final Row selectSingle(final ICS ics, final PreparedStmt stmt, final StatementParam param) {
        ics.ClearErrno();
        final IList i = ics.SQL(stmt, param, true);
        if (ics.GetErrno() == 0) {
            return new IListIterable(i).iterator().next();
        } else if (ics.GetErrno() == -101) { // no rows is fine
            ics.ClearErrno();
            return null;
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " for " + stmt.toString());
        }

    }

    /**
     * Executes an ICS.SQL operation, returning a single Row, or null if no
     * result was returned by ICS.SQL.
     * 
     * @param ics
     * @param table tablename
     * @param sql the sql statement, needs to start with 'select'
     * @return Row if resultset is returned, otherwise null
     */
    public static final Row selectSingle(final ICS ics, final String table, String sql) {

        Iterator<Row> i = select(ics, table, sql, 1).iterator();
        return i.hasNext() ? i.next() : null;

    }

    /**
     * Quote a string for use in a SQL statement.
     * 
     * @param s string to quote
     * @return quoted string. Null strings are returned simply as ''.
     */
    public static final String quote(final String s) {
        if (s == null || s.length() == 0) {
            return "''";
        }
        return "'" + s.replace("'", "''") + "'";
    }

    public static boolean tableExists(final ICS ics, final String table) {
            ics.CatalogDef(table, null, new StringBuffer());
            return ics.GetErrno() == 0;
    }

}
