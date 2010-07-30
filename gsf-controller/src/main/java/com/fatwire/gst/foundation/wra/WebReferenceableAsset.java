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

import java.util.Date;

import com.fatwire.assetapi.data.AssetId;

/**
 * Bean containing core WebReferenceableAsset fields
 *
 * @author Tony Field
 * @since Jul 21, 2010
 */
public interface WebReferenceableAsset {

    public AssetId getId();

    public String getName();

    public String getDescription();

    public String getSubtype();

    public String getStatus();

    public Date getStartDate();

    public Date getEndDate();

    public String getMetaTitle();

    public String getMetaDescription();

    public String getMetaKeyword();

    public String getH1Title();

    public String getLinkTitle();

    public String getPath();

    public String getTemplate();
}
