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

package com.fatwire.gst.foundation.facade.sql;

import java.util.Date;

import COM.FutureTense.Interfaces.IList;

import com.fatwire.cs.core.db.Util;

import org.apache.commons.lang.StringUtils;

/**
 * Implements a Row.
 * 
 * @author Dolf Dijkstra
 * 
 */
class SingleRow implements Row {

    private final IList list;

    /**
     * @param list
     */
    public SingleRow(final IList list) {
        super();
        this.list = list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.facade.sql.Row#getBytes(java.lang.String)
     */
    public byte[] getBytes(final String key) {
        try {
            return (byte[]) list.getObject(key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.facade.sql.Row#getChar(java.lang.String)
     */
    public Character getChar(final String key) {
        try {
            final String s = list.getValue(key);
            if (StringUtils.isNotBlank(s)) {
                return s.charAt(0);
            }
            return null;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.facade.sql.Row#getDate(java.lang.String)
     */
    public Date getDate(final String key) {
        try {
            final String s = list.getValue(key);
            if (StringUtils.isNotBlank(s)) {
                return Util.parseJdbcDate(s);
            }
            return null;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fatwire.gst.foundation.facade.sql.Row#getLong(java.lang.String)
     */
    public Long getLong(final String key) {
        try {
            final String s = list.getValue(key);
            if (StringUtils.isNotBlank(s)) {
                return Long.parseLong(s);
            }
            throw null;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.facade.sql.Row#getString(java.lang.String)
     */
    public String getString(final String key) {
        try {
            return list.getValue(key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param key
     * @return
     */
    public boolean isField(String key) {
        for (int i = 0; i < list.numColumns(); i++) {
            if (key.equalsIgnoreCase(list.getColumnName(i))) {
                return true;
            }

        }
        return false;
    }

    @Override
    public Integer getInt(String key) {
        try {
            final String s = list.getValue(key);
            if (StringUtils.isNotBlank(s)) {
                return Integer.parseInt(s);
            }
            throw null;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
}
