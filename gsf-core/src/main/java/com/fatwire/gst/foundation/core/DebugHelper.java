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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.assetapi.data.AttributeData;
import com.fatwire.assetapi.data.BlobObject;
import com.fatwire.assetapi.data.BlobObject.BlobAddress;
import com.fatwire.assetapi.def.AssetAssociationDef;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.assetapi.def.AttributeDefProperties;
import com.fatwire.assetapi.def.AttributeTypeEnum;
import com.fatwire.mda.Dimension;


/**
 * various helper classes for debugging
 * 
 * @author Dolf Dijkstra
 *
 */
public class DebugHelper {

    public static String TIME_LOGGER = DebugHelper.class.getName() + ".time";
    private static final Log log = LogFactory.getLog(DebugHelper.class);
    private static final Log log_time = LogFactory.getLog(TIME_LOGGER);

    private DebugHelper() {
    }

    @SuppressWarnings("unchecked")
    public static void dumpVars(ICS ics) {
        if (log.isDebugEnabled()) {
            for (final Enumeration<String> e = ics.GetVars(); e.hasMoreElements();) {
                final String n = e.nextElement();
                log.debug("ICS variable: " + n + "=" + ics.GetVar(n));
            }
        }
    }

    /**
     * 
     * 
     * 
     * @param e the exception with nested exception (causes)
     * @return the root cause
     */
    public static Throwable findRootCause(Throwable e) {
        Throwable p = e;
        while (p.getCause() != null) {
            p = p.getCause();
            // alternative would be to set the root on e => e.initCause(p);
        }

        return p;

    }

    /**
     * 
     * Converts an elapsed time in micro seconds to a human readable string with
     * seconds and milliseconds precision on larger elapsed times
     * 
     * @param elapsed the elapsed time in micro seconds (us)
     * @return
     */
    public static String microToHuman(long elapsed) {
        String human = "(" + elapsed + "us) ";
        if (elapsed > 1000000) {
            final long e = elapsed / 1000;
            human += Long.toString(e / 1000) + "." + Long.toString(e % 1000) + "s";
        } else if (elapsed > 1000) {
            human += Long.toString(elapsed / 1000) + "." + Long.toString(elapsed % 1000) + "ms";
        } else {
            human += Long.toString(elapsed) + "us";
        }
        return human;

    }

    /**
     * Print the elapsed time between the <tt>start</tt> and <tt>end</tt> to the
     * provided logger in a human readable form
     * 
     * @param log
     * @param msg
     * @param start time in nanoseconds {@link System.nanoSeconds}
     * @param end time in nanoseconds {@link System.nanoSeconds}
     * @see DebugHelper#microToHuman(long)
     */

    public static void printTime(final Log log, String msg, long start, long end) {
        if (start > 0) {
            final long elapsed = (end - start) / 1000;
            log.debug(msg + " took " + microToHuman(elapsed));
        }

    }

    /**
     * 
     * Print the elapsed time since the <tt>start</tt> to the provided Log
     * 
     * @param log
     * @param msg
     * @param start time in nanoseconds {@link System.nanoSeconds}
     * @see ebugHelper#microToHuman(long)
     */

    public static void printTime(final Log log, String msg, long start) {
        if (start > 0) {
            printTime(log, msg, start, System.nanoTime());
        }
    }

    /**
     * Print the elapsed time since the <tt>start</tt> to the default time
     * logger {@link TIME_LOGGER}
     * 
     * 
     * @param msg
     * @param start time in nanoseconds {@link System.nanoSeconds}
     * @see ebugHelper#microToHuman(long)
     * 
     */
    public static void printTime(String msg, long start) {
        if (start > 0) {
            printTime(log_time, msg, start);
        }
    }

