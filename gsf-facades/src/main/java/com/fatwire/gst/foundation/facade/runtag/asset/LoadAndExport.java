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

/**
 * 
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunner;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */

public class LoadAndExport implements TagRunner {

    private final String assetType;

    private final long assetId;

    public LoadAndExport(final String assetType, final long assetId) {
        super();
        this.assetType = assetType;
        this.assetId = assetId;
    }

    public String execute(final ICS ics) {
        final String name = "asset" + ics.genID(true);
        try {
            final AssetLoadById al = new AssetLoadById();
            al.setName(name);
            al.setAssetType(assetType);
            al.setAssetId(assetId);
            // al.setEditable(true);
            al.setOption(AssetLoadById.OPTION_READ_ONLY_COMPLETE);

            al.execute(ics);

            new AssetScatter(name, "as", "PubList").execute(ics);
            new AssetScatter(name, "as", true).execute(ics);
            new AssetExport(name, "as", "xml").execute(ics);

            final String xml = ics.GetVar("xml");
            ics.RemoveVar("xml");
            return xml;
        } finally {
            // cleaning up
            ics.SetObj(name, null);// clear obj from ics
            final List<String> toClean = new ArrayList<String>();
            for (final Enumeration<?> e = ics.GetVars(); e.hasMoreElements();) {
                final String k = (String) e.nextElement();
                if (k.startsWith("as:")) {
                    toClean.add(k);
                }
            }
            // preventing java.util.ConcurrentModificationException
            for (String n : toClean) {
                ics.RemoveVar(n);
            }

        }
    }

    public long getAssetId() {
        return assetId;
    }

    public String getAssetType() {
        return assetType;
    }

}
