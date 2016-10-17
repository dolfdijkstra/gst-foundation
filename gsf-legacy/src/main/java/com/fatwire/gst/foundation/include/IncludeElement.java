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
 * Include implementation for CallElement.
 * 
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 * 
 * @deprecated as of release 12.x, replace with OOTB features (e.g. callelement tag, calltemplate tag, ics.RunTag and the like)
 * 
 */
public class IncludeElement implements Include {

    private final CallElement tag;
    private final ICS ics;

    /**
     * @param ics Content Server context object
     * @param elementname element name to be called
     */
    public IncludeElement(final ICS ics, final String elementname) {
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
    public void include(final ICS ics) {
        final String s = tag.execute(ics);
        if (s != null) {
            ics.StreamText(s);
        }
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for the specified value
     * @param value value referenced by the key, name
     * @return this returns an IncludeElement
     */
    public IncludeElement argument(final String name, final String value) {
        tag.setArgument(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for specified value
     * @param value value referenced by the key, name
     * @return this
     */
    public IncludeElement argument(final String name, final Date value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for specified value
     * @param value value referenced by the key, name
     * @return this
     */
    public IncludeElement argument(final String name, final long value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for specified value
     * @param value value referenced by the key, name
     * @return this
     */
    public IncludeElement argument(final String name, final int value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for specified value
     * @param value value referenced by the key, name
     * @return this
     */
    public IncludeElement argument(final String name, final boolean value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Adds the name value pair as an argument to the CallElement tag.
     * 
     * @param name name for specified value
     * @param value value referenced by the key, name
     * @return this
     */
    public IncludeElement argument(final String name, final byte[] value) {
        tag.set(name, value);
        return this;
    }

    /**
     * Copies the ics variables identified by the name array
     * 
     * @param name name for specified value
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
     * @param scope scope object
     * @see "com.fatwire.gst.foundation.facade.runtag.render.CallElement#setScope(com.fatwire.gst.foundation.facade.runtag.render.CallElement.Scope)"
     * @return this include element
     */
    public IncludeElement setScope(final Scope scope) {
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
