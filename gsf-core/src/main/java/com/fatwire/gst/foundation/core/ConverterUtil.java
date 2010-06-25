/*
 * Copyright 2009 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.core;

import java.util.Date;
import java.util.List;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.assetapi.def.AttributeDefProperties;

/**
 * Utility for convert AttributeData into primary type objects if possible.
 * 
 * @author Dolf.Dijkstra
 * @since Nov 27, 2009
 * 
 */
public class ConverterUtil {
    private ConverterUtil() {// private
    }

    static public boolean isSingleValued(AttributeData attr) {
        AttributeDef ad = attr.getAttributeDef();
        return isSingleValued(ad);

    }

    static public boolean isSingleValued(AttributeDef ad) {
        return ad.getProperties() == null ? true : ad.getProperties().getValueCount().equals(
                AttributeDefProperties.ValueCount.SINGLE);

    }

    static public List<?> asList(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;

        if (isSingleValued(attr)) {
            throw new RuntimeException("not a multi valued attribute");
        }
        return attr.getDataAsList();
    }

    /**
     * <pre>
     * INT Integer 
     * FLOAT Double 
     * STRING String 
     * DATE Date 
     * MONEY Double 
     * LONG Long
     * LARGE_TEXT String 
     * ASSET AssetId 
     * BLOB BlobObjec
     * </pre>
     * 
     * @param name
     * @return attribute as an integer
     */

    static public Integer asInt(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case INT:
                return (Integer) o;
            case DATE:
            case BLOB:
            case FLOAT:
            case LONG:
            case ASSET:
            case MONEY:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Long");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public Date asDate(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case DATE:
                return (Date) o;
            case BLOB:
            case FLOAT:
            case INT:
            case LONG:
            case ASSET:
            case MONEY:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Date");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public BlobObject asBlob(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case URL:
            case BLOB:
                return (BlobObject) o;
            case FLOAT:
            case INT:
            case LONG:
            case ASSET:
            case MONEY:
            case DATE:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Blob");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public Float asFloat(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case FLOAT:
            case INT:
                return (Float) o;
            case LONG:
            case ASSET:
            case MONEY:
            case DATE:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case BLOB:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Float");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public Double asDouble(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case FLOAT:
            case INT:
            case LONG:
            case MONEY:
                return (Double) o;
            case ASSET:
            case DATE:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case BLOB:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Double");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public Long asLong(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case INT:
            case LONG:
                return (Long) o;
            case ASSET:
            case MONEY:
            case FLOAT:
            case DATE:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case BLOB:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a Long");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }

    }

    static public AssetId asAssetId(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {
            case ASSET:
                return (AssetId) o;

            case INT:
            case FLOAT:
            case LONG:
            case MONEY:
            case DATE:
            case STRING:
            case LARGE_TEXT:
            case ASSETREFERENCE:
            case BLOB:
            case URL:
            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a AssetId ");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());
        }
    }

    static public String asString(AttributeData attr) {

        Object o = attr == null ? null : attr.getData();
        if (o == null)
            return null;
        switch (attr.getType()) {

            case INT:
            case FLOAT:
            case LONG:
            case MONEY:
                return String.valueOf(o);
            case DATE:
                return com.fatwire.cs.core.db.Util.formatJdbcDate((Date) o);
            case STRING:
            case LARGE_TEXT:
                return (String) o;

            case ASSET:
            case ASSETREFERENCE:

            case BLOB:
            case URL:

            case ARRAY:
            case STRUCT:
            case LIST:
            case ONEOF:
                throw new IllegalArgumentException("Can't cast " + attr.getType() + " into a String");
            default:
                throw new IllegalArgumentException("Don't know about " + attr.getType());

        }

    }

}
