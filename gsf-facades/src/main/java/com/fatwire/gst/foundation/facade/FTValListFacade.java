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

package com.fatwire.gst.foundation.facade;

import java.util.Date;

import COM.FutureTense.Interfaces.FTValList;

import com.fatwire.cs.core.db.Util;

/**
 * Base class for all ICS commands that accept an FTValList (like
 * CatalogManager, runTag and CallElement)
 * 
 * Converts the different java types to the correct FTValList type
 * 
 * @author Dolf.Dijkstra
 * 
 */

public class FTValListFacade {

    protected final FTValList list = new FTValList();

    public FTValListFacade() {
        super();
    }

    protected final void set(final String key, final String value) {
        list.setValString(key, value);
    }

    protected final void set(final String key, final boolean value) {
        list.setValString(key, Boolean.toString(value));
    }

    protected final void set(final String key, final int value) {
        list.setValInt(key, value);
    }

    protected final void set(final String key, final byte[] value) {
        list.setValBLOB(key, value);
    }

    protected final void set(final String key, final long value) {
        list.setValString(key, Long.toString(value));
    }

    protected final void set(final String key, final Date value) {
        list.setValString(key, Util.formatJdbcDate(value));
    }

    protected final FTValList getList() {
        return list;
    }

}
