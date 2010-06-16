/**
 * 
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * 
 * 
 * @author Dolf Dijkstra
 *
 */

public class AssetScatter extends AbstractTagRunner {

	public AssetScatter(final String name, final String prefix, final String fieldlist) {
		super("asset.scatter");
		set("NAME", name);
		set("PREFIX", prefix);
		set("FIELDLIST", fieldlist);
	}

	public AssetScatter(final String name, final String prefix, final boolean exclude) {
		super("asset.scatter");
		set("NAME", name);
		set("PREFIX", prefix);
		set("EXCLUDE", exclude);
	}

}