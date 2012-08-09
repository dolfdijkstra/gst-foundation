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
package com.fatwire.gst.foundation.mapping;

/**
 * Object to hold the CSElement or Template mapping value and mapping type.
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public final class MappingValue {

    /**
     * Enum for the mapping type.
     */
    public enum Type {
        asset, assettype, assetname, tname
    }

    private final Type type;
    private final String value;
    private String[] parts;

    /**
     * @param type mapping type
     * @param value mapping value
     */
    public MappingValue(final Type type, final String value) {
        super();
        this.type = type;
        this.value = value;
        switch (type) {
            case asset:
            case assetname:
                parts = value.split(":");
        }
    }

    /**
     * @return the left hand side of the value
     */
    public String getLeft() {
        if (parts == null) {
            return value;
        }
        return parts[0];
    }

    /**
     * @return the right hand side of the value.
     */
    public String getRight() {
        if (parts == null) {
            return value;
        }
        return parts[1];
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
