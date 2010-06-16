package com.fatwire.gst.foundation.facade.runtag.asset;


/**
 * 
 * Loads an asset by name 
 * 
 * @author Dolf Dijkstra
 *
 */


public class AssetLoadByName extends AbstractAssetLoad {

	public AssetLoadByName() {
	}

	public void setAssetName(final String name) {
		this.set("FIELD", "name");
		this.set("VALUE", name);
	}

}
