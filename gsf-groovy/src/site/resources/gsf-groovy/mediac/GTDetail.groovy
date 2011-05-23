package mediac

import COM.FutureTense.Interfaces.ICS;

import org.apache.commons.lang.StringUtils

import COM.FutureTense.Interfaces.ICS

import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.controller.annotation.Mapping
import com.fatwire.gst.foundation.controller.annotation.Mapping.Match
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetMapper
import com.fatwire.gst.foundation.facade.uri.BlobUriBuilder
import com.fatwire.gst.foundation.html.Img
import com.fatwire.gst.foundation.include.IncludeService

class GTDetail implements Action {
    @InjectForRequest public ScatteredAssetAccessTemplate assetDao;
    @InjectForRequest public Model model;

    @Mapping(value="ImageFileAttrName", match=Match.right) public String ImageFileAttrName
    @Mapping(value="ImageMimeTypeAttrName", match=Match.right) public String ImageMimeTypeAttrName
    @Mapping(value="ImageWidthAttrName", match=Match.right) public String ImageWidthAttrName
    @Mapping(value="ImageHeightAttrName", match=Match.right) public String ImageHeightAttrName
    @Mapping(value="AltTextAttrName", match=Match.right) public String AltTextAttrName


    @Override
    public void handleRequest(ICS ics) {

        TemplateAssetMapper mapper = new TemplateAssetMapper();
        TemplateAsset asset = assetDao.readAsset(assetDao.currentId(), mapper,ImageMimeTypeAttrName,ImageWidthAttrName,ImageHeightAttrName,AltTextAttrName,ImageFileAttrName);


        BlobUriBuilder ub = new BlobUriBuilder(asset.asBlob(ImageFileAttrName));
        ub.mimeType(asset.asString(ImageMimeTypeAttrName))
        Img img = new Img();
        img.setSrc(ub.toURI(ics));
        img.setWidth asset.asString(ImageWidthAttrName)
        img.setHeight asset.asString(ImageHeightAttrName)
        String alt = asset.asString(AltTextAttrName);
        if(StringUtils.isBlank(alt)){
            alt="Content Server Image"
        }
        img.setAlt alt
        model.add("image",img);
    }
}
