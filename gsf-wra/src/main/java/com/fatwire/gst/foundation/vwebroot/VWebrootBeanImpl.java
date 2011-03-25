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
package com.fatwire.gst.foundation.vwebroot;

import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;

/**
 * Simple Virtual Webroot Bean
 * 
 * @author Tony Field
 * @since Jul 22, 2010
 */
final class VWebrootBeanImpl implements VirtualWebroot {

    private static final String GST_VIRTUAL_WEBROOT = "GSTVirtualWebroot";
    private AssetId id;
    private String masterVWebroot;
    private String envVWebroot;
    private String envName;

    VWebrootBeanImpl(long id, String masterVWebroot, String envVWebroot, String envName) {
        this.id = new AssetIdImpl(GST_VIRTUAL_WEBROOT, id);
        if (!Utilities.goodString(masterVWebroot))
            throw new IllegalArgumentException("Invalid Master VWebroot:" + masterVWebroot);
        this.masterVWebroot = masterVWebroot;
        if (!Utilities.goodString(envVWebroot))
            throw new IllegalArgumentException("Invalid Env VWebroot:" + envVWebroot);
        this.envVWebroot = envVWebroot;
        if (!Utilities.goodString(envName))
            throw new IllegalArgumentException("Invalid Env Name:" + envName);
        this.envName = envName;
    }

    public AssetId getId() {
        return id;
    }

    public String getMasterVirtualWebroot() {
        return masterVWebroot;
    }

    public String getEnvironmentVirtualWebroot() {
        return envVWebroot;
    }

    public String getEnvironmentName() {
        return envName;
    }
}
