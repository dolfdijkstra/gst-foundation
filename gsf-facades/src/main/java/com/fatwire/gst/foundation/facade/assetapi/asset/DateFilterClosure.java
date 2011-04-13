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
package com.fatwire.gst.foundation.facade.assetapi.asset;

import java.util.Date;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetClosure;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.openmarket.xcelerate.interfaces.IAsset;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dolf Dijkstra
 * @since Apr 6, 2011
 */
public class DateFilterClosure implements AssetClosure {
    private static Log LOG = LogFactory.getLog(DateFilterClosure.class.getPackage().getName());

    private final Date cutoff;

    private final AssetClosure target;

    private boolean filter = true;

    private static Date parseDate(final String date) {

        return StringUtils.isNotBlank(date) ? com.fatwire.cs.core.db.Util.parseJdbcDate(date) : null;
    }

    /**
     * 
     * 
     * @param ics
     * @param cutoff the cutoff date in the cs date format.
     * @param target
     */
    public DateFilterClosure(final ICS ics, final String cutoff, final AssetClosure target) {
        this(ics, parseDate(cutoff), target);

    }

    public DateFilterClosure(final ICS ics, final Date cuttoff, final AssetClosure target) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        this.target = target;
        this.cutoff = calculateCutOffDate(ics, cuttoff);

    }

    /**
     * @param ics
     * @param cuttoff
     */
    private Date calculateCutOffDate(final ICS ics, final Date cuttoff) {
        if (ics.LoadProperty("futuretense.ini;futuretense_xcel.ini")) {
            if (ftMessage.cm.equals(ics.GetProperty(ftMessage.cssitepreview))) {
                // We disable caching if and ONLY if cs.sitepreview is
                // contentmanagement. Check for that property in the ini files
                ics.DisableFragmentCache();

                // Insite Editing is enabled
                if (null == cuttoff) {
                    return new Date();
                } else {
                    return cuttoff;
                }

            } else if (ftMessage.disabled.equals(ics.GetProperty(ftMessage.cssitepreview))) {
                filter = false;
                return null;
            } else {
                return new Date(); // site preview disabled or delivery,
                // implies production install, use
                // server date
            }

        } else {
            // Cannot read from property file, use server date
            // TODO: isn't ignoring cutoff a better option when prop can't be
            // read??
            return new Date();
        }
    }

    public boolean work(final AssetData assetData) {
        if (filter && cutoff != null) {
            final Date assetStartDate = AttributeDataUtils.asDate(assetData.getAttributeData(IAsset.STARTDATE, true));
            final Date assetEndDate = AttributeDataUtils.asDate(assetData.getAttributeData(IAsset.ENDDATE, true));
            if (LOG.isTraceEnabled())
                LOG.trace("assetStartDate " + assetStartDate + " assetEndDate " + assetEndDate);
            if (assetEndDate != null && assetEndDate.before(cutoff)) {
                // ignore
                return true;
            }
            if (assetStartDate != null && assetStartDate.after(cutoff)) {
                // ignore, assuming that endDate if after startDate
                return true;
            }
        }
        // filter disabled or no date set, just pass-thru
        if (LOG.isDebugEnabled())
            LOG.debug("passing thru " + assetData.getAssetId());
        return target.work(assetData);
    }

}
