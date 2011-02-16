package com.fatwire.gst.foundation.facade.assetapi;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.data.BlobObject.BlobAddress;
import com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl;

/**
 * Builder support for blob urls.
 * <pre>
 * new BlobUriBuilder(blob).mimeType("image/jpeg").parent("12345").toURI(ics);
 * </pre>
 * 
 * @author Dolf Dijkstra
 * @since Feb 15, 2011
 * 
 */
public class BlobUriBuilder {
    
   

    private GetBlobUrl tag = new GetBlobUrl();
    private int n = 1;

    /**
     * @param blob
     */
    public BlobUriBuilder(final BlobObject blob) {
        BlobAddress a = blob.getBlobAddress();
        tag.setBlobCol(a.getColumnName());
        tag.setBlobKey(a.getIdentifier().toString());
        tag.setBlobTable(a.getTableName());
        tag.setBlobWhere(a.getIdentifierColumnName());
    }

    /**
     * @param ics
     * @return
     */
    public String toURI(ICS ics) {
        tag.setOutstr("foo_");
        tag.execute(ics);
        String ret = ics.GetVar("foo_");
        ics.RemoveVar("foo_");
        return ret;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setAssembler(java.lang.String)
     */
    public BlobUriBuilder assembler(String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setFragment(java.lang.String)
     */
    public BlobUriBuilder fragment(String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBobHeader(java.lang.String)
     */
    public BlobUriBuilder mimeType(String s) {
        tag.setBobHeader(s);
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBlobNoCache(java.lang.String)
     */
    public BlobUriBuilder blobNoCache(String s) {
        tag.setBlobNoCache(s);
        return this;
    }

    /**
     * @param n
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBlobHeaderName(int,
     *      java.lang.String)
     */
    public BlobUriBuilder header(String name, String value) {
        tag.setBlobHeaderName(n, name);
        tag.setBlobHeaderValue(n, value);
        n++;
        return this;
    }

    /**
     * @param s
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setParentId(java.lang.String)
     */
    public BlobUriBuilder parent(String s) {
        tag.setParentId(s);
        return this;
    }
}
