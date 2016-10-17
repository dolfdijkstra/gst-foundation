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

package tools.gsf.facade.assetapi;

import COM.FutureTense.Interfaces.IList;
import com.fatwire.assetapi.data.AssetId;
import tools.gsf.facade.sql.AbstractIList;
import tools.gsf.facade.sql.IListIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * IList implementation that starts with a List{@literal<AssetId>} and exposes the rows as
 * ASSETTYPE,ASSETID.  The getter is case-insensitive, so assettype,assetid works too.
 *
 * @author Tony Field
 * @see IListIterable
 * @since Aug 13, 2010
 */
public class AssetIdIList extends AbstractIList {

    public static final String ASSETTYPE = "ASSETTYPE";
    public static final String ASSETID = "ASSETID";
    /**
     * Collection of AssetId objects
     */
    protected List<AssetId> ids;

    /**
     * Construct a new IList taking the name and the data.
     *
     * @param name IList name
     * @param ids  asset ids.
     */
    public AssetIdIList(String name, Collection<AssetId> ids) {
        super(name);
        this.ids = Collections.unmodifiableList(new ArrayList<AssetId>(ids));
    }

    public IList clone(String newname) {
        List<AssetId> clone = new ArrayList<AssetId>(ids.size());
        for (AssetId id : ids) {
            clone.add(id);
        }
        return new AssetIdIList(newname, clone);
    }

    public void flush() {
        ids = new ArrayList<AssetId>();
    }

    public int numColumns() {
        return 2;
    }

    public String getColumnName(int i) throws ArrayIndexOutOfBoundsException {
        if (i == 0) {
            return ASSETTYPE;
        }
        if (i == 1) {
            return ASSETID;
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }

    public int numRows() {
        return ids.size();
    }

    public String getValue(String s) throws NoSuchFieldException {
        // ID is column 2 but will likely be called more often that type so
        // check it first.
        if (ASSETID.equalsIgnoreCase(s)) {
            return Long.toString(ids.get(currentRow() - 1).getId());
        }
        if (ASSETTYPE.equalsIgnoreCase(s)) {
            return ids.get(currentRow() - 1).getType();
        }
        throw new NoSuchFieldException(s);
    }

    public Object getObject(String s) throws NoSuchFieldException {
        return getValue(s);
    }

    public byte[] getFileData(String s) throws IllegalArgumentException, NoSuchFieldException {
        throw new IllegalArgumentException(s);
    }

    public String getFileString(String s) throws NoSuchFieldException {
        throw new IllegalArgumentException(s);
    }

    public int numIndirectColumns() {
        return 0;
    }

    public String getIndirectColumnName(int i) {
        return null;
    }

    public boolean stringInList(String s) {
        return false;
    }
}
