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
package com.fatwire.gst.foundation.tagging;

/**
 * Utilities for converting a tag into a cache dependency string and back
 * 
 * @author Tony Field
 * @since Jul 28, 2010
 */
public final class TagUtils {

    private static final String IITEM_PREFIX = "gsf-tag-";

    /**
     * Return the cache dependency string format corresponding to the specified
     * tag
     * 
     * @param tag the tag
     * @return string
     */
    public static String convertTagToCacheDepString(Tag tag) {
        return IITEM_PREFIX + tag.getTag();
    }

    /**
     * Convert the cache dep string to a Tag. If the string is not valid, null
     * is returned
     * 
     * @param cacheDepString input string, note not the same as the tag itself.
     * @return Tag or null
     * @see #convertTagToCacheDepString
     */
    public static Tag convertCacheDepStringToTag(String cacheDepString) {
        if (cacheDepString != null && cacheDepString.length() > IITEM_PREFIX.length()
                && cacheDepString.startsWith(IITEM_PREFIX)) {
            return asTag(cacheDepString.substring(IITEM_PREFIX.length() + 1));
        }
        return null;
    }

    public static Tag asTag(final String tagValue) {
        return new Tag() {
            public String getTag() {
                return tagValue;
            }

            public String toString() {
                return "tag:" + getTag();
            }

            @Override
            public int hashCode() {
                return tagValue.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof Tag) {
                    Tag t = (Tag) o;
                    return t.getTag().equals(tagValue);
                }
                return false;
            }

        };
    }
}
