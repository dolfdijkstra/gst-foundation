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

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * &lt;RENDER.LOOKUP KEY="name of lookup key" VARNAME="output variable name"
 * SITE="site name" [TID="id of template or cselement"]
 * [TTYPE="CSElement|Template"] [MATCH="x|x:|:x"] /&gt;
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public class Lookup extends AbstractTagRunner {
    public enum Match {
        X("x"), XCOLON("x:"), COLONX(":x");
        private final String pattern;

        Match(String pattern) {
            this.pattern = pattern;
        }
    }

   

    public Lookup() {
        super("RENDER.LOOKUP");
    }

    public void setKey(String key) {
        set("KEY", key);
    }

    public void setVarname(String name) {
        set("VARNAME", name);
    }

    public void setSite(String site) {
        set("SITE", site);
    }

    public void setTid(String tid) {
        set("TID", tid);
    }

    public void setTtype(CallTemplate.Type ttype) {
        set("TTYPE", ttype.name());
    }

    public void setMatch(Match match) {
        set("MATCH", match.pattern);
    }

}
