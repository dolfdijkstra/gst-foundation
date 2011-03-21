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
