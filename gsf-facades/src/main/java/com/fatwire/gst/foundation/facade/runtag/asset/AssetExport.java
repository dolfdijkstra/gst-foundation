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
public class AssetExport extends AbstractTagRunner {

    public AssetExport(final String name, final String prefix, final String output) {
        super("asset.export");
        set("NAME", name);
        set("PREFIX", prefix);
        set("OUTPUT", output);
        set("WRITEATTRVALUE", false);
    }

}
