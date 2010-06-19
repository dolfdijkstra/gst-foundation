/*
 * Copyright (c) 2008 FatWire Corporation. All Rights Reserved.
 * Title, ownership rights, and intellectual property rights in and
 * to this software remain with FatWire Corporation. This  software
 * is protected by international copyright laws and treaties, and
 * may be protected by other law.  Violation of copyright laws may
 * result in civil liability and criminal penalties.
 */
package com.fatwire.gst.foundation.facade.runtag.assetset;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wrapper around the ASSETSET.GETATTRIBUTEVALUES xml tag
 * <pre>
 * &lt;ASSETSET.GETATTRIBUTEVALUES NAME="assetsetname" ATTRIBUTE="attribname"
 * [TYPENAME="assettypename"] LISTVARNAME="varname"
 * [ORDERING="ascending|descending"]/&gt;
 * </pre>
 * @author Mike Field
 * @author Dolf Dijkstra
 * @since July 15, 2008
 */
public final class GetAttributeValues extends AbstractTagRunner {
    // Default Constructor
    public GetAttributeValues() {
        super("ASSETSET.GETATTRIBUTEVALUES");
    }

    /**
     * Sets name to the value of <code>s</code>
     * 
     * @param s The name of the assetset to return
     */
    public void setName(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid name string: " + s);
        }
        this.set("NAME", s);
    }

    /**
     * Sets attribute to the value of <code>s</code>
     * 
     * @param s The name of the attribute
     */
    public void setAttribute(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid attribute string: " + s);
        }
        this.set("ATTRIBUTE", s);
    }

    /**
     * Sets listvarname to the value of <code>s</code>
     * 
     * @param s The name of the listvarname
     */
    public void setListvarname(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid listvarname string: " + s);
        }
        this.set("LISTVARNAME", s);
    }

    /**
     * Sets typename to the value of <code>s</code>
     * 
     * @param s The attribute's typename
     */
    public void setTypename(String s) {
        // validate first
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid typename string: " + s);
        }
        this.set("TYPENAME", s);
    }

    /**
     * Sets immediateonly to the value of <code>s</code>
     * 
     * @param s The value of immediateonly: true or false
     */
    public void setImmediateonly(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("true") && !s.equals("false")) {
            throw new IllegalArgumentException("Invalid immediateonly string: " + s);
        }
        this.set("IMMEDIATEONLY", s);
    }

    /**
     * Sets ordering to the value of <code>s</code>
     * 
     * @param s The value of the ordering field: ascending or descending
     */
    public void setOrdering(String s) {
        // validate first
        if (s == null || s.length() == 0 || !s.equals("ascending") && !s.equals("descending")) {
            throw new IllegalArgumentException("Invalid ordering string: " + s);
        }
        this.set("ORDERING", s);
    }

    /**
     * 
     * GetAttributeValues method has a constructor name and should not be used
     * 
     * @param ics
     * @param id
     * @param deptype
     * @param locale
     * @param attr
     * @param attrType
     * @param ordering
     * @return
     * @deprecated
     * @see GetAttributeValues#getAttributeValues(ICS, AssetId, String, String,
     *      String, String, String)
     */
    public static IList GetAttributeValues(final ICS ics, final AssetId id, final String deptype, final String locale,
            final String attr, final String attrType, final String ordering) {
        return getAttributeValues(ics, id, deptype, locale, attr, attrType, ordering);
    }

    /**
     * 
     * @param ics
     * @param id
     * @param deptype
     * @param locale
     * @param attr
     * @param attrType
     * @param ordering
     * @return
     */
    public static IList getAttributeValues(ICS ics, AssetId id, String deptype, String locale, String attr,
            String attrType, String ordering) {

        // create asset set
        SetAsset setAsset = new SetAsset();
        final String assetSetName = "__AssetSet" + ics.genID(false);
        setAsset.setName(assetSetName);
        setAsset.setType(id.getType());
        setAsset.setId(Long.toString(id.getId()));
        if (deptype != null) {
            setAsset.setDeptype(deptype);
        }
        if (locale != null) {
            setAsset.setLocale(locale);
        }
        setAsset.execute(ics);

        GetAttributeValues gav = new GetAttributeValues();
        gav.setName(assetSetName);
        gav.setAttribute(attr);
        if (attrType != null) {
            gav.setTypename(attrType);
        }
        if (ordering != null) {
            gav.setOrdering(ordering);
        }
        String listname = ics.genID(true);
        gav.setAttribute(listname);
        gav.execute(ics);

        IList result = ics.GetList(listname);
        ics.RegisterList(listname, null);
        return result;
    }

}
