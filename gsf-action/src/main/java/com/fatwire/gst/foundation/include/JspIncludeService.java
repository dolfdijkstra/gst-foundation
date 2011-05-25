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

import java.util.HashMap;
import java.util.Map;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.render.CallTemplate.Style;

import org.springframework.util.Assert;

/**
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class JspIncludeService implements IncludeService {

    private final ICS ics;
    private final Map<String, Include> map = new HashMap<String, Include>();

    public JspIncludeService(final ICS ics) {
        this.ics = ics;
    }

    public IncludeElement element(final String name, final String elementname) {
        Assert.hasText(name);
        Assert.hasText(elementname);
        final IncludeElement i = new IncludeElement(ics, elementname);
        map.put(name, i);
        return i;
    }

    public IncludePage page(final String name, final String pagename, final Style style) {
        Assert.hasText(name);
        Assert.hasText(pagename);
        final IncludePage i = new IncludePage(ics, pagename, style == null ? Style.pagelet : style);
        map.put(name, i);
        return i;

    }

    public IncludeTemplate template(final String name, final AssetId asset, final String tname) {
        Assert.hasText(name);
        Assert.hasText(tname);
        Assert.notNull(asset);
        final IncludeTemplate i = new IncludeTemplate(ics, asset, tname);
        map.put(name, i);
        return i;

    }

    public Include find(final String name) {
        return map.get(name);
    }

}
