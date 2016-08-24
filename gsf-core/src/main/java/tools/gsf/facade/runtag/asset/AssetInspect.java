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

import tools.gsf.facade.runtag.AbstractTagRunner;

/**
 * {@literal <ASSET.INSPECT LIST="list name" TYPE="asset type" [PUBID="publication ID"]}
 * {@literal [SUBTYPE="asset subtype"] />}
 *
 * @author Tony Field
 * @since 21-Nov-2008
 */
public final class AssetInspect extends AbstractTagRunner {
    public AssetInspect() {
        super("ASSET.INSPECT");
    }

    public void setList(String s) {
        set("LIST", s);
    }

    public void setType(String s) {
        set("TYPE", s);
    }

    public void setPubid(String s) {
        set("PUBID", s);
    }

    public void setSubtype(String s) {
        set("SUBTYPE", s);
    }
}
