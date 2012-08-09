/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.url.db;

import java.util.Date;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.facade.logging.LogUtil;
import com.fatwire.gst.foundation.facade.runtag.asset.FilterAssetsByDate;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.vwebroot.VirtualWebroot;
import com.fatwire.gst.foundation.vwebroot.VirtualWebrootApiBypassDao;
import com.fatwire.gst.foundation.vwebroot.VirtualWebrootDao;
import com.fatwire.gst.foundation.wra.SimpleWRADao;
import com.fatwire.gst.foundation.wra.SimpleWra;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

/**
 * WraPathTranslationService that is backed by the GSTUrlRegistry table.
 * 
 * @author Dolf Dijkstra
 * @since Jun 17, 2010
 */

public class UrlRegistry2 implements WraPathTranslationService {
    private static final Log LOG = LogUtil.getLog(UrlRegistry2.class);

    private final ICS ics;
    private final SimpleWRADao wraDao;
    private final VirtualWebrootDao vwDao;
    private final UrlRegistryDao regDao;

    public UrlRegistry2(final ICS ics, final SimpleWRADao wraDao, final VirtualWebrootDao vwDao,
            final UrlRegistryDao regDao) {
        this.ics = ics;
        this.wraDao = wraDao;
        this.vwDao = vwDao;
        this.regDao = regDao;
    }

    @Override
    public AssetIdWithSite resolveAsset(final String virtual_webroot, final String url_path) {
        for (final VanityUrl asset : regDao.resolveAsset(virtual_webroot, url_path)) {
            final String assettype = asset.getAssettype();
            final long assetid = asset.getAssetid();
            final AssetIdWithSite id = new AssetIdWithSite(assettype, assetid, asset.getOpt_site());
            if (FilterAssetsByDate.isValidOnDate(ics, id, null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved and validated effective date for asset " + id + " from virtual-webroot:"
                            + virtual_webroot + " and url-path:" + url_path);
                }
                return id;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved asset "
                            + id
                            + " but it is not valid on the effective date as determined by the asset.filterassetsbydate tag.");
                }
            }
        }

        return null;
    }

    @Override
    public void addAsset(final AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("addAsset(AssetId) called for asset " + id);
        }
        updateAsset_(id);
    }

    @Override
    public void updateAsset(final AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("updateAsset(AssetId) called for asset " + id);
        }

        updateAsset_(id);
    }

    @Override
    public void deleteAsset(final AssetId id) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("deleteAsset(AssetId) called for asset " + id);
        }
        regDao.delete(id);
    }

    private void addAsset_(final SimpleWra wra) {
        final AssetId asset = wra.getId();

        final VirtualWebroot vw = vwDao.lookupVirtualWebrootForUri(wra.getPath());

        if (vw != null) {
            final String site = wraDao.resolveSite(asset);
            regDao.add(wra, vw, site);

        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Did not add WRA " + asset + " to url registry because no valid virtual webroot was found");
            }
        }

    }

    private boolean compare(final Date d1, final Date d2) {
        if (d1 == null && d2 == null) {
            return true;
        }

        if (d1 == null && d2 != null) {
            return false;
        }
        return d1.equals(d2);

    }

    private boolean compare(final VanityUrl url, final SimpleWra wra) {
        // path, startdate && enddate all the same?
        if (url == null) {
            return false;
        }
        if (wra == null) {
            return false;
            // path cannot be null
        }

        if (wra.getPath().equals(url.getPath()) == false) {
            return false;
        }

        if (compare(wra.getEndDate(), url.getEnddate()) == false) {
            return false;
        }
        if (compare(wra.getStartDate(), url.getStartdate()) == false) {
            return false;
        }

        return true;
    }

    public void updateAsset_(final AssetId id) {
        /*
         * here is the optimization question, what is faster, the select on the
         * urlregtable or on the assettable? Probably it depends on number rows
         * in table. If urlregtable is smaller than individual assettype table
         * the lookup for registry table first is probably preferred. Otherwise,
         * change to logic below to first lookup the wraDao.getWra().
         */
        final SimpleWra wra = wraDao.getWra(id);
        if (isWra(wra)) {
            final VanityUrl row = regDao.read(id);
            if (!compare(row, wra)) {
                regDao.delete(id);
                addAsset_(wra);
            } else if (row == null) {
                addAsset_(wra);
            }
        } else {
            regDao.delete(id);
        }

    }

    private boolean isWra(final SimpleWra wra) {
        return wra != null && StringUtils.isNotBlank(wra.getPath()) && StringUtils.isNotBlank(wra.getTemplate());
    }

    public static WraPathTranslationService lookup(final ICS ics) {
        final Object o = ics.GetObj(UrlRegistry2.class.getName());
        if (o instanceof UrlRegistry2) {
            return (UrlRegistry2) o;
        }

        final UrlRegistry2 x = new UrlRegistry2(ics, new DbSimpleWRADao(ics), new VirtualWebrootApiBypassDao(ics),
                new UrlRegistryDaoImpl(ics));

        ics.SetObj(UrlRegistry2.class.getName(), x);
        return x;
    }

}
