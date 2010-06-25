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
