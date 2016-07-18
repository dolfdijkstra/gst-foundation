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
package com.fatwire.gst.foundation.wra;

import java.util.Date;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.sql.Row;

/**
 * @author Dolf Dijkstra
 *
 *
 * @deprecated as of release 12.x, replace with WCS 12c's native vanity URLs support.
 *
 */
public class SimpleWra {
    private final Row row;
    private final AssetId id;

    public SimpleWra(final Row row, final AssetId id) {
        this.row = row;
        this.id = id;
    }

    public AssetId getId() {
        return id;
    }

    public String getPath() {
        return row.getString("path");
    }

    public Date getStartDate() {
        return row.getDate("startdate");
    }

    public Date getEndDate() {
        return row.getDate("enddate");
    }

    public String getTemplate() {
        return row.getString("template");
    }

    @Override
    public String toString() {
        return "SimpleWra [id=" + id + "]";
    }

}
