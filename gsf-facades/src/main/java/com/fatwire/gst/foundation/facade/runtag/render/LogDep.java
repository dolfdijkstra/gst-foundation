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

package com.fatwire.gst.foundation.facade.runtag.render;

import org.apache.commons.lang3.StringUtils;

import COM.FutureTense.Cache.CacheManager;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.openmarket.xcelerate.publish.PubConstants;

/**
 * Wraps the {@code<RENDER.LOGDEP>} xml tag.
 * 
 * @author Tony Field
 * @since 10-Jun-2008
 */
public final class LogDep extends AbstractTagRunner {
    public LogDep() {
        super("RENDER.LOGDEP");
    }

    public static enum DependencyType {
        exact, exists, greater, none
    }

    public void setAsset(String s) {
        set("ASSET", s);
    }

    public void setDeptype(DependencyType deptype) {
        switch (deptype) {
            case exact:
                set("DEPTYPE", "exact");
                break;
            case exists:
                set("DEPTYPE", "exists");
                break;
            case greater:
                set("DEPTYPE", "greater");
                break;
            case none:
                set("DEPTYPE", "none");
                break;
        }
    }

    public void setC(String s) {
        set("c", s);
    }

    public void setCid(String s) {
        set("cid", s);
    }

    public static void logDep(ICS ics, String c, String cid) {

        String rm = StringUtils.defaultString(ics.GetVar(PubConstants.RENDERMODE), PubConstants.LIVE);
        if (rm.startsWith(PubConstants.DEPS) || rm.startsWith(PubConstants.EXPORT)) {
            LogDep ld = new LogDep();
            ld.setC(c);
            ld.setCid(cid);
            ld.execute(ics);
        } else {
            CacheManager.RecordItem(ics, PubConstants.CACHE_PREFIX + cid + PubConstants.SEPARATOR + c);
        }
    }

    public static void logDep(ICS ics, AssetId id) {
        if (ics == null)
            throw new IllegalArgumentException("ics must not be null");
        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        logDep(ics, id.getType(), Long.toString(id.getId()));
    }
}
