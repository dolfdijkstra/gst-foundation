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

package com.fatwire.gst.foundation.facade.runtag.render;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <RENDER.UNKNOWNDEPS [ASSETTYPE="assettype"]/>
 *
 * @author Tony Field
 * @since Mar 10, 2011
 */
public final class Unknowndeps extends AbstractTagRunner {
    public Unknowndeps() {
        super("RENDER.UNKNOWNDEPS");
    }

    public void setAssettype(String s) {
        set("ASSETTYPE", s);
    }

    public static void unknonwDeps(ICS ics, String c) {
        Unknowndeps ud = new Unknowndeps();
        ud.setAssettype(c);
        ud.execute(ics);
    }

    public static void unknonwDeps(ICS ics) {
        Unknowndeps ud = new Unknowndeps();
        ud.execute(ics);
    }
}