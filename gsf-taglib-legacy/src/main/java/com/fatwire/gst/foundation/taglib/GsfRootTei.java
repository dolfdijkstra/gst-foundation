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

package com.fatwire.gst.foundation.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @deprecated as of release 12.x
 *
 */
public class GsfRootTei extends TagExtraInfo {
    private static final Logger log = LoggerFactory.getLogger("tools.gsf.taglib.GsfRootTei");

    @Override
    public VariableInfo[] getVariableInfo(final TagData data) {
        if (log.isDebugEnabled()) {
            log.debug("getVariableInfo: " + data);
        }
        return new VariableInfo[] { new VariableInfo("assetDao", ScatteredAssetAccessTemplate.class.getName(), true,
                VariableInfo.NESTED) };

    }
}
