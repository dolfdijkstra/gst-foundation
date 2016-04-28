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

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.TagRunner;

/**
 * Sets an alias ONLY if it has not been set before. Avoids unnecessary DB
 * thrash.
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetAliasWithoutReset implements TagRunner {

    private final String key;

    private final String value;

    public SetAliasWithoutReset(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String execute(final ICS ics) {
        final String varname = "alias" + ics.genID(false);
        try {
            GetAlias getAlias = new GetAlias(key, varname);
            String getResult = getAlias.execute(ics);
            String alias = ics.GetVar(varname);
            if (alias != null && alias.equals(value) || alias == null && value == null) {
                // nothing to do. this saves a lot of processing
                return getResult;
            }

            SetAlias setAlias = new SetAlias(key, value);
            return setAlias.execute(ics);

        } finally {
            // cleaning up
            ics.RemoveVar(varname);
        }
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

}
