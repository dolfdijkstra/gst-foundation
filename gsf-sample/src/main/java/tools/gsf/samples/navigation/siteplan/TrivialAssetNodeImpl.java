package tools.gsf.samples.navigation.siteplan;

import com.fatwire.assetapi.data.AssetId;

import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.impl.AbstractAssetNodeImpl;

public class TrivialAssetNodeImpl extends AbstractAssetNodeImpl<TrivialAssetNodeImpl> {
	
	private static final long serialVersionUID = -7637446633778028560L;

    private TemplateAsset asset;
	
	public TrivialAssetNodeImpl(TemplateAssetAccess taa, AssetId assetId) {
		super(assetId);
		// NOTE: you may gather the data to be exposed by this implementation
		//       specific getters (getWraUrl, etc... e.g. project-specific ones) here
		//       or we could do it upon demand, inside each getter method. 
	    this.asset = taa.read(assetId, "name", "template");
	}

	public String getAssetName() { 
		return this.asset.asString("name");
	}
	
	public String getAssetTemplate() {
		return this.asset.asString("template");
	}
	
	// NOTE: we could add as many getters as data we need to expose for our NavService implementation
	//       OR you could even have this class extend HashMap... the point is you have full control
	//       over what you expose and how.
	
    @Override
    public String toString() {
        return "TrivialAssetNodeImpl{" +
                "id=" + id +
                "}";
    }

}