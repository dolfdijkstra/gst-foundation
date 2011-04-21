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

package com.fatwire.gst.foundation;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Interfaces.Utilities;
import COM.FutureTense.Util.IterableIListWrapper;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.Util;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Miscellaneous IList Utilities
 * 
 * @author Tony Field
 * @since 21-Nov-2008
 */
public final class IListUtils {
    private IListUtils() {
    }

    /**
     * Return a string value for a column. Wraps the possible checked exception
     * NoSuchFieldException with an IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return string value
     * @throws IllegalArgumentException if the column name is not found
     */
    public static String getStringValue(IList list, String colname) {
        try {
            return list.getValue(colname);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field: " + colname, e);
        }
    }

    /**
     * Return a long value for a column. Wraps the possible checked exception
     * NoSuchFieldException with an IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return long value
     * @throws IllegalArgumentException if the column name is not found
     * @throws NumberFormatException if the column being queried does not
     *             contain a long.
     */
    public static long getLongValue(IList list, String colname) {
        try {
            String s = list.getValue(colname);
            return Long.valueOf(s);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field: " + colname, e);
        }
    }

    /**
     * Return a Date value for a column. The date must be in standard JDBC date
     * format. Wraps possible checked exception NoSuchFieldException with an
     * IllegalArgumentException
     * 
     * @param list IList to interrogate
     * @param colname name of column to return
     * @return date value
     */
    public static Date getDateValue(IList list, String colname) {
        String s = getStringValue(list, colname);
        return !Utilities.goodString(s) ? null : Util.parseJdbcDate(s);
    }

    private static final ThreadLocal<Long> counter = new ThreadLocal<Long>() {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.ThreadLocal#get()
         */
        @Override
        public Long get() {
            long c = super.get();
            c++;
            set(c);
            return c;

        }
    };

    /**
     * Generates a random String to assign to a IList name. It is optimized for
     * performance.
     * 
     * @return String that can be used for an IList name.
     */
    public static String generateRandomListName() {
        return generateRandomListName("rnd-");
    }

    /**
     * Generates a random String to assign to a IList name. It is optimized for
     * performance.
     * 
     * @param prefix the prefix to assign to the list name, cannot be empty or
     *            null.
     * @return
     */
    public static String generateRandomListName(String prefix) {
        if (StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not be blank.");
        return prefix + counter.get();
    }

    /**
     * Reads an IList with collumns <tt>assettype</tt> and <tt>assetid</tt> and
     * creates a Collection of AssetId objects.
     * 
     * @param result
     * @return Collection of AssetIds, never null.
     */
    public static Collection<AssetId> transformToAssetIds(IList result) {
        if (result == null || !result.hasData())
            return Collections.emptyList();
        final List<AssetId> list = new LinkedList<AssetId>();
        for (IList row : new IterableIListWrapper(result)) {
            AssetId id;
            try {
                id = AssetIdUtils.createAssetId(row.getValue("assettype"), row.getValue("assetid"));
                list.add(id);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return list;
    }

}
