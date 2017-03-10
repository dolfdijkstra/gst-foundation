package tools.gsf.samples.config;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.BuildersFactory;
import com.fatwire.assetapi.data.DefaultBuildersFactory;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.config.Factory;
import tools.gsf.config.IcsBackedFactory;
import tools.gsf.config.ServiceProducer;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.NavService;
import tools.gsf.samples.navigation.siteplan.LightweightSitePlanNavService;
import tools.gsf.samples.navigation.siteplan.MySampleAssetNode;

/**
 * A simple IcsBackedFactory implementation for exposing our custom NavService
 * via ServiceProducer.
 * 
 * This ServiceProduce will allow, amongst other things, injecting the NavService
 * into your InjectingController.
 * 
 * Note that this custom IcsBackedFactory implementation is just an EXAMPLE.
 * 
 * Use only for test-driving this sample NavService implementation.
 * 
 * For real projects, you should use this class only as a guideline on how to expose a
 * custom NavService implementation via a ServiceProducer method added to your own
 * (custom) IcsBackedFactory implementation.
 *
 * 
 * @author fvillalba
 * @since 09-March-2017
 *
 */
public class MyIcsBackedFactory extends IcsBackedFactory {
	
	public MyIcsBackedFactory(ICS ics, Factory delegate) {
		super(ics, delegate);
	}
	
    @ServiceProducer(cache = true, name="myNavService")
    public NavService<MySampleAssetNode, AssetId, AssetId> createNavService() {
    	TemplateAssetAccess taa = getObject("templateAssetAccess", TemplateAssetAccess.class);
    	ICS ics = getICS();
        return new LightweightSitePlanNavService(ics, taa);
    }	

    @ServiceProducer(cache = true, name="buildersFactory")
    public BuildersFactory createBuildersFactory(ICS ics) {
    	return new DefaultBuildersFactory(ics);
    }
    
}