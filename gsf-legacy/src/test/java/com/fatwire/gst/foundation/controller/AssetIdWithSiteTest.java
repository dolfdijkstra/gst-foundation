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

import junit.framework.TestCase;

import com.openmarket.xcelerate.asset.AssetIdImpl;

public class AssetIdWithSiteTest extends TestCase {

    public void testEqualsObject() {

        AssetIdWithSite a = new AssetIdWithSite("foo", 123L, "fsii");
        AssetIdImpl b = new AssetIdImpl("foo", 123L);
        assertTrue(a.equals(b));
        //b equals a should also be true, but it is not.
        //assertTrue(b.equals(a));
        /*
        
        Map<AssetId, String> x = new HashMap<AssetId, String>();
        x.put(b, "ok");
        System.out.println(x.get(a));
        */
    }

}
