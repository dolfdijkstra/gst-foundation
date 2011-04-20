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

package com.fatwire.gst.foundation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * various helper classes for debugging
 * 
 * @author Dolf Dijkstra
 * 
 */
public class DebugHelper {

    public static String TIME_LOGGER = DebugHelper.class.getName() + ".time";
    private static final Log LOG = LogFactory.getLog(DebugHelper.class);
    private static final Log LOG_TIME = LogFactory.getLog(TIME_LOGGER);

    private DebugHelper() {
    }

    public static void dumpVars(final ICS ics) {
        dumpVars(ics, LOG);
    }

    @SuppressWarnings("unchecked")
    public static void dumpVars(final ICS ics, final Log log) {
        if (log.isDebugEnabled()) {
            for (final Enumeration<String> e = ics.GetVars(); e.hasMoreElements();) {
                final String n = e.nextElement();
                log.debug("ICS variable: " + n + "=" + ics.GetVar(n));
            }
        }
    }

    /**
     * Retrieves the root exception of a <code>Throwable</code>.
     * 
     * 
     * @param e the exception with nested exceptions (causes)
     * @return the root cause
     */
    public static Throwable findRootCause(final Throwable e) {
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
     * @return A human readable string for the elapsed micro seconds
     */
    public static String microToHuman(final long elapsed) {
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
     * 
     * Converts an elapsed time in nano seconds to a human readable string with
     * microseconds, seconds and milliseconds precision on larger elapsed times.
     * Precision is dropped to microseconds precision.
     * 
     * @param elapsed the elapsed time in nano seconds (us)
     * @return A human readable string for the elapsed micro seconds
     */
    public static String milliToHuman(final long elapsed) {
        String human = "(" + elapsed + "ms) ";
        if (elapsed > 1000 * 60L) {
            // final long e = elapsed / 60000;
            final long mins = elapsed / 60000L;
            final long secs = (elapsed - mins * 60000) / 1000L;
            human += Long.toString(mins) + "m " + Long.toString(secs) + "s";
        } else if (elapsed > 1000L) {
            human += Long.toString(elapsed / 1000) + "." + Long.toString(elapsed % 1000) + "ms";
        } else {
            human += Long.toString(elapsed) + "ms";
        }
        return human;

    }

    /**
     * 
     * Converts an elapsed time in milli seconds to a human readable string with
     * minutes and seconds precision on larger elapsed times.
     * 
     * @param elapsed the elapsed time in nano seconds (us)
     * @return A human readable string for the elapsed micro seconds
     */
    public static String nanoToHuman(final long elapsed) {
        return microToHuman(elapsed / 1000L);

    }

    /**
     * Print the elapsed time between the <tt>start</tt> and <tt>end</tt> to the
     * provided logger in a human readable form
     * 
     * @param log The logger where the message will be printed to.
     * @param msg The message as an indicator of the operation that was
     *            monitored.
     * @param start time in nanoseconds {@link System#nanoTime()}
     * @param end time in nanoseconds {@link System#nanoTime()}
     * @see DebugHelper#microToHuman(long)
     */

    public static void printTime(final Log log, final String msg, final long start, final long end) {
        if (start > 0) {
            final long elapsed = (end - start) / 1000;
            log.debug(msg + " took " + microToHuman(elapsed));
        }

    }

    /**
     * 
     * Print the elapsed time since the <tt>start</tt> to the provided Log
     * 
     * @param log The logger where the message will be printed to.
     * @param msg The message as an indicator of the operation that was
     *            monitored.
     * @param start time in nanoseconds {@link System#nanoTime()}
     * @see DebugHelper#microToHuman(long)
     */

    public static void printTime(final Log log, final String msg, final long start) {
        if (start > 0) {
            printTime(log, msg, start, System.nanoTime());
        }
    }

    /**
     * Print the elapsed time since the <tt>start</tt> to the default time
     * logger {@link DebugHelper#TIME_LOGGER}
     * 
     * 
     * @param msg The message as an indicator of the operation that was
     *            monitored.
     * @param start time in nanoseconds {@link System#nanoTime()}
     * @see DebugHelper#microToHuman(long)
     * 
     */
    public static void printTime(final String msg, final long start) {
        if (start > 0) {
            printTime(LOG_TIME, msg, start);
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
    public static String printAsset(final AssetData ad) throws AssetAccessException {
        final StringWriter sw = new StringWriter();
        final PrintWriter out = new PrintWriter(sw);
        out.println(ad.getAssetId() + " '" + ad.getAssetTypeDef().getName() + "' '" + ad.getAssetTypeDef().getSubtype()
                + "'");

        out.println("defs --- name (type [meta/value count/inherited/derived]");
        for (final AttributeDef def : ad.getAssetTypeDef().getAttributeDefs()) {
            final AttributeDefProperties props = def.getProperties();

            out.println("\t" + def.getName() + " (" + def.getType() + " [" + def.isMetaDataAttribute() + "/"
                    + props.getValueCount().name() + "/" + props.isInheritedFlexAttribute() + "/"
                    + props.isDerivedFlexAttribute() + "])");
        }
        List<AttributeDef> parentDefs = ad.getAssetTypeDef().getParentDefs();
        if (parentDefs != null) {
            out.println("parent defs --- name (type [meta/value count/inherited/derived]");
            for (final AttributeDef def : parentDefs) {
                final AttributeDefProperties props = def.getProperties();

                out.println("\t" + def.getName() + " (" + def.getType() + " [" + def.isMetaDataAttribute() + "/"
                        + props.getValueCount().name() + "/" + props.isInheritedFlexAttribute() + "/"
                        + props.isDerivedFlexAttribute() + "])");
            }
        }

        out.println("attribute names --- ");
        out.println("\t" + ad.getAttributeNames());
        out.println("attributes --- name (type [meta/value count/inherited/derived]");
        for (final AttributeData attr : ad.getAttributeData()) {
            final AttributeDefProperties props = attr.getAttributeDef().getProperties();
            // props.getDataMap()
            out.print("\t" + attr.getAttributeName() + " (" + attr.getType() + " ["
                    + attr.getAttributeDef().isMetaDataAttribute() + "/" + props.getValueCount().name() + "/"
                    + props.isInheritedFlexAttribute() + "/" + props.isDerivedFlexAttribute() + "]): ");
            if (attr.getType() == AttributeTypeEnum.URL || attr.getType() == AttributeTypeEnum.BLOB) {
                final BlobObject blob = (BlobObject) attr.getData();
                if (blob != null) {
                    final BlobAddress addr = blob.getBlobAddress();
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
        for (final AssetId parent : ad.getParents()) {
            out.println("\t" + parent);
        }

        out.println("associations --- ");
        for (final AssetAssociationDef adef : ad.getAssetTypeDef().getAssociations()) {
            for (final AssetId association : ad.getAssociatedAssets(adef.getName())) {
                out.println("\t" + adef.getName() + ":" + association);
            }
        }

        out.println("dimension --- group/name/id");
        try {
            AttributeData locale = ad.getAttributeData("Dimension");
            if (locale != null) {
                for (final Object o1 : locale.getDataAsList()) {
                    if (o1 instanceof Collection) { // o1 is probably a Set
                        for (Object o2 : (Collection<?>) o1) {
                            if (o2 instanceof Dimension) {
                                final Dimension dim2 = (Dimension) o2;
                                out.print("\t" + dim2.getGroup());
                                out.print("/" + dim2.getName());
                                out.println("/" + dim2.getId());
                            } else {
                                out.println("\t" + String.valueOf(o2));
                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            out.println("\tgetting the dimension attribute threw a " + e.getMessage());

        }
        final AttributeData mapping = ad.getAttributeData("Mapping");

        if (mapping != null) {
            int i = 0;
            final List<AttributeData> mappingArray = mapping.getDataAsList();
            for (final AttributeData s : mappingArray) {
                @SuppressWarnings("rawtypes")
                final List<Map<String, AttributeData>> structList = (List) s.getData();
                for (final Map<String, AttributeData> m : structList) {
                    final String key = (String) m.get("key").getData();
                    final String type = (String) m.get("type").getData();
                    final String value = (String) m.get("value").getData();
                    final String siteid = (String) m.get("siteid").getData();

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

    /**
     * Returns the assetid in as a human readable string in the format of
     * type:id.
     * 
     * @param assetId
     * @return
     */
    public static String toString(final AssetId assetId) {
        return assetId.getType() + ":" + assetId.getId();
    }

    public static String toString(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        pw.println(t.toString());
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();

    }
}
