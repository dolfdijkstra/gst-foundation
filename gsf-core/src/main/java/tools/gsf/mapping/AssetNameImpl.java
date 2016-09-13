/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package tools.gsf.mapping;

/**
 * Class that holds asset type and asset name, similar to AssetId.
 *
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * @see com.fatwire.assetapi.data.AssetId
 */
public final class AssetNameImpl implements AssetName {

    private final String type;
    private final String name;

    /**
     * @param type asset type
     * @param name asset name
     */
    public AssetNameImpl(final String type, final String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return type + ":" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssetNameImpl assetName = (AssetNameImpl) o;

        if (type != null ? !type.equals(assetName.type) : assetName.type != null) {
            return false;
        }
        return name != null ? name.equals(assetName.name) : assetName.name == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
