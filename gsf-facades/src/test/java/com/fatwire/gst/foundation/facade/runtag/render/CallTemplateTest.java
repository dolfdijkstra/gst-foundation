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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

public class CallTemplateTest extends TestCase {

    public void testFixPageCriteria() {
        x();
    }

    void x() {
        String[] pc = { "locale", "p" };
        Map<String, Object> m = new HashMap<String, Object>();

        m.put("TTYPE", "foo");
        m.put("ARGS_locale", "en");
        m.put("ARGS_foo", "foo");
        m.put("ARGS_TNAME", "foo");
        for (final Iterator<Entry<String, Object>> i = m.entrySet().iterator(); i.hasNext();) {
            final Entry<String, ?> e = i.next();
            final String key = e.getKey();
            // only inspect arguments that start with ARGS_
            if (key.startsWith(CallTemplate.ARGS)) {

                String shortKey = key.substring(CallTemplate.ARGS.length());
                boolean found = CallTemplate.CALLTEMPLATE_EXCLUDE_VARS.contains(shortKey);
                System.out.println(key +" " + found);
                if (!found) {
                    for (final String c : pc) {
                        if (c.equalsIgnoreCase(shortKey)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    System.out.println("Argument '" + key + "' not found as PageCriterium " + ". Arguments are: "
                            + m.keySet().toString() + ". PageCriteria: " + Arrays.asList(pc));
                    // we could correct this by calling as an element
                    // or by removing the argument

                    i.remove();
                    System.out.println("Argument '" + key + "' is removed from the call as it is not a PageCriterium.");
                }

            }
        }
    }

}
