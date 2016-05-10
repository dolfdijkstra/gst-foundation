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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetAccessTemplate;
import com.fatwire.gst.foundation.facade.assetapi.AssetClosure;

/**
 * Iterable that filters a list of assetids based on a future date. It makes use
 * of a DateFilterClosure.
 * 
 * @author Dolf.Dijkstra
 * @since Apr 20, 2011
 * @see DateFilterClosure
 */
public class AssetFilterIterator implements Iterable<AssetId> {

    final Iterable<AssetId> i;

    /**
     * @param aat asset access template
     * @param assetIds collection of AssetIds
     */
    public AssetFilterIterator(final AssetAccessTemplate aat, final Iterable<AssetId> assetIds) {
        this(aat, new Date(), assetIds);

    }

    /**
     * @param aat asset access template
     * @param date date
     * @param assetIds collection of AssetIds
     */

    public AssetFilterIterator(final AssetAccessTemplate aat, final Date date, final Iterable<AssetId> assetIds) {
        super();

        i = date == null ? assetIds : toIterable(aat, date, assetIds);
    }

    private Iterable<AssetId> toIterable(final AssetAccessTemplate aat, final Date date,
            final Iterable<AssetId> assetIds) {
        final List<AssetId> list = new LinkedList<AssetId>();
        final AssetClosure target = new AssetClosure() {
            public boolean work(final AssetData asset) {
                list.add(asset.getAssetId());
                return true;
            }
        };
        final AssetClosure closure = new DateFilterClosure(date, target);

        aat.readAsset(assetIds, closure, "startdate", "enddate");
        return list;
    }

    public Iterator<AssetId> iterator() {
        return i.iterator();
    }
}
