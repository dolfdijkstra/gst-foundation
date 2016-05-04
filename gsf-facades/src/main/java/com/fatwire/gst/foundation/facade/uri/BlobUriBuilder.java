/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.gst.foundation.facade.uri;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.data.BlobObject.BlobAddress;
import com.fatwire.gst.foundation.DebugHelper;
import com.fatwire.gst.foundation.facade.assetapi.AttributeDataUtils;
import com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl;

/**
 * Builder support for blob urls.
 * 
 * <pre>
 * new BlobUriBuilder(blob).mimeType(&quot;image/jpeg&quot;).parent(&quot;12345&quot;).toURI(ics);
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
     * Constructor that accepts AssetData and an attribute name.
     * 
     * @param data the asset.
     * @param attributeName the name of the attribute containing the blob.
     */
    public BlobUriBuilder(final AssetData data, String attributeName) {
        AttributeData attr = data.getAttributeData(attributeName);
        if (attr == null)
            throw new IllegalStateException("Can't find attribute " + attributeName + " on asset "
                    + DebugHelper.toString(data.getAssetId()));
        BlobObject blob = AttributeDataUtils.asBlob(attr);
        if (blob == null)
            throw new IllegalStateException("Attribute " + attributeName + " on asset "
                    + DebugHelper.toString(data.getAssetId()) + " is not found.");

        populateTag(blob.getBlobAddress());

        parent(data.getAssetId());
    }

    /**
     * Constructor accepting a BlobObject.
     * 
     * @param blob
     */
    public BlobUriBuilder(final BlobObject blob) {
        this(blob.getBlobAddress());
    }

    /**
     * Constructor accepting a BlobAddress.
     * 
     * @param address
     */
    public BlobUriBuilder(final BlobAddress address) {

        populateTag(address);
    }

    /**
     * @param address
     */
    private void populateTag(final BlobAddress address) {
        tag.setBlobCol(address.getColumnName());
        tag.setBlobKey(address.getIdentifierColumnName());
        tag.setBlobTable(address.getTableName());
        tag.setBlobWhere(address.getIdentifier().toString());
    }

    /**
     * @param ics Content Server context object
     * @return the URI
     */
    public String toURI(ICS ics) {
        tag.setOutstr("foo_");
        String ret;
        try {
            tag.execute(ics);
            ret = ics.GetVar("foo_");
        } finally {
            ics.RemoveVar("foo_");
        }
        return ret;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setAssembler(java.lang.String)
     */
    public BlobUriBuilder assembler(String s) {
        tag.setAssembler(s);
        return this;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setFragment(java.lang.String)
     */
    public BlobUriBuilder fragment(String s) {
        tag.setFragment(s);
        return this;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBobHeader(java.lang.String)
     */
    public BlobUriBuilder mimeType(String s) {
        if (StringUtils.isBlank(s))
            throw new IllegalArgumentException("Value cannot be blank.");
        tag.setBobHeader(s);
        return this;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBlobNoCache(java.lang.String)
     */
    public BlobUriBuilder blobNoCache(String s) {
        if (StringUtils.isBlank(s))
            throw new IllegalArgumentException("Value cannot be blank.");
        tag.setBlobNoCache(s);
        return this;
    }

    /**
     * @param name
     * @param value
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBlobHeaderName(int,
     *      java.lang.String)
     */
    public BlobUriBuilder header(String name, String value) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Name cannot be blank.");
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("Value cannot be blank.");
        tag.setBlobHeaderName(n, name);
        tag.setBlobHeaderValue(n, value);
        n++;
        return this;
    }

    /**
     * Sets the Cache-Control: max-age http response header for this blob.
     * 
     * @param value the max-age value as per http specification.
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setBlobHeaderName(int,
     *      java.lang.String)
     */
    public BlobUriBuilder maxAge(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Cache-Control: max-age can not be negative");
        tag.setBlobHeaderName(n, "Cache-Control");
        tag.setBlobHeaderValue(n, "max-age=" + value);
        n++;
        return this;
    }

    /**
     * @param s
     * @return this
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setParentId(java.lang.String)
     */
    public BlobUriBuilder parent(String s) {
        if (StringUtils.isBlank(s))
            throw new IllegalArgumentException("Value cannot be blank.");
        tag.setParentId(s);
        return this;
    }

    /**
     * @param assetId
     * @see com.fatwire.gst.foundation.facade.runtag.render.GetBlobUrl#setParentId(java.lang.String)
     * @return this
     */
    public BlobUriBuilder parent(AssetId assetId) {
        return parent(Long.toString(assetId.getId()));

    }

}
