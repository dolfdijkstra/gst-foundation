package com.fatwire.gst.foundation.navigation;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AssetTypeDef;
import com.fatwire.assetapi.def.AttributeTypeEnum;
import com.fatwire.gst.foundation.facade.assetapi.asset.TemplateAsset;
import com.fatwire.mda.Dimension;

import java.util.*;

/**
 * Simple node, representing an asset, that can be populated with asset data. Not all attributes
 * of the asset are necessarily loaded into this node. Many convenience methods exist for retrieving
 * node attribute data.
 * @author Tony Field
 * @since 2016-07-06
 */
public class SimpleAssetNode implements AssetNode {

    private final AssetId id;
    private TemplateAsset asset = null;
    Node parent = null;
    ArrayList<Node> children = new ArrayList<>();

    SimpleAssetNode(AssetId id) {
        this.id = id;
    }

    void setAsset(TemplateAsset asset) {
        this.asset = asset;
    }

    void setParent(Node parent) {
        this.parent = parent;
    }

    void addChild(int rank, Node child) {
        List<Node> kids = Collections.singletonList(child);
        children.addAll(rank, kids);

        //clean up
        for (Iterator<Node> it = children.iterator(); it.hasNext();) {
            Node n = it.next();
            if (n == null) {
                it.remove();
            }
        }
        children.trimToSize();
    }

    public AssetId getId() {
        return id;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getSiblings() {
        return parent.getChildren();
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<Node> getBreadcrumb() {
        List<Node> ancestors = new ArrayList<>();
        Node node = this;
        do {
            ancestors.add(node);
            node = node.getParent();
        } while (node != null);
        Collections.reverse(ancestors);
        return ancestors;
    }

    public AssetId asAssetId(String name) {
        return asset.asAssetId(name);
    }

    public BlobObject asBlob(String name) {
        return asset.asBlob(name);
    }

    public BlobObject.BlobAddress asBlobAddress(String name) {
        return asset.asBlobAddress(name);
    }

    public Date asDate(String name) {
        return asset.asDate(name);
    }

    public Double asDouble(String name) {
        return asset.asDouble(name);
    }

    public Float asFloat(String name) {
        return asset.asFloat(name);
    }

    public Integer asInt(String name) {
        return asset.asInt(name);
    }

    public List<?> asList(String name) {
        return asset.asList(name);
    }

    public Long asLong(String name) {
        return asset.asLong(name);
    }

    public String asString(String name) {
        return asset.asString(name);
    }

    public AssetTypeDef getAssetTypeDef() {
        return asset.getAssetTypeDef();
    }

    public AssetId getAssociatedAsset(String name) {
        return asset.getAssociatedAsset(name);
    }

    public List<AssetId> getAssociatedAssets(String name) {
        return asset.getAssociatedAssets(name);
    }

    public Object getAttribute(String name) {
        return asset.getAttribute(name);
    }

    public AssetData getAssetData() {
        return asset.getDelegate();
    }

    public AttributeData getAttributeData(String name, boolean meta) {
        return asset.getAttributeData(name, meta);
    }

    public List<String> getAttributeNames() {
        return asset.getAttributeNames();
    }

    public List<AssetId> getImmediateParents(String name) throws AssetAccessException {
        return asset.getImmediateParents(name);
    }

    public Dimension getLocale() {
        return asset.getLocale();
    }

    public Object getMetaAttribute(String name) {
        return asset.getMetaAttribute(name);
    }

    public List<String> getMetaAttributeNames() {
        return asset.getMetaAttributeNames();
    }

    public List<AssetId> getParents() throws AssetAccessException {
        return asset.getParents();
    }

    public String getSubtype() {
        return asset.getSubtype();
    }

    public AttributeTypeEnum getType(String name) {
        return asset.getType(name);
    }

    public boolean isAttribute(String name) {
        return asset.isAttribute(name);
    }

    public boolean isSingleValued(String name) {
        return asset.isSingleValued(name);
    }
}
