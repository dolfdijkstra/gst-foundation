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
package com.fatwire.gst.foundation.properties;

import com.fatwire.assetapi.data.AssetId;

/**
 * Data representing a property value
 *
 * @author Tony Field
 * @since 2011-09-02
 */
public interface Property {
    /**
     * The name of the property
     */
    String getName();

    /**
     * The description of the property, for human informational purposes only
     */
    String getDescription();

    /**
     * Returns whether or not the property is set to null.  Empty strings are not null.
     */
    boolean isNull();

    /**
     * Returns true if the value is set to TRUE or true.  Not set or set to anything else returns false.
     */
    boolean asBoolean();

    /**
     * Returns the property value as a string
     */
    String asString();

    /**
     * Retursn the property as a long.
     */
    long asLong();

    /**
     * Returns the property as an int
     */
    int asInt();

    /**
     * Returns the property as an asset ID.
     */
    AssetId asAssetId();
}
