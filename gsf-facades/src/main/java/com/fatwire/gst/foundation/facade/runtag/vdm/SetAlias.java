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

package com.fatwire.gst.foundation.facade.runtag.vdm;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * {@code <VDM.SETALIAS KEY="keyvalue" VALUE="aliasvalue"/>}
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetAlias extends AbstractTagRunner {
    public SetAlias() {
        super("VDM.SETALIAS");
    }

    public SetAlias(String key, String value) {
        this();
        setKey(key);
        setValue(value);
    }

    public void setKey(String key) {
        set("KEY", key);
    }

    public void setValue(String value) {
        set("VALUE", value);
    }
}
