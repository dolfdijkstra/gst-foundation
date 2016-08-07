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

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdIList;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.asset.PreviewContext;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.openmarket.xcelerate.publish.PubConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Filters assets via startdate/enddate.
 * <p>
 * NOTE: This class calls a public yet internal function inside the JSP tag. No
 * guarantees can therefore exist as to its compatibility across patch versions.
 * The core function, however, is exceptionally slow to begin with, so caution
 * should be exercised when using this function.
 * <p>
 *
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 23, 2010
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class FilterAssetsByDate {
    private static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.runtag.asset.FilterAssetsByDate");

    private static String[] jdbcDateFormatStrings = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS"};

    /**
     * Filter a single asset, checking to see if it's valid on the given date.
     * If no date is specified, then the date used is the one used by the
     * FilterAssetsByDate tag when no parameter is specified
     *
     * @param ics  context
     * @param id   input asset
     * @param date override date
     * @return true if the asset is valid, false otherwise.
     */
    public static boolean isValidOnDate(ICS ics, AssetId id, Date date) {

        AssetId[] out = filter(ics, date, id);

        if (out == null) {
            throw new IllegalStateException("Tag executed successfully but no outlist was returned");
        }
        if (out.length == 0) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Asset " + id + " is not valid on the effective date.");
            }
            return false; // no matches
        }
        String c = out[0].getType();
        if (!id.getType().equals(c)) {
            throw new IllegalStateException("Output asset is not the right type: in:" + id + ", out:" + c);
        }
        long cid = out[0].getId();

        boolean result = id.getId() == cid;

        if (LOG.isTraceEnabled()) {
            LOG.trace("Asset " + id + " is " + (result ? "" : "not ") + "valid on the effective date.");
        }
        return result;
    }

    /**
     * Filter a array of asset, checking to see if they're valid on the given
     * date. If no date is specified, then the date used is the one used by the
     * FilterAssetsByDate tag when no parameter is specified
     *
     * @param ics  context
     * @param date override date
     * @param id   array of assetids
     * @return the array of asset filtered for the date.
     */
    public static AssetId[] filter(ICS ics, Date date, AssetId... id) {
        Collection<AssetId> ret = filter(ics, date, Arrays.asList(id));
        return ret.toArray(new AssetId[ret.size()]);
    }

    /**
     * Filter a collection of assets, checking to see if they're valid on the
     * given date. If no date is specified, then the date used is the one used
     * by the FilterAssetsByDate tag when no parameter is specified
     *
     * @param ics  ics context
     * @param date override date
     * @param list Collection of assetids.
     * @return the Collection of asset filtered for the date.
     */
    public static Collection<AssetId> filter(ICS ics, Date date, Collection<AssetId> list) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Checking to see if asset " + list + " is valid on "
                    + (date == null ? "the site preview date, (assuming site preview is enabled)." : date));
        }
        ics.ClearErrno();

        final String inListName = IListUtils.generateRandomListName("FilterAssetByDateInputList-");
        IList inlist = new AssetIdIList(inListName, list);

        final String outListName = IListUtils.generateRandomListName("FilterAssetsByDateOutputList-");

        String sdate = date == null ? null : com.fatwire.cs.core.db.Util.formatJdbcDate(date);
        int i = com.openmarket.xcelerate.jsp.asset.FilterAssetsByDate.filter(inlist, outListName, sdate, ics);
        if (i < 0 && i != -500) {
            LOG.info("Errno set by com.openmarket.xcelerate.jsp.asset.FilterAssetsByDate.filter() while attempting to filter assets "
                    + list + " by date: " + sdate + "(null date is ok). Errno: " + ics.GetErrno());
            // note the above tag behaves erratically and errno is unreliable
        }
        ics.ClearErrno();
        IList out = ics.GetList(outListName);
        ics.RegisterList(outListName, null); // tidy up!

        if (out == null) {
            throw new IllegalStateException("Tag executed successfully but no outlist was returned");
        }

        List<AssetId> olist = new ArrayList<AssetId>();

        boolean previewEnabled = PreviewContext.isSitePreviewEnabled(ics);

        for (Row row : new IListIterable(out)) {
            AssetId id = AssetIdUtils.createAssetId(row.getString("assettype"), row.getLong("assetid"));
            olist.add(id);
            if (previewEnabled) {
                logDependancy(ics, id);
            }
        }

        return olist;
    }

    static private void logDependancy(ICS ics, AssetId id) {

        if (PreviewContext.isSitePreviewDelivery(ics)) {
            final AssetData pageData = AssetDataUtils.getAssetData(ics, id, "startdate", "enddate");

            if (pageData != null) {
                String sdate = AttributeDataUtils.asString(pageData.getAttributeData("startdate"));
                String edate = AttributeDataUtils.asString(pageData.getAttributeData("enddate"));

                if (StringUtils.isNotBlank(sdate) || StringUtils.isNotBlank(edate)) {
                    CacheManager.RecordItem(ics,
                            PubConstants.CACHE_PREFIX + id.getId() + PubConstants.SEPARATOR + id.getType(), null,
                            sdate, edate);
                } else {
                    CacheManager.RecordItem(ics,
                            PubConstants.CACHE_PREFIX + id.getId() + PubConstants.SEPARATOR + id.getType());
                }
            }
        } else {
            CacheManager
                    .RecordItem(ics, PubConstants.CACHE_PREFIX + id.getId() + PubConstants.SEPARATOR + id.getType());
        }
        ics.ClearErrno();

    }

    /**
     * Method to check to see if a date falls between two dates. The comparison
     * date is a Date object, or null, in which case the current date is used.
     * The boundary dates are JDBC format dates, and can be null, indication the
     * dates aren't boudned.
     *
     * @param startDateJdbc start date in jdbc format or null
     * @param effectiveDate comparison date or null to use current date
     * @param endDateJdbc   end date in jdbc format
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
     * @param startDate     start date or null
     * @param effectiveDate comparison date or null to use current date
     * @param endDate       end date or null
     * @return true if the date is in the valid range; false otherwise.
     */
    public static boolean isDateWithinRange(Date startDate, Date effectiveDate, Date endDate) {
        if (startDate == null && endDate == null) {
            return true;
        } else {
            if (effectiveDate == null) {
                effectiveDate = new Date();
            }

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
