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
package com.fatwire.gst.foundation.wra;

import com.fatwire.assetapi.data.AssetId;

/**
 * Bean containing core Alias fields
 * 
 * @author Larissa Kowaliw
 * @since Jul 27, 2010
 * 
 * 
 * @deprecated as of release 12.x, will be replaced with a brand new, significantly improved NavigationService implementation which won't depend on any GSF-specific asset type / subtypes.
 * 
 */
public interface Alias extends WebReferenceableAsset {

    public static String ALIAS_ASSET_TYPE_NAME = "GSTAlias";

    public AssetId getTarget();

    public String getTargetUrl();

    public String getPopup();

    public AssetId getLinkImage();

}
