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

package tools.gsf.facade.runtag.xlat;

import COM.FutureTense.Interfaces.ICS;
import tools.gsf.facade.runtag.AbstractTagRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper around the XLAT.STREAM xml tag
 *
 * @author Mike Field
 * @since August 15, 2008
 */
public final class Stream extends AbstractTagRunner {
    // Default Constructor
    public Stream() {
        super("XLAT.STREAM");
    }

    /**
     * Sets key to the value of <code>s</code>
     *
     * @param s The key of the db entry to return
     */
    public void setKey(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid key string: " + s);
        }
        this.set("KEY", s);
    }

    /**
     * Sets locale to the value of <code>s</code>
     *
     * @param s The locale of the db entry
     */
    public void setLocale(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid locale string: " + s);
        }
        this.set("LOCALE", s);
    }

    /**
     * Sets encode to the value of <code>s</code>
     *
     * @param s true or false (case sensitive)
     */
    public void setEncode(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid encode string: " + s);
        }
        this.set("ENCODE", s);
    }

    /**
     * Sets escape to the value of <code>s</code>
     *
     * @param s true or false (case sensitive)
     */
    public void setEscape(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid escape string: " + s);
        }
        this.set("ESCAPE", s);
    }

    /**
     * Sets evalall to the value of <code>s</code>
     *
     * @param s true or false (case sensitive)
     */
    public void setEvalAll(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid evalall string: " + s);
        }
        this.set("EVALALL", s);
    }

    /**
     * Sets the name and value of a custome argument to
     * <code>argname=argvalue</code>
     *
     * @param argname  The name of the argument
     * @param argvalue The name of the value
     */
    public void setArgument(String argname, String argvalue) {
        // validate first
        if (argname == null || argname.length() == 0) {
            throw new IllegalArgumentException("Invalid argname string: " + argname);
        }
        if (argvalue == null || argvalue.length() == 0) {
            throw new IllegalArgumentException("Invalid argvalue string: " + argvalue);
        }
        this.set(argname, argvalue);
    }

    protected void preExecute(ICS ics) {
        // work around a bug in the tag where a variable needs to exist for the
        // mapping to work
        super.preExecute(ics);

        List<String> newVars = new ArrayList<String>();
        for (Object oKey : list.keySet()) {
            String sKey = (String) oKey;
            if (ics.GetVar(sKey) == null) {
                newVars.add(sKey);
                ics.SetVar(sKey, list.getValString(sKey));
            }
        }
        ics.SetObj("NewVars", newVars);
    }

    @SuppressWarnings("unchecked")
    protected void postExecute(ICS ics) {
        super.postExecute(ics);
        List<String> newVars = (List<String>) ics.GetObj("NewVars");
        for (String toRemove : newVars) {
            ics.RemoveVar(toRemove);
        }
        ics.SetObj("NewVars", null);
    }

}
