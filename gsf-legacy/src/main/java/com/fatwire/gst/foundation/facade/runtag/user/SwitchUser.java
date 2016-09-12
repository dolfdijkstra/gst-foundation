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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

/**
 * This class provides the user.su body tag functionality. Unfortunately, RunTag
 * does not support body tags with actions in both the starttag and endtag, and
 * this functionality requires this. Instead, this class is stateful and
 * contains a switchTo() and switchBack() method.
 * <p>
 * Note that this class is NOT THREAD SAFE.
 * 
 * @author Tony Field
 * @since Feb 1, 2011
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class SwitchUser {
    private final ICS ics;

    private Map<String, String> vars;
    private Map<String, String> ssvars;

    public SwitchUser(ICS ics) {
        if (ics == null)
            throw new IllegalArgumentException("ICS cannot be null");
        this.ics = ics;
    }

    /**
     * Switch from the current user to a new user, (if username and password are
     * correct). If Username and password aren't correct, nothing happens. The
     * new user sees no variables or session variables from the previous user,
     * but lists and objects in the object pool are not affected.
     * 
     * @param username new userid. Null is not allowed
     * @param password new password. Null is not allowed.
     * @return true on success, false on login failure
     */
    public boolean switchTo(String username, String password) {

        saveState();
        clearState();
        if (!Login.login(ics, username, password)) {
            restoreState();
            vars = null;
            ssvars = null;
            return false;
        }

        return true;
    }

    /**
     * Switch back to the previous user.
     */
    public void switchBack() {

        if (vars == null || ssvars == null)
            throw new IllegalStateException("Can't \"switch back\" without \"switching to\" first.");

        Logout.logout(ics);
        clearState();
        restoreState();

        vars = null;
        ssvars = null;
    }

    private void restoreState() {
        for (String key : vars.keySet()) {
            ics.SetVar(key, vars.get(key));
        }
        for (String key : ssvars.keySet()) {
            ics.SetSSVar(key, ssvars.get(key));
        }
    }

    private void clearState() {
        Enumeration<?> e = ics.GetVars();
        while (e.hasMoreElements()) {
            ics.RemoveVar((String) e.nextElement());
        }
        e = ics.GetSSVars();
        while (e.hasMoreElements()) {
            ics.RemoveSSVar((String) e.nextElement());
        }
    }

    private void saveState() {
        vars = new HashMap<String, String>();
        Enumeration<?> e = ics.GetVars();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            vars.put(key, ics.GetVar(key));
        }
        ssvars = new HashMap<String, String>();
        e = ics.GetSSVars();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            ssvars.put(key, ics.GetSSVar(key));
        }
    }
}
