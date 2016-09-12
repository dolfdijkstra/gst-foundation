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

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.openmarket.xcelerate.asset.AssetIdImpl;
import tools.gsf.facade.runtag.AbstractTagRunner;

/**
 * {@literal <ASSET.GETSUBTYPE [NAME="loaded asset]" [TYPE="assettype]"
 * [OBJECTID="asset id]" [OUTPUT="variable name"] />}
 *
 * @author Tony Field
 * @since Oct 7, 2008
 */
public class GetSubtype extends AbstractTagRunner {
    public GetSubtype() {
        super("ASSET.GETSUBTYPE");
    }

    public void setAssetId(AssetId id) {
        setType(id.getType());
        setObjectid(id.getId());
    }

    public void setName(String s) {
        this.set("NAME", s);
    }

    public void setType(String s) {
        this.set("TYPE", s);
    }

    public void setObjectid(long id) {
        this.set("OBJECTID", Long.toString(id));
    }

    public void setOutput(String s) {
        this.set("OUTPUT", s);
    }

    /**
     * Get the subtype of the specified asset. The asset does not need to be
     * loaded.
     *
     * @param ics ICS context
     * @param id  the Id of the asset to return the subtype for.
     * @return subtype on success
     */
    public static String getSubtype(ICS ics, AssetId id) {
        ics.PushVars();
        GetSubtype gs = new GetSubtype();
        gs.setAssetId(id);
        gs.setOutput("st");
        gs.execute(ics);
        String ret = ics.GetVar("st");
        ics.PopVars();
        return ret;
    }

    /**
     * Get the subtype of the specified asset. The asset does not need to be
     * loaded.
     *
     * @param ics ICS context
     * @param c   the type of the asset to return the subtype for.
     * @param cid the id of the asset to return the subtype for.
     * @return subtype on success
     */
    public static String getSubtype(ICS ics, String c, String cid) {
        return getSubtype(ics, new AssetIdImpl(c, Long.valueOf(cid)));
    }

    /**
     * Get the subtype for the specified loaded asset.
     *
     * @param ics             ICS context
     * @param loadedAssetName object pool handle for the asset
     * @return the subtype on success
     */
    public static String getSubtype(ICS ics, String loadedAssetName) {
        ics.PushVars();
        GetSubtype gs = new GetSubtype();
        gs.setName(loadedAssetName);
        gs.setOutput("st");
        gs.execute(ics);
        String ret = ics.GetVar("st");
        ics.PopVars();
        return ret;
    }
}
