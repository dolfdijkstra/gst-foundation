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
package com.fatwire.gst.foundation.facade.assetapi;

import com.fatwire.assetapi.data.AssetId;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * test case for assetIdUtils
 *
 * @author Tony Field
 * @since Mar 9, 2011
 */
public final class AssetIdUtilsTest extends TestCase {
    public void testFromString() {
        AssetId id = AssetIdUtils.fromString("article:123");
        Assert.assertEquals("123", Long.toString(id.getId()));
        Assert.assertEquals("article", id.getType());

        try {
            AssetIdUtils.fromString(null);
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for null input: " + t);
        }

        try {
            AssetIdUtils.fromString("foo");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for foo input: " + t);
        }

        try {
            AssetIdUtils.fromString("foo:");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for foo: input: " + t);
        }

        try {
            AssetIdUtils.fromString("foo:bar");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for foo:bar input: " + t);
        }

        try {
            AssetIdUtils.fromString("foo:");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for foo: input: " + t);
        }

        try {
            AssetIdUtils.fromString(":123");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for :123 input: " + t);
        }

        try {
            AssetIdUtils.fromString(":");
            Assert.fail("Successfully parsed garbage");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        } catch (Throwable t) {
            Assert.fail("bad data did not return the right exception for : input: " + t);
        }

    }

}
