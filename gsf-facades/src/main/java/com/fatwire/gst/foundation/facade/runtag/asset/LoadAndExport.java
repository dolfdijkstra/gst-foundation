/**
 * 
 */
package com.fatwire.gst.foundation.facade.runtag.asset;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunner;

/**
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */

public class LoadAndExport implements TagRunner {

    private final String assetType;

    private final long assetId;

    public LoadAndExport(final String assetType, final long assetId) {
        super();
        this.assetType = assetType;
        this.assetId = assetId;
    }

    public String execute(final ICS ics) {
        final String name = "asset" + ics.genID(true);
        try {
            final AssetLoadById al = new AssetLoadById();
            al.setName(name);
            al.setAssetType(assetType);
            al.setAssetId(assetId);
            // al.setEditable(true);
            al.setOption(AssetLoadById.OPTION_READ_ONLY_COMPLETE);

            al.execute(ics);

            new AssetScatter(name, "as", "PubList").execute(ics);
            new AssetScatter(name, "as", true).execute(ics);
            new AssetExport(name, "as", "xml").execute(ics);

            final String xml = ics.GetVar("xml");
            ics.RemoveVar("xml");
            return xml;
        } finally {
            // cleaning up
            ics.SetObj(name, null);// clear obj from ics
            final List<String> toClean = new ArrayList<String>();
            for (final Enumeration e = ics.GetVars(); e.hasMoreElements();) {
                final String k = (String) e.nextElement();
                if (k.startsWith("as:")) {
                    toClean.add(k);
                }
            }
            // preventing java.util.ConcurrentModificationException
            for (String n : toClean) {
                ics.RemoveVar(n);
            }

        }
    }

    public long getAssetId() {
        return assetId;
    }

    public String getAssetType() {
        return assetType;
    }

}
