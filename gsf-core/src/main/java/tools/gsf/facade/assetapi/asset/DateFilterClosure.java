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
package tools.gsf.facade.assetapi.asset;

import com.fatwire.assetapi.data.AssetData;
import com.openmarket.xcelerate.interfaces.IAsset;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.gsf.facade.assetapi.AssetClosure;
import tools.gsf.facade.assetapi.AttributeDataUtils;

import java.util.Date;

/**
 * @author Dolf Dijkstra
 * @since Apr 6, 2011
 */
public class DateFilterClosure implements AssetClosure {
    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.facade.assetapi.asset.DateFilterClosure");

    private final Date cutoff;

    private final AssetClosure target;

    private static Date parseDate(final String date) {

        return StringUtils.isNotBlank(date) ? com.fatwire.cs.core.db.Util.parseJdbcDate(date) : null;
    }

    /**
     * @param cutoff the cutoff date in the cs date format.
     * @param target target asset closure
     */
    public DateFilterClosure(final String cutoff, final AssetClosure target) {
        this(parseDate(cutoff), target);

    }

    /**
     * @param cuttoff cut off date
     * @param target  target asset closure
     */
    public DateFilterClosure(final Date cuttoff, final AssetClosure target) {
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null");
        }
        this.target = target;
        this.cutoff = cuttoff == null ? null : new Date(cuttoff.getTime());

    }

    public boolean work(final AssetData assetData) {
        if (cutoff != null) {
            final Date assetStartDate = AttributeDataUtils.asDate(assetData.getAttributeData(IAsset.STARTDATE, true));
            final Date assetEndDate = AttributeDataUtils.asDate(assetData.getAttributeData(IAsset.ENDDATE, true));
            if (LOG.isTraceEnabled()) {
                LOG.trace("assetStartDate " + assetStartDate + " assetEndDate " + assetEndDate);
            }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("passing thru " + assetData.getAssetId());
        }
        return target.work(assetData);
    }

}
