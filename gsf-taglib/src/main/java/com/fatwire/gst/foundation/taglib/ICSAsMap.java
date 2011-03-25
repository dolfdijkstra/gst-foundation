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

package com.fatwire.gst.foundation.taglib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import COM.FutureTense.Interfaces.FTVAL;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

/**
 * 
 * This class represents all the ics varariables, objects and lists as a map. It
 * exposes the ics vars to an expression language, liek JSP EL.
 * 
 * @author Dolf.Dijkstra
 * 
 */
public class ICSAsMap implements Map<String, Object> {

    private final ICS ics;

    /**
     * @param ics
     */
    public ICSAsMap(final ICS ics) {
        super();
        this.ics = ics;
    }

    public void clear() {

    }

    public boolean containsKey(final Object key) {
        return ics.GetVar((String) key) != null;

    }

    public boolean containsValue(final Object value) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        final Map<String, Object> tmp = new HashMap<String, Object>();
        for (final Enumeration<String> enumeration = ics.GetVars(); enumeration.hasMoreElements();) {
            final String name = enumeration.nextElement();
            tmp.put(name, get(name));
        }

        return tmp.entrySet();
    }

    @SuppressWarnings("deprecation")
    public Object get(final Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("key must be a String");
        }
        // first: check the object pool
        final Object o = ics.GetObj((String) key);
        if (o != null) {
            return o;
        }

        // second: lists

        final IList i = ics.GetList((String) key, true);
        if (i != null) {
            // TODO: medium transform into List/Map like
            return i;
        }

        // FTVAL mungo jungo
        final FTVAL val = ics.GetCgi((String) key);
        if (val != null) {
            switch (val.GetType()) {
                case FTVAL.BLOB:
                    return val.getBlob();
                case FTVAL.I4:
                    return val.GetInt();
                case FTVAL.DATE:
                case FTVAL.DOUBLE:
                case FTVAL.LONG:
                case FTVAL.LPSTR:
                    return val.getString();
                case FTVAL.UNKNOWN:
                    return val.GetObj();

            }
        }
        return null;
    }

    public boolean isEmpty() {
        return ics.GetVars().hasMoreElements();
    }

    @SuppressWarnings("unchecked")
    public Set<String> keySet() {
        return new HashSet<String>(Collections.list(ics.GetVars()));
    }

    public Object put(final String key, final Object value) {
        // TODO implement??
        // ics.SetVar(key,value);
        // ics.SetVar(key, new FTVAL(value));
        throw new UnsupportedOperationException();
    }

    public void putAll(final Map<? extends String, ? extends Object> t) {
        throw new UnsupportedOperationException();
    }

    public String remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public int size() {
        return Collections.list(ics.GetVars()).size();
    }

    public Collection<Object> values() {
        final Collection<Object> tmp = new ArrayList<Object>();
        for (@SuppressWarnings("unchecked")
        final Enumeration<String> enumeration = ics.GetVars(); enumeration.hasMoreElements();) {
            final String name = enumeration.nextElement();
            tmp.add(get(name));
        }

        return tmp;
    }

}
