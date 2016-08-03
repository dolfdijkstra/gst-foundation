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
package tools.gsf.facade.runtag.render;

/**
 * * &lt;RENDER.SATELLITEPAGE&gt;
 *
 * @author Dolf Dijkstra
 * @since Apr 11, 2011
 */
public class SatellitePage extends TagRunnerWithRenderArguments {

    public SatellitePage() {
        super("RENDER.SATELLITEPAGE");
    }

    /**
     * @param pagename string value for page name
     */
    public SatellitePage(String pagename) {
        super("RENDER.SATELLITEPAGE");
        setPagename(pagename);
    }

    public void setPagename(String s) {
        set("PAGENAME", s);
    }

    public void setPackedArgs(String s) {
        set("PACKEDARGS", s);
    }

}
