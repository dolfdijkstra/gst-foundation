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

import java.util.Locale;

public class CallElement extends TagRunnerWithRenderArguments {

    public static final String SCOPE_GLOBAL = "global";
    public static final String SCOPE_LOCAL = "local";
    public static final String SCOPE_STACKED = "stacked";

    public enum Scope {
        GLOBAL, LOCAL, STACKED;
        private final String v;

        Scope() {
            v = name().toLowerCase(Locale.ENGLISH);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return v;
        }

    }

    public CallElement() {
        super("RENDER.CALLELEMENT");
    }

    public CallElement(final String element) {
        super("RENDER.CALLELEMENT");
        this.setElementName(element);
    }

    public void setElementName(final String element) {
        this.set("ELEMENTNAME", element);
    }

    public void setScope(final String scope) {
        this.set("SCOPED", scope);
    }

    public void setScope(final Scope scope) {
        this.set("SCOPE", scope.toString());
    }

}
