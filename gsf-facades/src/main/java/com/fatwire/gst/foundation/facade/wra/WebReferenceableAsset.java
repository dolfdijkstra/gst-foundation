/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.wra;

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
