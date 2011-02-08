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

package com.fatwire.gst.foundation.facade.runtag.user;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;
import COM.FutureTense.Util.ftErrors;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;
import com.fatwire.gst.foundation.facade.runtag.TagRunnerRuntimeException;

/**
 * <user.login>
 * 
 * @author Tony Field
 * @since Feb 1, 2011
 */
public final class Login extends AbstractTagRunner {
    public Login() {
        super("user.login");
    }

    public void setUsername(String s) {
        if (!Utilities.goodString(s))
            throw new IllegalArgumentException("Username cannot be null");
        set("username", s);
    }

    public void setPassword(String s) {
        if (!Utilities.goodString(s))
            throw new IllegalArgumentException("Password cannot be null");
        set("password", s);
    }

    public static boolean login(ICS ics, String username, String password) {
        Login l = new Login();
        l.setUsername(username);
        l.setPassword(password);
        try {
            l.execute(ics);
        } catch (TagRunnerRuntimeException e) {
            if (e.getErrno() == ftErrors.badpassword || e.getErrno() == ftErrors.unknownuser) {
                return false;
            } else
                throw e;
        }
        return true;
    }

}
