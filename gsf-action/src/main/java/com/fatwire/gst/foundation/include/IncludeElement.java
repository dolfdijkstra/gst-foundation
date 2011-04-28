/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.include;

import java.util.Date;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.render.CallElement;
import com.fatwire.gst.foundation.facade.runtag.render.CallElement.Scope;

/**
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 */
public class IncludeElement implements Include {

    private CallElement tag;
    private final ICS ics;

    /**
     * @param ics
     * @param elementname
     */
    public IncludeElement(ICS ics, String elementname) {
        tag = new CallElement(elementname);
        this.ics = ics;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.include.Include#include(COM.FutureTense.Interfaces
     * .ICS)
     */
    public void include(ICS ics) {
        String s = tag.execute(ics);
        if (s != null)
            ics.StreamText(s);
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, Date value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, long value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, int value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, boolean value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name
     * @param value
     * @return this
     */
    public IncludeElement argument(String name, byte[] value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Copies the ics variables identified by the name array
     * 
     * @param name
     * @return this
     */
    public IncludeElement copyArguments(final String... name) {
        if (name == null) {
            return this;
        }
        for (final String n : name) {
            argument(n, ics.GetVar(n));
        }
        return this;
    }

    /**
     * @param scope
     * @see com.fatwire.gst.foundation.facade.runtag.render.CallElement#setScope(com.fatwire.gst.foundation.facade.runtag.render.CallElement.Scope)
     */
    public IncludeElement setScope(Scope scope) {
        tag.setScope(scope);
        return this;
    }

    public IncludeElement global() {
        tag.setScope(Scope.GLOBAL);
        return this;
    }

    public IncludeElement stacked() {
        tag.setScope(Scope.STACKED);
        return this;
    }

    public IncludeElement local() {
        tag.setScope(Scope.LOCAL);
        return this;
    }

}
