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

package com.fatwire.gst.foundation.facade.runtag.listobject;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * {@code <LISTOBJECT.TOLIST NAME="listname" LISTVARNAME="varname"/> } 
 * 
 * @author Tony Field
 * @since Oct 24, 2008
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public final class ToList extends AbstractTagRunner {
    public ToList() {
        super("LISTOBJECT.TOLIST");
    }

    public void setName(String s) {
        set("NAME", s);
    }

    public void setListVarName(String s) {
        set("LISTVARNAME", s);
    }
}
