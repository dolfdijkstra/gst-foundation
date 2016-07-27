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

package com.fatwire.gst.foundation.facade.runtag.asset;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

public abstract class AbstractAssetLoad extends AbstractTagRunner {

    public static final String DEPTYPE_EXACT = "exact";
    public static final String DEPTYPE_EXISTS = "exists";
    public static final String DEPTYPE_GREATER = "greater";
    public static final String DEPTYPE_NONE = "none";

    public static final String OPTION_EDITABLE = "editable";
    public static final String OPTION_READ_ONLY = "readonly";
    public static final String OPTION_READ_ONLY_COMPLETE = "readonly_complete";

    public AbstractAssetLoad() {
        super("asset.load");
    }

    public void setName(final String name) {
        this.set("NAME", name);
    }

    public void setAssetType(final String type) {
        this.set("TYPE", type);
    }

    public void setSite(final String site) {
        this.set("SITE", site);
    }

    public void setDepType(final String type) {
        this.set("DEPTYPE", type);
    }

    public void setEditable(final boolean value) {
        this.set("EDITABLE", value);
    }

    public void setOption(final String option) {
        this.set("OPTION", option);
    }

    public void setFlushOnVoid(final boolean flush) {
        this.set("FLUSHONVOID", flush);
    }

}
