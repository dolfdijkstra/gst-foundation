/*
 * Copyright (c) 2010 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
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
     * Return the cache dependency string format corresponding to the specified tag
     *
     * @param tag the tag
     * @return string
     */
    public static String convertTagToCacheDepString(Tag tag) {
        return IITEM_PREFIX + tag.getTag();
    }

    /**
     * Convert the cache dep string to a Tag.  If the string is not valid, null is returned
     *
     * @param cacheDepString input string, note not the same as the tag itself.
     * @return Tag or null
     * @see #convertTagToCacheDepString
     */
    public static Tag convertCacheDepStringToTag(String cacheDepString) {
        if (cacheDepString != null && cacheDepString.length() > IITEM_PREFIX.length() && cacheDepString.startsWith(IITEM_PREFIX)) {
            return asTag(cacheDepString.substring(IITEM_PREFIX.length() + 1));
        }
        return null;
    }

    public static Tag asTag(final String tagValue) {
        return new Tag() {
            public String getTag() {
                return tagValue;
            }
        };
    }
}
