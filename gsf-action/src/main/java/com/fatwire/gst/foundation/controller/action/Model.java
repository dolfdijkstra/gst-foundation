/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * This class is a container for Model data in the Model View Controller (MVC)
 * framework.
 * 
 * <p/>
 * This class is not thread-safe.
 * 
 * @author Dolf Dijkstra
 * @since Apr 16, 2011
 */
public class Model {

    private final Map<String, Object> map = new HashMap<String, Object>();

    /**
     * Clears the model data.
     */
    public void reset() {
        map.clear();
    }

    /**
     * Removes the data with the name key.
     * 
     * @param name
     */
    public void reset(String name) {
        map.remove(name);
    }

    /**
     * Adds the key/value pair.
     * 
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        map.put(key, value);
    }

    /**
     * Adds the key/value pair, where are the values are added as a list.
     * 
     * @param key
     * @param value
     */
    public void add(String key, Object... value) {
        map.put(key, Arrays.asList(value));
    }

    /**
     * Adds the key/value pair, where are the value is added as a list.
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void list(String key, Object value) {
        Object v = map.get(key);
        if (v instanceof Collection<?>) {
            Collection<Object> l = (Collection<Object>) v;
            l.add(value);
        } else {
            Collection<Object> l = new LinkedList<Object>();
            if (v != null)
                l.add(v);
            l.add(value);
            map.put(key, l);
        }
    }

    /**
     * Returns all the key/value pairs.
     * 
     * @return the entries as an unmodifiableSet, never null.
     */
    public Set<Entry<String, Object>> entries() {
        return Collections.unmodifiableSet(map.entrySet());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((map == null) ? 0 : map.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Model))
            return false;
        Model other = (Model) obj;
        if (map == null) {
            if (other.map != null)
                return false;
        } else if (!map.equals(other.map))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Model [map=" + map + "]";
    }

}
