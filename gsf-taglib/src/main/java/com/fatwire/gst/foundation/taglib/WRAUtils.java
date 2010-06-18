package com.fatwire.gst.foundation.taglib;

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.gst.foundation.facade.assetapi.AssetDataUtils;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;

/**
 * @author David Chesebro
 * @since Jun 17, 2010
 */
public class WRAUtils {
    private ICS ics;

    public WRAUtils(ICS ics) {
        this.ics = ics;
    }

    /**
     * Return a Map<String,String> containing the common information found
     * inside a HEAD html tag.
     * <p/>
     * Does not resolve aliases.
     * 
     * @param c asset type
     * @param cid asset id
     * @return Map<String,String> with keys title, keywords, and description
     */
    public Map<String, String> getCoreFields(String c, String cid) {
        Map<String, String> coreFields = new HashMap<String, String>();

        AssetData data = AssetDataUtils.getAssetData(c, cid, "metatitle", "metadescription", "metakeyword", "h1title",
                "linktext", "path", "template");
        String metatitle = data.getAttributeData("metatitle").getData().toString();
        String metakeyword = AttributeDataUtils.getMultivaluedAsCommaSepString(data.getAttributeData("metakeyword"));
        String metadescription = data.getAttributeData("metadescription").getData().toString();
        String h1title = data.getAttributeData("h1title").getData().toString();
        String linktext = data.getAttributeData("linktext").getData().toString();
        String path = data.getAttributeData("path").getData().toString();
        String template = data.getAttributeData("template").getData().toString();

        coreFields.put("metatitle", metatitle);
        coreFields.put("metakeyword", metakeyword);
        coreFields.put("metadescription", metadescription);
        coreFields.put("h1title", h1title);
        coreFields.put("linktext", linktext);
        coreFields.put("path", path);
        coreFields.put("template", template);

        return coreFields;
    }
}
