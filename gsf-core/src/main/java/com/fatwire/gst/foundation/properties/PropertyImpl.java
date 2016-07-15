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

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils;

/**
 * TODO: Add class/interface details
 *
 * @author Tony Field
 * @since 11-09-02
 */
final class PropertyImpl implements Property, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 692797066527955686L;
	String name;
    String description;
    String value;
    String propertyAssetType;
    
    PropertyImpl(String propertyAssetType, String name, String description, String value) {
        this.name = name;
        this.value = value;
        this.propertyAssetType = propertyAssetType;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public boolean isNull() {
        return value == null;
    }
    public boolean asBoolean() {
        return Boolean.getBoolean(value);
    }
    public String asString() {
        return value;
    }
    public long asLong() {
        return Long.valueOf(value);
    }
    public int asInt() {
        return Integer.valueOf(value);
    }
    public AssetId asAssetId() {
        return AssetIdUtils.fromString(value);
    }

    public String toString() {
    	// Basic password suppression
    	if (!StringUtils.isEmpty(name) &&
    		(name.startsWith("password") ||
    		 name.startsWith("pwd"))) {
    		return this.propertyAssetType + ":" + name + "= (value blanked by password protection mechanism)";
    	} else { 
    		return this.propertyAssetType + ":" + name + "=" + value; // todo: smart password suppression
    	}
    }
}
