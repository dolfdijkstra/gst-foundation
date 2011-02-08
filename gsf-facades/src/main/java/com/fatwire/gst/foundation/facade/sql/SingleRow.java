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

class SingleRow implements Row {

    private final IList list;

    public SingleRow(final IList list) {
        super();
        this.list = list;
    }

    public byte[] getBytes(final String key) {
        try {
            return (byte[]) list.getObject(key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public char getChar(final String key) {
        try {
            final String s = list.getValue(key);
            if (s != null && s.length() > 0) {
                return s.charAt(0);
            }
            throw new RuntimeException("no value for " + key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getDate(final String key) {
        try {
            final String s = list.getValue(key);
            if (s != null && s.length() > 0) {
                return Util.parseJdbcDate(s);
            }
            throw new RuntimeException("no value for " + key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLong(final String key) {
        try {
            final String s = list.getValue(key);
            if (s != null && s.length() > 0) {
                return Long.parseLong(s);
            }
            throw new RuntimeException("no value for " + key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(final String key) {
        try {
            return list.getValue(key);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
