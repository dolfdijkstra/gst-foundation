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

package com.fatwire.gst.foundation.facade.runtag.asset;

import static COM.FutureTense.Interfaces.Utilities.goodString;

import java.text.Format;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;

/**
 * Filters assets via startdate/enddate.
 * <p/>
 * NOTE: This class calls a public yet internal function inside the JSP tag. No
 * guarantees can therefore exist as to its compatibility across patch versions.
 * The core function, however, is exceptionally slow to begin with, so caution
 * should be exercised when using this function.
 * <p/>
 * 
 * @author Tony Field
 * @since Jun 23, 2010
 */
public final class FilterAssetsByDate {
    private static final Log LOG = LogFactory.getLog(FilterAssetsByDate.class);

    private static String[] jdbcDateFormatStrings = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS" };
    private static Format jdbcDateFormat = FastDateFormat.getInstance(jdbcDateFormatStrings[0]);

    /**
     * Filter a single asset, checking to see if it's valid on the given date.
     * If no date is specified, then the date used is the one used by the
     * FilterAssetsByDate tag when no parameter is specified
     * 
     * @param ics context
     * @param id input asset
     * @param date override date
     * @return true if the asset is valid, false otherwise.
     */
    public static boolean isValidOnDate(ICS ics, AssetId id, Date date) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Checking to see if asset " + id + " is valid on "
                    + (date == null ? "the site preview date, (assuming site preview is enabled)." : date));
        }
        ics.ClearErrno();

        final String inListName = "FilterAssetByDateInputList-" + ics.genID(false);
        IList inlist = new AssetIdIList(inListName, Collections.singletonList(id));
        ics.RegisterList(inListName, inlist);

        final String outListName = "FilterAssetsByDateOutputList-" + ics.genID(false);

        com.openmarket.xcelerate.jsp.asset.FilterAssetsByDate tag = new com.openmarket.xcelerate.jsp.asset.FilterAssetsByDate();
        tag.setInputList(inListName);
        tag.setOutputList(outListName);
        if (date != null) {
            tag.setDate(jdbcDateFormat.format(date));
        }
        tag.doEndTag(ics, true);
        if (ics.GetErrno() < 0) {
            LOG.warn("Errno set by <asset:filterassetsbydate> JSP tag while attempting to filter asset " + id
                    + " by date: " + date + "(null date is ok). Errno: " + ics.GetErrno());
            // note the above tag behaves erratically and errno is unreliable
            // throw new
            // CSRuntimeException("Unexpected exception filtering assets by date.  Input Asset: "
            // + id + ", date: " + null + " (null is ok)", errno);
        }

        IList out = ics.GetList(outListName);
        ics.RegisterList(outListName, null); // tidy up!

        if (out == null)
            throw new IllegalStateException("Tag executed successfully but no outlist was returned");
        if (!out.hasData()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Asset " + id + " is not valid on the effective date.");
            }
            return false; // no matches
        }
        String c = IListUtils.getStringValue(out, "assettype");
        if (!id.getType().equals(c)) {
            throw new IllegalStateException("Output asset is not the right type: in:" + id + ", out:" + c);
        }
        String cid = IListUtils.getStringValue(out, "assetid");

        boolean result = Utilities.goodString(cid) && id.getId() == Long.parseLong(cid);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Asset " + id + " is " + (result ? "" : "not ") + "valid on the effective date.");
        }
        return result;
    }

    /**
     * Method to check to see if a date falls between two dates. The comparison
     * date is a Date object, or null, in which case the current date is used.
     * The boundary dates are JDBC format dates, and can be null, indication the
     * dates aren't boudned.
     * 
     * @param startDateJdbc start date in jdbc format or null
     * @param effectiveDate comparison date or null to use current date
     * @param endDateJdbc end date in jdbc format
     * @return true if the date is in the valid range; false otherwise.
     */
    public static boolean isDateWithinRange(String startDateJdbc, Date effectiveDate, String endDateJdbc) {
        final Date startDate = goodString(startDateJdbc) ? parseJdbcDate(startDateJdbc) : null;
        final Date endDate = goodString(endDateJdbc) ? parseJdbcDate(endDateJdbc) : null;
        return isDateWithinRange(startDate, effectiveDate, endDate);
    }

    /**
     * Method to check to see if a date falls between two dates. The comparison
     * date is a Date object, or null, in which case the current date is used.
     * The boundary dates can be null, indication the dates aren't bounded.
     * 
     * @param startDate start date or null
     * @param effectiveDate comparison date or null to use current date
     * @param endDate end date or null
     * @return true if the date is in the valid range; false otherwise.
     */
    public static boolean isDateWithinRange(Date startDate, Date effectiveDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return true;
        } else {
            if (effectiveDate == null)
                effectiveDate = new Date();

            if (startDate == null) {
                return effectiveDate.before(endDate) || effectiveDate.equals(endDate);
            } else if (endDate == null) {
                return startDate.before(effectiveDate) || startDate.equals(effectiveDate);
            } else {
                return startDate.before(effectiveDate) && effectiveDate.before(endDate);
            }
        }
    }

    /**
     * Given an input string in JDBC form, parse it and return a date object.
     * 
     * @param string jdbc date string in the form yyyy-MM-dd HH:mm:ss
     * @return Date
     * @throws IllegalArgumentException on failure
     */
    public static Date parseJdbcDate(String string) {
        try {
            return DateUtils.parseDate(string, jdbcDateFormatStrings);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failure parsing string " + string, e);
        }
    }
}
