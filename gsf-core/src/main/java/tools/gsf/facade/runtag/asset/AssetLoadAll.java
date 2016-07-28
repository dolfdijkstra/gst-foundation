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
package tools.gsf.facade.runtag.asset;

import com.fatwire.assetapi.data.AssetId;
import tools.gsf.facade.runtag.AbstractTagRunner;

import java.util.List;

/**
 * &gt;ASSET.LOADALL
 * TYPE="asset type"
 * [LIST="list object name"]
 * [IDS="asset IDs"]
 * [IDFIELD="field name"]
 * PREFIX="prefix"
 * [INITCOUNT="integer"]
 * [DEPTYPE="exact|exists|none"]
 * [EDITABLE="true|false"]
 * [OPTION="editable|readonly|readonly_complete"]/&lt;
 *
 * @author Tony Field
 * @since 2011-04-07
 */
public final class AssetLoadAll extends AbstractTagRunner {
    public AssetLoadAll() {
        super("ASSET.LOADALL");
    }

    public void setType(String s) {
        set("TYPE", s);
    }

    public void setList(String s) {
        set("LIST", s);
    }

    public void setIds(String s) { // todo: add IList of IDs
        set("IDS", s);
    }

    public void setIds(List<AssetId> ids) {
        StringBuilder s = new StringBuilder();
        for (AssetId id : ids) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(Long.toString(id.getId()));
        }
        setIds(s.toString());
    }

    public void setIdfield(String s) {
        set("IDFIELD", s);
    }

    public void setPrefix(String s) {
        set("PREFIX", s);
    }

    public void setInitCount(int i) {
        set("INITCOUNT", Integer.toString(i));
    }

    public void setDeptype(String s) {  // todo: type enum?
        set("DEPTYPE", s);
    }

    public void setEditable(boolean b) {
        set("EDITABLE", Boolean.toString(b));
    }

    public void setOption(String s) {  // todo: type enum?
        set("OPTION", s);
    }


}
