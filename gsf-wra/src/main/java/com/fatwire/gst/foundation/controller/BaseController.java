/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.FTVAL;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.XML.Template.Seed2;

/**
 * <p>
 * This is the controller (dispatcher) that dispatches the request to the
 * correct asset/template combination based on the path field for a Web
 * Referenceable Asset.
 * </p>
 * <p/>
 * This controller should be called from an outer XML element via the
 * <tt>CALLJAVA</tt> tag:
 * <code>&lt;CALLJAVA CLASS="com.fatwire.gst.foundation.controller.BaseController" /&gt;
 * </code>
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since Jun 10, 2010
 */
public class BaseController implements Seed2 {

    private RenderPageAdapter delegate;
    private FTValList vIn;

    /* (non-Javadoc)
     * @see COM.FutureTense.XML.Template.Seed2#SetAppLogic(COM.FutureTense.Interfaces.IPS)
     */
    @Override
    public void SetAppLogic(IPS ips) {
        delegate = new RenderPageAdapter(ips.GetICSObject());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * COM.FutureTense.XML.Template.Seed#Execute(COM.FutureTense.Interfaces.
     * FTValList, COM.FutureTense.Interfaces.FTValList)
     */

    public final String Execute(final FTValList vIn, final FTValList vOut) {
        this.vIn = vIn;
        try {
            doExecute();
        } catch (final Exception e) {
            handleException(e);
        }

        return "";
    }

    protected final FTVAL getInputArgument(String name) {
        return vIn == null ? null : vIn.getVal(name);
    }

    protected final String getInputArgumentAsString(String name) {
        return vIn == null ? null : vIn.getValString(name);
    }


    protected void doExecute() {
        delegate.doExecute();
    }

    protected void handleException(final Exception e) {
        delegate.handleException(e);
    }

}
