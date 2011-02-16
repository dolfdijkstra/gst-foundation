package com.fatwire.gst.foundation.facade.assetapi.asset;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetMapper;

public class TemplateAssetMapper implements AssetMapper<TemplateAsset> {

    public TemplateAsset map(AssetData assetData) {
        return new TemplateAsset(assetData);
    }

}
