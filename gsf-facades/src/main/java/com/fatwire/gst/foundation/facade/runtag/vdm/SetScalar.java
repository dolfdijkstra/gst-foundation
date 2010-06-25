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
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.Util.ftStatusCode;

import com.fatwire.gst.foundation.CSRuntimeException;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <VDM.SETSCALAR ATTRIBUTE="attribute" VALUE="value"/>
 * 
 * @author Tony Field
 * @since Sep 29, 2008
 */
public class SetScalar extends AbstractTagRunner {

    public SetScalar(String attribute, String value) {
        this();
        setAttribute(attribute);
        setValue(value);
    }

    public SetScalar() {
        super("VDM.SETSCALAR");
    }

    public void setAttribute(String attr) {
        set("ATTRIBUTE", attr);
    }

    public void setValue(String val) {
        set("VALUE", val);
    }

    public String execute(ICS ics) {
        String s = super.execute(ics);

        // Update from Tony Field September 19, 2009
        // for some oddball reason, setscalar can fail with a database error
        // (unique constraint violation)
        // and the tag does not report errno. However it does return a status
        // code in a variable called
        // cshttp. We can look into this variable to get the code, parse it, and
        // extract errno from it.
        // Wow this is nasty...

        String statusCode = ics.GetVar("cshttp");
        ftStatusCode sc = new ftStatusCode();
        if (sc.setFromData(statusCode)) {
            int errno = sc.getErrorID();
            switch (errno) {
                case ftErrors.success:
                    return s;
                case ftErrors.dberror: {
                    throw new CSRuntimeException(
                            "SetScalar failed with a database error.  It was returned in a ftStatusCode.  StatusCode:"
                                    + statusCode, -13704);
                }
                default: {
                    throw new CSRuntimeException(
                            "SetScalar failed with an unexpected error.  It was returned in a ftStatusCode.  StatusCode:"
                                    + statusCode, -13704);
                }
            }
        }
        return s;
    }
}
