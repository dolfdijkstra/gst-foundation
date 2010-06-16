package com.fatwire.gst.foundation.facade.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;

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
     * @param table
     * @param sql
     * @return never null, always an IListIterable
     * @throws RuntimeException
     *             if errno is not zero or not -101
     * @see ICS.SQL
     */

    public static final IListIterable select(final ICS ics, final String table,
            final String sql) {
        return select(ics, table, sql, -1);
    }

    /**
     *
     *
     * @param ics
     * @param table
     * @param sql
     * @param limit
     * @return
     * @see SqlHepler.select
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
     * @param table
     * @param sql
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

    public static final IListIterable select(final ICS ics,
            final PreparedStmt stmt, final StatementParam data) {

        final IList i = ics.SQL(stmt, data, true);
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