    /**
     * Print the AssetData to a string for debugging purposes
     * 
     * @param ad the asset date
     * @return String with asset attributes etc
     * @throws AssetAccessException
     */
    @SuppressWarnings("unchecked")
    public static String printAsset(AssetData ad) throws AssetAccessException {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println(ad.getAssetId() + " " + ad.getAssetTypeDef().getName() + " " + ad.getAssetTypeDef().getSubtype());

        out.println("defs --- ");
        for (AttributeDef def : ad.getAssetTypeDef().getAttributeDefs()) {
            AttributeDefProperties props = def.getProperties();
            // def.getDescription();
            // def.getName();
            // def.isDataMandatory();
            // def.getType();
            // def.isMetaDataAttribute();
            // props.getMultiple()
            // props.isDerivedFlexAttribute()
            // BeanInfo
            // info=java.beans.Introspector.getBeanInfo(def.getClass());
            // for (PropertyDescriptor desc:info.getPropertyDescriptors()){
            // desc.getName()
            // desc.getReadMethod()
            // desc.
            // }

            out.println("\t" + def.getName() + " (" + def.getType() + " [" + def.isMetaDataAttribute() + "/"
                    + props.getValueCount().name() + "/" + props.isInheritedFlexAttribute() + "])");
            switch (def.getType()) {
                case ARRAY:
                case ASSET:
                case INT:
                case FLOAT:
                case STRING:
                case LONG:
                case DATE:
                case MONEY:
                case LARGE_TEXT:
                case ASSETREFERENCE:
                case BLOB:
                case URL:
                case STRUCT:
                case LIST:
                case ONEOF:
                    break;
            }
        }

        out.println("attribute names --- ");
        out.println("\t" + ad.getAttributeNames());
        out.println("attributes --- ");
        for (AttributeData attr : ad.getAttributeData()) {
            AttributeDefProperties props = attr.getAttributeDef().getProperties();
            out.print("\t" + attr.getAttributeName() + " (" + attr.getType() + " ["
                    + attr.getAttributeDef().isMetaDataAttribute() + "/" + props.getValueCount().name() + "]): ");
            if (attr.getType() == AttributeTypeEnum.URL || attr.getType() == AttributeTypeEnum.BLOB) {
                BlobObject blob = (BlobObject) attr.getData();
                if (blob != null) {
                    BlobAddress addr = blob.getBlobAddress();
                    out.print(addr.getIdentifier());
                    out.print(" " + addr.getIdentifierColumnName());
                    out.print(" " + addr.getColumnName());
                    out.println(" " + addr.getTableName());
                } else {
                    out.println("NULL BLOB");
                }
            } else {
                out.println(attr.getData());
            }

        }
        out.println("parents --- ");
        for (AssetId parent : ad.getParents()) {
            out.println("\t" + parent);
        }
        out.println("associations --- ");
        for (AssetAssociationDef adef : ad.getAssetTypeDef().getAssociations()) {
            for (AssetId association : ad.getAssociatedAssets(adef.getName())) {
                out.println("\t" + adef.getName() + ":" + association);
            }
        }
        out.println("dimension --- ");
        AttributeData locale = ad.getAttributeData("Dimension");
        for (Object o2 : locale.getDataAsList()) {
            if (o2 instanceof Dimension) {
                Dimension dim2 = (Dimension) o2;
                out.println("\t" + dim2.getGroup());
                out.println("\t" + dim2.getName());
                out.println("\t" + dim2.getId());
            }

        }
        AttributeData mapping = ad.getAttributeData("Mapping");

        if (mapping != null) {
            // AttributeData mappingData = (AttributeData) mapping.getData();
            int i = 0;
            List<AttributeData> mappingArray = mapping.getDataAsList(); // we
            // can
            // use
            // getDataAsList();
            for (AttributeData s : mappingArray) {
                List<Map<String, AttributeData>> structList = (List) s.getData(); // we
                // can
                // use
                // getDataAsList();
                for (Map<String, AttributeData> m : structList) {
                    String key = (String) m.get("key").getData();
                    String type = (String) m.get("type").getData();
                    String value = (String) m.get("value").getData();
                    String siteid = (String) m.get("siteid").getData();

                    out.println("Mapping Entry #" + String.valueOf(i + 1));
                    out.println("Key: " + key);
                    out.println("Type: " + type);
                    out.println("Value: " + value);
                    out.println("Siteid: " + siteid);
                }
                i++;
            }
        }

        out.flush();
        return sw.toString();
    }

}
