/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 *  <asset:getsitenode
 *    name="pageAssetName"
 *    output="nodeIDVariableName"/>
 *
 * @author Tony Field
 * @since Sep 28, 2008
 */
public class GetSiteNode extends AbstractTagRunner
{
    public GetSiteNode() { super("ASSET.GETSITENODE"); }

    public void setName(String name) {set("NAME", name);}

    public void setOutput(String output) {set("OUTPUT", output);}
}
