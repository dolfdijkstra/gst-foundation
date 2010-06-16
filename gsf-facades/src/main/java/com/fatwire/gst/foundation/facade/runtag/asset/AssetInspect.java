package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <ASSET.INSPECT
      LIST="list name"
      TYPE="asset type"
      [PUBID="publication ID"]
      [SUBTYPE="asset subtype"] />
 *
 * @author Tony Field
 * @since 21-Nov-2008
 */
public final class AssetInspect extends AbstractTagRunner
{
    public AssetInspect()
    {
        super("ASSET.INSPECT");
    }

    public void setList(String s) { set("LIST", s); }
    public void setType(String s) { set("TYPE", s); }
    public void setPubid(String s) { set("PUBID", s); }
    public void setSubtype(String s) { set("SUBTYPE", s); }
}
