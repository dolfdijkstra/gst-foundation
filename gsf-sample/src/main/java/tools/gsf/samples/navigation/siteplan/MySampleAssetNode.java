package tools.gsf.samples.navigation.siteplan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.BuildersFactory;
import com.fatwire.assetapi.data.LegacyLinkInfo;
import com.fatwire.assetapi.def.DependencyTypeEnum;

import COM.FutureTense.Interfaces.Utilities;
import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.siteplan.AbstractAssetNode;

/**
 * This is just an example of what you can do inside your AssetNode implementation.
 * 
 * You may gather the data to be exposed by this class either at instantiation time
 * (e.g. inside the constructor) or on-demand (e.g. inside your own, project-specific
 * "get" methods: getWraUrl, etc...)
 * 
 * You may add as many getters as data you need to expose.
 * 
 * You could even expose the TemplateAsset itself, if that works best for you, as in:
 *       
 *    public TemplateAsset getAssetData() {
 *         return this.asset;
 *    }
 *    
 * The bottomline is: YOU HAVE FULL CONTROL OVER YOUR AssetNode IMPLEMENTATION.
 * 
 * 
 * @author Freddy Villalba
 * @since 2017-03-09
 *
 */
public class MySampleAssetNode extends AbstractAssetNode<MySampleAssetNode> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MySampleAssetNode.class);
	
	private static final long serialVersionUID = -7637446633778028560L;
	
	private BuildersFactory buildersFactory;
    private TemplateAsset asset;
    private String sitename;
	
	public MySampleAssetNode(BuildersFactory buildersFactory, TemplateAssetAccess taa, AssetId assetId, String sitename) {
		super(assetId);
		
		LOG.debug("Initializing instance of MySampleAssetNode, will now read data for asset {} using dao {}", assetId, taa);
	    this.asset = taa.read(assetId, "name", "template");
	    this.buildersFactory = buildersFactory;
		this.sitename = sitename;
	}

	public String getAssetName() { 
		return this.asset.asString("name");
	}
	
	public String getAssetTemplate() {
		return this.asset.asString("template");
	}
	
	public String getAssetUrl() throws AssetAccessException {
		String tname = this.getAssetTemplate();
		if (!Utilities.goodString(tname)) {
			LOG.debug("There is no template bound to this node's asset. Hence, no url can be produced.");
			return null;
		}
		if (!Utilities.goodString(sitename)) {
			// Since this is just a sample implementation, we are keeping it simple.
			// However, you *could* at least try to figure out the site on your own.
			// For instance, if the asset belongs to a single site, then that's THE site. 
			LOG.debug("No site was specified. Hence, no url can be produced.");
			return null;			
		}
		// NOTE: YOU CAN USE AssetReader HERE (OR ANYTHING ELSE) FOR BUILDING
		//       THE URL, IF YOU WANT / NEED TO !!!
		LegacyLinkInfo linkInfo = this.buildersFactory.newLegacyAssetLinkInfo();
		linkInfo.logDep(DependencyTypeEnum.EXISTS)
			.forAsset(this.asset.getAssetId())
			.forSite(this.sitename)
			.useTemplate(tname);

		String url = this.buildersFactory.newLinkFactory().makeLink(linkInfo);

		LOG.debug("Produced URL for asset {} and site {} using template {}: {}", id, tname, this.sitename, url);
		return url;
	}

	
    @Override
    public String toString() {
        return "MySampleAssetNode{" +
                "id=" + id +
                ", name=" + this.getAssetName() +
                ", template=" + this.getAssetTemplate() +
                ", url=" + this.getAssetTemplate() +
                "}";
    }

}