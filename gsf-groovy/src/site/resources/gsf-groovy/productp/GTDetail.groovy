package productp;


import COM.FutureTense.Interfaces.ICS

import com.fatwire.assetapi.common.AssetAccessException
import com.fatwire.assetapi.data.AssetId
import com.fatwire.assetapi.util.AssetUtil
import com.fatwire.gst.foundation.controller.action.Action
import com.fatwire.gst.foundation.controller.action.Model
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest
import com.fatwire.gst.foundation.controller.annotation.Mapping
import com.fatwire.gst.foundation.facade.assetapi.asset.ScatteredAssetAccessTemplate
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAssetMapper
import com.fatwire.gst.foundation.include.IncludeService
import com.fatwire.gst.foundation.mapping.AssetName

class GTDetail implements Action {

    @InjectForRequest public IncludeService includeService;
    @InjectForRequest public ScatteredAssetAccessTemplate assetDao;
    @InjectForRequest public ICS ics;
    @InjectForRequest public Model model;

    //@Mapping("ProductAttrType")     public String productAttrType
    @Mapping("Manufacturer")        public AssetName manufacturer
    @Mapping("ManufacturerName")    public AssetName manufacturerName
    @Mapping("ManufacturerDesc")    public AssetName manufacturerDesc
    @Mapping("ManufacturerLogo")    public AssetName manufacturerLogo
    //@Mapping("ImageType")           public String imageType
    @Mapping("ImageDetail")         public String imageDetail
    @Mapping("Category")            public AssetName category
    @Mapping("CategoryName")        public AssetName categoryName
    @Mapping("CategoryDesc")        public AssetName categoryDesc
    @Mapping("Subcategory")         public AssetName subCategory
    @Mapping("SubcategoryName")     public AssetName subCategoryName
    @Mapping("SubcategoryDesc")     public AssetName subCategoryDesc
    @Mapping("StandardSideNavView") public String standardSideNavView
    @Mapping("Summary")             public String summary
    @Mapping("ProductType")         public String productType


    public void handleRequest(ICS ics){

        TemplateAssetMapper mapper = new TemplateAssetMapper();
        TemplateAsset asset = assetDao.readAsset(assetDao.currentId(), mapper);

        String subType;
        try {
            subType=AssetUtil.getSubtype(ics, assetDao.currentId());
        } catch (AssetAccessException e) {
            throw new RuntimeException(e);
        }
        String assetName = null;
        if (manufacturer.name == subType) {
            // We are rendering a product manufacturer.
            model.add("manufacturer", true);
            AssetId logoId = asset.asAssetId(manufacturerLogo.name);
            if (logoId != null) {
                model.add("manufacturerLogo", true);
                
                includeService.template("ManufacturerImage", logoId,
                        imageDetail).embedded()
            }
            assetName = asset.asString(manufacturerName.name);
            model.add("ParentName", assetName);
            model.add("Desc", asset.asString(manufacturerDesc.name));
        } else if (category.name == subType) {
            model.add("category", true);
            // We are rendering a product category.
            assetName = asset.asList(categoryName.name).get(0);
            model.add("ParentName", assetName);
            model.add("Desc", asset.asList(categoryDesc.name).get(0));

        } else if (subCategory.name == subType) {
            model.add("subCategory", true);
            // We are rendering a product subcategory
            assetName = asset.asList(subCategoryName.name).get(0);
            model.add("ParentName", assetName);
            model.add("Desc", asset.asList(subCategoryDesc.name).get(0));
        }

        String condition=subType + "Name=" + assetName;
        Iterable<TemplateAsset> children = assetDao.query(productType, "FSII Product",
                condition, mapper);

        int i = 0;
        List l = new ArrayList();
        for (TemplateAsset child : children) {
            i++;
            l.add("ProductSummary" + i);
            includeService.template("ProductSummary" + i, child.getAssetId(),
                    summary);
        }
        model.add("ProductSummary", l);
        AssetId pageId = assetDao.createAssetId("Page", ics.GetVar("p"));
        includeService.template("ProductDetailViewAd", pageId,
                standardSideNavView);
    }
}


