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

/**
 * 
 */
package com.fatwire.gst.foundation.facade.assetapi;

import com.fatwire.assetapi.data.AssetData;

/**
 * 
 * Inteface to transform a AssetData into another form.
 * 
 * @author Dolf Dijkstra
 * @param <T> creates new object T from asset data
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public interface AssetMapper<T> {

    /**
     * Creates a new object based on the provided AssetData.
     * @param assetData asset data
     * @return the created object.
     */
    T map(AssetData assetData);

}
