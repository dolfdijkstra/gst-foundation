package tools.gsf.navigation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fatwire.assetapi.data.AssetId;

import tools.gsf.facade.assetapi.asset.TemplateAsset;
import tools.gsf.facade.assetapi.asset.TemplateAssetAccess;
import tools.gsf.navigation.AssetNode;

public class TrivialAssetNodeImpl implements AssetNode<TrivialAssetNodeImpl> {
	
	private static final long serialVersionUID = -7637446633778028560L;

	private final AssetId id;
    private TrivialAssetNodeImpl parent = null;
    private ArrayList<TrivialAssetNodeImpl> children = new ArrayList<>();
    private TemplateAsset asset;
	
	public TrivialAssetNodeImpl(TemplateAssetAccess taa, AssetId assetId) {
		this.id = assetId;
		
		// NOTE: you may gather the data to be exposed by this implementation
		//       specific getters (getWraUrl, etc... e.g. project-specific ones) here
		//       or we could do it upon demand, inside each getter method. 
	    this.asset = taa.read(assetId, "name", "template");
	}

	public String getAssetName() { 
		return this.asset.asString("name");
	}
	
	public String getAssetTemplate() {
		return this.asset.asString("template");
	}
	
	// NOTE: we could add as many getters as data we need to expose for our NavService implementation
	//       OR you could even have this class extend HashMap... the point is you have full control
	//       over what you expose and how.
	
	@Override
	public AssetId getId() {
		return this.id;
	}
	
	@Override
    public void addChild(TrivialAssetNodeImpl child, int rank) {
        while (children.size() < rank) children.add(null);
        children.set(rank-1, child);
    }

	@Override
	public void addChild(TrivialAssetNodeImpl node) {
		children.add(node);
	}

	@Override
    public void setParent(TrivialAssetNodeImpl parent) {
        this.parent = parent;
    }
	
	@Override
    public TrivialAssetNodeImpl getParent() {
        return parent;
    }

	@Override
    public List<TrivialAssetNodeImpl> getSiblings() {
        return parent.getChildren();
    }

	@Override
    public List<TrivialAssetNodeImpl> getChildren() {
        return children.stream().filter(n -> n != null).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "TrivialAssetNodeImpl{" +
                "id=" + id +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TrivialAssetNodeImpl that = (TrivialAssetNodeImpl) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) {
            return false;
        }
        return children.equals(that.children);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (parent != null ? parent.id.hashCode() : 0);
        result = 31 * result + children.hashCode();
        return result;
    }
	
}