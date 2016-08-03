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

/**
 * 
 * Represents a row in an IList.
 * <p>
 * Wrapper over IList so that it can be used by an iterator()
 * 
 * @author Dolf.Dijkstra
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */

public interface Row {
    /**
     * @param key key string to look up
     * @return the key value as a String or null.
     */
    String getString(String key);

    /**
     * @param key key string to look up
     * @return the key value as a Long or null.
     */
    Long getLong(String key);
    
    /**
     * @param key key string to look up
     * @return the key value as a Integer or null.
     */
    Integer getInt(String key);


    /**
     * @param key key string to look up
     * @return the key value as a Byte array or null.
     */
    byte[] getBytes(String key);

    /**
     * @param key key string to look up
     * @return the key value as a char or null.
     */
    Character getChar(String key);

    /**
     * @param key key string to look up
     * @return the key value as a Date or null.
     */
    Date getDate(String key);

    /**
     * @param key key string to look up
     * @return true if key is a field in the Row.
     */
    boolean isField(String key);
}
