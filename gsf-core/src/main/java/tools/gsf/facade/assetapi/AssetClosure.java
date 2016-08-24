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

package tools.gsf.facade.assetapi;

import com.fatwire.assetapi.data.AssetData;

/**
 * Closure to work with a single AssetData.
 *
 * @author Dolf Dijkstra
 */
public interface AssetClosure {
    /**
     * Perform work based on asset
     *
     * @param asset asset to work with
     * @return false if you want to iteration to stop
     */
    boolean work(AssetData asset);
}
