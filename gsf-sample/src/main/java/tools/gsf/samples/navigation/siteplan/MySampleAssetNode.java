package tools.gsf.samples.navigation.siteplan;

import java.util.HashMap;

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
 * (e.g. inside the constructor) or on-demand.
 * 
 * You may add as many getters as data you need to expose and you may expose data in
 * any way you want.
 * 
 * For instance, you could expose the TemplateAsset itself, if that works best for you,
 * as in:
 *       
 *    public TemplateAsset getAssetData() {
 *         return this.asset;
 *    }
 * 
 * You could also expose a HashMap holding the specific data you want to expose instead
 * of defining specific getters for that purpose.
 *    
 * The bottomline is: YOU HAVE FULL CONTROL OVER YOUR AssetNode IMPLEMENTATION.
 * 
 * In any case, always design your Node implementation(s) with performance in mind.
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
    private HashMap<String, Object> data = new HashMap<String, Object>();
    
	public MySampleAssetNode(BuildersFactory buildersFactory, TemplateAssetAccess taa, AssetId assetId, String sitename) {
		super(assetId);

	    this.buildersFactory = buildersFactory;
	    
	    if (assetId.getType().equals("SiteNavigation")) {
	    	// Do nothing
	    	LOG.debug("No need to read any attribute data for node {}.", assetId);
	    } else {
			LOG.debug("Reading attribute data for asset {} using dao {}", assetId, taa);
		    this.asset = taa.read(assetId, "name", "template");
		    
	    	this.data.put("title", this.asset.asString("name"));
	    	this.data.put("url", this._buildUrl(assetId, this.asset.asString("template"), sitename));
	    }
	}
	
	private String _buildUrl(AssetId assetId, String tname, String sitename) {
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
			.forAsset(assetId)
			.forSite(sitename)
			.useTemplate(tname);

		String url;
		try {
			url = this.buildersFactory.newLinkFactory().makeLink(linkInfo);
		} catch (AssetAccessException e) {
			e.printStackTrace();
			return null;
		}

		LOG.debug("Produced URL for asset {} and site {} using template {}: {}", id, tname, sitename, url);
		return url;
	}
	
	public HashMap<String, Object> getData() {
		return this.data;
	}

    @Override
    public String toString() {
        return "MySampleAssetNode{" +
                "id=" + id +
                ", asset=" + this.asset +
                ", data=" + this.data +
                "}";
    }

}