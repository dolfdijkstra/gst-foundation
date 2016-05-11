/*
 * Copyright 2012 Oracle Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.mobile.action;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Util.ftErrors;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.controller.AssetIdWithSite;
import com.fatwire.gst.foundation.controller.action.RenderPage;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;

import com.fatwire.gst.foundation.facade.mda.LocaleService;
import com.fatwire.gst.foundation.mobile.DeviceDetector;
import com.fatwire.gst.foundation.mobile.DeviceType;
import com.fatwire.mda.DimensionFilterInstance;

/**
 * RenderPage action extension that handles translations as well as device
 * detection to direct the visitor to the device specific template.
 * <p>
 * The rule is straight forward. If the template is X then a lookup is done if there
 * is a Template with the name X_mobile and that is used if the visitor is using
 * a mobile device. The same for _desktop and _tablet. If such a template does
 * not exist, the 'normal' template is used.
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */
public class DeviceAwareRenderPageAction extends RenderPage {

    static final private Logger log = LoggerFactory.getLogger(DeviceAwareRenderPageAction.class);

    @InjectForRequest
    public DeviceDetector detector;

    @InjectForRequest
    public LocaleService localeService;

    @Override
    protected AssetIdWithSite resolveAssetId() {
        final AssetIdWithSite id = super.resolveAssetId();
        if (id == null || id.getSite() == null) {
            throw new CSRuntimeException("Asset or site not found: '" + id + "' for url " + ics.pageURL(),
                    ftErrors.pagenotfound);
        }
        return findTranslation(id);
    }

    @Override
    protected void callTemplate(AssetIdWithSite id, String tname) {
        DeviceType type = detector.detectDeviceType(ics);
        if (LOG.isDebugEnabled())
            LOG.debug("detected device type: " + type);
        String dtname = checkForDeviceTName(id, tname, type);
        super.callTemplate(id, dtname);

    }

    @Override
    protected void callPage(AssetIdWithSite id, String pagename, String packedArgs) {
        DeviceType type = detector.detectDeviceType(ics);
        if (LOG.isDebugEnabled())
            LOG.debug("detected device type: " + type);
        String nn = checkForDevicePagename(id, pagename, type);
        super.callPage(id, nn, packedArgs);
    }

    protected AssetIdWithSite findTranslation(AssetIdWithSite id) {
        
        if (localeService == null)
            return id;
        AssetIdWithSite n = id;
        DimensionFilterInstance df = localeService.getDimensionFilter(id.getSite());
        if (df != null) {
            AssetId translated = localeService.findTranslation(id, df);
            if (translated != null)
                n = new AssetIdWithSite(translated, id.getSite());

        }
        return n;
    }

    protected String getPostfix(DeviceType type) {
        switch (type) {
            case MOBILE:
                return "_mobile";
            case TABLET:
                return "_tablet";
            default:
                return "_desktop";
        }

    }

    protected String checkForDeviceTName(AssetIdWithSite id, String tname, DeviceType type) {
        if (StringUtils.endsWith(tname, "_mobile") || StringUtils.endsWith(tname, "_tablet")
                || StringUtils.endsWith(tname, "_desktop")) {
            return tname;
        }
        String pf = getPostfix(type);
        final String targetPagename = tname.startsWith("/") ? (id.getSite() + tname + pf) : (id.getSite() + "/"
                + id.getType() + "/" + tname + pf);
        try {
            if (ics.getPageData(targetPagename).isRegistered()) {
                return tname + pf;
            } else if (LOG.isTraceEnabled()) {
                log.trace("There is no special template for " + type + " at template " + tname);
            }
        } catch (IllegalArgumentException e) {
            LOG.warn(e.getMessage());
            // ignore
        }

        return tname;
    }

    protected String checkForDevicePagename(AssetIdWithSite id, String pagename, DeviceType type) {
        if (StringUtils.endsWith(pagename, "_mobile") || StringUtils.endsWith(pagename, "_tablet")
                || StringUtils.endsWith(pagename, "_desktop")) {
            return pagename;
        }
        String pf = getPostfix(type);
        final String targetPagename = pagename + pf;

        try {

            if (ics.getPageData(targetPagename).isRegistered()) {
                return targetPagename;
            } else if (LOG.isTraceEnabled()) {
                log.trace("There is no device specific pagename for " + type + " at page " + pagename);
            }
        } catch (NullPointerException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            LOG.warn(e.getMessage());
            // ignore
        }
        return pagename;
    }
}
