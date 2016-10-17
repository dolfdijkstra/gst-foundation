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

package tools.gsf.facade.runtag.vdm;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.facade.runtag.TagRunner;

/**
 * Sets a scalar attribute value without resetting it if it has already been set
 * to the same value.
 *
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetScalarWithoutReset implements TagRunner {

    private final String attribute;

    private final String value;

    public SetScalarWithoutReset(final String attribute, final String value) {
        super();
        this.attribute = attribute;
        this.value = value;
    }

    public String execute(final ICS ics) {
        final String varname = "get_scalar_output_value" + ics.genID(true);
        try {
            ics.RemoveVar(varname);
            GetScalar getScalar = new GetScalar(attribute, varname);
            String getResult = getScalar.execute(ics);
            String attrVal = ics.GetVar(varname);
            if (attrVal != null && attrVal.equals(value) || attrVal == null && value == null) {
                // nothing to do. this saves a lot of processing
                return getResult;
            }

            SetScalar setScalar = new SetScalar(attribute, value);
            return setScalar.execute(ics);
        } finally {
            // cleaning up
            ics.RemoveVar(varname);
        }
    }

    public String getValue() {
        return value;
    }

    public String getAttribute() {
        return attribute;
    }
}
