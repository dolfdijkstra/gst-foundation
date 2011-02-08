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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;

/**
 * A helper class over <tt>ICS.SQL</tt>
 * 
 * @author Dolf Dijkstra
 * @see ICS#SQL(String, String, String, int, boolean, boolean, StringBuffer)
 */
public class SqlHelper {

    private static final Log log = LogFactory.getLog(SqlHelper.class);

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
        if (ics.GetErrno() == 0) {
        } else if (ics.GetErrno() == -101) {
            ics.ClearErrno();
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " and errstr: '" + errstr.toString()
                    + "' for " + sql);
        }

        return new IListIterable(i);
    }

    /**
     * handles sql statements, other then SELECT statements
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
                log.warn("Flushing failed for table " + table + ". (" + ics.GetErrno() + ")");
                ics.ClearErrno();
            }
        } else if (ics.GetErrno() == -502) { // update statements do not
            // return an IList, cs signals
            // this via errno -502
            if (ics.FlushCatalog(table)) {
                ics.ClearErrno();
            } else {
                // throw exception??
                log.warn("Flushing failed for table " + table + ". (" + ics.GetErrno() + ")");
                ics.ClearErrno();
            }
        } else {
            log.warn("ics.SQL returned " + ics.GetErrno() + " and errstr: " + errstr.toString() + " for " + sql);
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
        if (ics.GetErrno() == 0) {
            // ok, no worries
        } else if (ics.GetErrno() != -101) { // no rows if fine
            ics.ClearErrno();
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " and errstr: " + " for "
                    + stmt.toString());
        }

        return new IListIterable(i);

    }

    /**
     * Executes a PreparedStatement
     * 
     * @param ics
     * @param stmt the PreparedStatement
     * @param param the statement parameters
     * @return Row if resultset is returned, otherwise null
     */

    public static final Row selectSingle(final ICS ics, final PreparedStmt stmt, final StatementParam param) {

        final IList i = ics.SQL(stmt, param, true);
        if (ics.GetErrno() == 0) {
            i.moveTo(IList.first);
            return new SingleRow(i);
        } else if (ics.GetErrno() != -101) { // no rows if fine
            ics.ClearErrno();
            return null;
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno() + " and errstr: " + " for "
                    + stmt.toString());
        }

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

}
