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
package com.fatwire.gst.foundation.facade.vwebroot;

import com.fatwire.assetapi.data.AssetId;

/**
 * Virtual webroot as defined in GST Site Foundation document
 *
 * @author Tony Field
 * @since Jul 22, 2010
 */
public interface VirtualWebroot {

    public AssetId getId();

    public String getMasterVirtualWebroot();

    public String getEnvironmentVirtualWebroot();

    public String getEnvironmentName();
}