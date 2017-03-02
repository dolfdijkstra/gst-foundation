/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.navigation;

import com.fatwire.assetapi.data.AssetId;

/**
 * Simple node, representing an asset, that can be populated with asset data. 
 * It is up to the implementation to decide what data to expose and how.
 * @author Freddy Villalba
 * @since 2017-03-02.
 */
public interface AssetNode<NODE extends AssetNode<NODE>> extends Node<NODE> {

    AssetId getId();
    
}