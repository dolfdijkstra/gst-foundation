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
 *
 */
public class SqlHelper {

    private static final Log log = LogFactory.getLog(SqlHelper.class);

    private SqlHelper() {
    };

    /**
     * facade over ICS.SQL
     *
     * limit =-1;
     *
     * bCache=true;
     *
     * clears errno before ics.SQL
     *
     * no IList registered in ics variable space
     *
     * @param ics
     * @param table tablename
     * @param sql the sql statement, needs to start with 'select'
     * @return never null, always an IListIterable
     * @throws RuntimeException
     *             if errno is not zero or not -101
     * @see SqlHelper#select(ICS, String, String, int)
     */

    public static final IListIterable select(final ICS ics, final String table,
            final String sql) {
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
    public static final IListIterable select(final ICS ics, final String table,
            final String sql, final int limit) {
        final StringBuffer errstr = new StringBuffer();
        ics.ClearErrno();
        if (sql == null) {
            throw new NullPointerException("sql can not be null");
        }
        if (!sql.toLowerCase().trim().startsWith("select")) {
            throw new IllegalArgumentException("Can only do select statements:"
                    + sql);
        }

        final IList i = ics.SQL(table, sql, null, limit, true, errstr);
        if (ics.GetErrno() == 0) {
        } else if (ics.GetErrno() == -101) {
            ics.ClearErrno();
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno()
                    + " and errstr: '" + errstr.toString() + "' for " + sql);
        }

        return new IListIterable(i);
    }

    /**
     * handles sql statements, other then SELECT statements
     *
     * flushes the table (ics.FlushCatalog()) after the statement execution
     *
     * @param ics
     * @param table tablename
     * @param sql the sql statement, can not start with "select"
     */
    public static final void execute(final ICS ics, final String table,
            final String sql) {
        final StringBuffer errstr = new StringBuffer();
        ics.ClearErrno();
        if (sql == null) {
            throw new NullPointerException("sql can not be null");
        }
        if (sql.toLowerCase().trim().startsWith("select")) {
            throw new IllegalArgumentException("Can not do select statements:"
                    + sql);
        }

        ics.SQL(table, sql, null, -1, false, true, errstr);
        if (ics.GetErrno() == 0) {
            if (ics.FlushCatalog(table)) {
                ics.ClearErrno();
            } else {
                log.warn("Flushing failed for table " + table);
            }
        } else if (ics.GetErrno() == -502) { // update statements do not
            // return an IList, cs signals
            // this via errno -502
            if (ics.FlushCatalog(table)) {
                ics.ClearErrno();
            } else {
                // throw exception??
                log.warn("Flushing failed for table " + table);

                ics.ClearErrno();

            }

        } else {
            log.warn("ics.SQL returned " + ics.GetErrno() + " and errstr: "
                    + errstr.toString() + " for " + sql);
        }
    }
    
    /**
     * Executes a PreparedStatement
     * 
     * @param ics
     * @param stmt the PreparedStatement
     * @param data the statement parameters
     * @return
     */

    public static final IListIterable select(final ICS ics,
            final PreparedStmt stmt, final StatementParam param) {

        final IList i = ics.SQL(stmt, param, true);
        if (ics.GetErrno() == 0) {
            // ok, no worries
        } else if (ics.GetErrno() != -101) { // no rows if fine
            ics.ClearErrno();
        } else {
            throw new RuntimeException("ics.SQL returned " + ics.GetErrno()
                    + " and errstr: " + " for " + stmt.toString());
        }

        return new IListIterable(i);

    }

}
