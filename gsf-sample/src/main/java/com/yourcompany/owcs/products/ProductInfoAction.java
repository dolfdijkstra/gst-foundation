package com.yourcompany.owcs.products;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.gst.foundation.controller.action.Action;
import com.fatwire.gst.foundation.controller.action.Model;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.uri.BlobUriBuilder;
import com.fatwire.gst.foundation.facade.uri.TemplateUriBuilder;
import com.fatwire.gst.foundation.include.IncludeService;

public class ProductInfoAction implements Action {

    @InjectForRequest
    ProductInfoAccessTemplate assetService;
    @InjectForRequest
    IncludeService includeService;

    @InjectForRequest
    Model model;

    @Override
    public void handleRequest(ICS ics) {
        // read all the product AssetData with current c/cid (ics.GetVar())
        // you could also get the from a local cache

        ProductInfo product = assetService.readCurrent();
        // register compositional dependency
        LogDep.logDep(ics, assetService.currentId());

        // do more with product if needed...

        // add this product to the model, so it can be resolved in the jsp
        model.add("product", product);

        model.add("productURL", new TemplateUriBuilder(assetService.currentId(), "Full").toURI(ics));
        // set up a render:calltemplate to a product's
        // OneModel/BreakQuantityDropdowns template
        // this is done with a includeService and replaces the
        // render:calltemplate with it's complexities
        /*
         * <render:calltemplate tname="OneModel/BreakQuantityDropdowns"
         * site='<%=ics.GetVar("site") %>' c='<%=ics.GetVar("c") %>'
         * cid='<%=ics.GetVar("cid") %>' context="" slotname="selectorset"
         * style="pagelet" tid='<%=ics.GetVar("tid") %>' ttype="Template" >
         * </render:calltemplate>
         */

        includeService.template("detail", assetService.currentId(), "OneModel/BreakQuantityDropdowns").pagelet();
        includeService.template("pricing", assetService.currentId(), "OneModel/Pricing").pagelet();

        // read the blob to construct a URI to the product image, could also
        // come from product.getImageBlob() for instance;
        BlobObject blob = AttributeDataUtils.asBlob(assetService.readAsset(assetService.currentId()).getAttributeData(
                "image"));
        String uriForImage = new BlobUriBuilder(blob).mimeType("image/jpeg").maxAge(3600).toURI(ics);
        // add this image link to the model, so it can be resolved in the jsp
        model.add("imgurlprodpri", uriForImage);

    }

}
