/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import java.util.Date;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Filters assets via startdate/enddate.
 *
 * This class is not yet complete and offers only basic functionality.
 *
 * TODO: implement core functionality for ASSET:FILTERASSETSBYDATE
 *
 * @author Tony Field
 * @since Jun 23, 2010
 */
public final class FilterAssetsByDate {
    private static final String STARTDATE = "startdate";
    private static final String ENDDATE = "enddate";

    public static boolean isValidOnDate(AssetId id, Date date)
    {
        AssetData d = AssetDataUtils.getAssetData(id, STARTDATE, ENDDATE);
        String sStartdate = d.getAttributeData(STARTDATE) != null ? d.getAttributeData(STARTDATE).toString() : null;
        Date startDate = goodString(sStartdate)? Util.parseJdbcDate(sStartdate):null;
        String sEnddate = d.getAttributeData(ENDDATE) != null ? d.getAttributeData(ENDDATE).toString() : null;
        Date endDate = goodString(sEnddate)? Util.parseJdbcDate(sEnddate) : null;

        if (startDate == null && endDate == null)
        {
            return true;
        }
        else
        {
            Date effectiveDate = date == null ? new Date() : date;
            if (startDate == null)
            {
                return effectiveDate.before(endDate);
            }
            else if (endDate == null)
            {
                return startDate.before(effectiveDate);
            }
            else
            {
                return startDate.before(effectiveDate) && effectiveDate.before(endDate);
            }
        }
    }
}
