/*
 * Copyright 2016 Function1. All Rights Reserved.
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
package tools.gsf.navigation.siteplan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fatwire.assetapi.data.AssetId;

import tools.gsf.navigation.AssetNode;
import tools.gsf.navigation.ConfigurableNode;

/**
 * Simple node, representing an asset, that can be populated with asset data.
 *  
 * It is up to the concrete class extending this base class to decide what data to
 * expose and how.
 * 
 * This base class just deals with the annoying stuff required by most (all?)
 * AssetNode implementations.
 *  
 * @author Freddy Villalba
 * @since 2017-03-02.
 */
public abstract class AbstractAssetNode<NODE extends AssetNode<NODE> & ConfigurableNode<NODE>> implements AssetNode<NODE>, ConfigurableNode<NODE> {

	private static final long serialVersionUID = -7637446633778028560L;

	protected AssetId id;
	protected NODE parent = null;
	protected ArrayList<NODE> children = new ArrayList<>();

	public AbstractAssetNode(AssetId assetId) {
		this.id = assetId;		
	}

	public AssetId getId() {
		return this.id;
	}
	
	@Override
	public void addChild(NODE node) {
		children.add(node);
	}

	@Override
    public void setParent(NODE parent) {
        this.parent = parent;
    }
	
	@Override
	public void removeChildren() {
		for (NODE child : this.children) {
			child.setParent(null);
		}
		this.children.clear();
	}
	
	public boolean removeChild(NODE child) {
		return this.children.remove(child);
	}
	
	@Override
    public NODE getParent() {
        return parent;
    }

	@Override
    public List<NODE> getSiblings() {
        return parent.getChildren();
    }

	@Override
    public List<NODE> getChildren() {
        return children.stream().filter(n -> n != null).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssetNode<?> that = (AssetNode<?>) o;

        if (! this.id.equals(that.getId())) {
            return false;
        }
        if (this.parent != null ? !this.parent.equals(that.getParent()) : that.getParent() != null) {
            return false;
        }
        return this.children.equals(that.getChildren());

    }

    @Override
    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + (this.parent != null ? this.parent.getId().hashCode() : 0);
        result = 31 * result + this.children.hashCode();
        return result;
    }

}