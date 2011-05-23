package advcol

import COM.FutureTense.Interfaces.ICS
import COM.FutureTense.Interfaces.IList

import com.fatwire.assetapi.data.AssetId
import com.fatwire.assetapi.data.BlobObject
import com.fatwire.cs.core.db.Util
import com.fatwire.gst.foundation.IListUtils
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.controller.annotation.Mapping
import com.fatwire.gst.foundation.facade.assetapi.AssetIdUtils
import com.fatwire.gst.foundation.facade.assetapi.asset.AssetFilterIterator
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetAccess
import com.fatwire.gst.foundation.facade.runtag.commercecontext.Recommendations
import com.fatwire.gst.foundation.facade.uri.BlobUriBuilder



public class GTStyleSheetResolver implements Action {
    @InjectForRequest public TemplateAssetAccess assetDao;
    @InjectForRequest public Model model;

    @Mapping("StyleSheetAttrName") public String StyleSheetAttrName
    @Mapping("StyleSheetAttrType") public String StyleSheetAssetType

    @Override
    public void handleRequest(ICS ics) {
        AssetId current = AssetIdUtils.currentId(ics);
        int max = ics.GetVar("max")!=null?Integer.parseInt(ics.GetVar("max")):1000;
        Collection<AssetId> list=Recommendations.getRecommendations(ics,current,max);
        
        Date insiteDate = ics.GetSSVar("__insiteDate") !=null? Util.parseJdbcDate(ics.GetSSVar("__insiteDate")): new Date();
        for(AssetId id: new AssetFilterIterator(assetDao, insiteDate, list)){
            TemplateAsset asset =assetDao.read(id,StyleSheetAttrName);
            BlobObject blob = asset.asBlob(StyleSheetAttrName);
            if(blob !=null){
                String link = new BlobUriBuilder(blob).mimeType("text/css").toURI(ics);
                model.list("stylesheet", link);
            }
        }
    }
}