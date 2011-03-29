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

package com.fatwire.gst.foundation.facade.assetapi.code;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.assetapi.common.AssetAccessException;
import com.fatwire.assetapi.def.AssetAssociationDef;
import com.fatwire.assetapi.def.AssetTypeDef;
import com.fatwire.assetapi.def.AssetTypeDefManager;
import com.fatwire.assetapi.def.AssetTypeDefProperties;
import com.fatwire.assetapi.def.AttributeDef;
import com.fatwire.assetapi.def.AttributeDefProperties;
import com.fatwire.assetapi.def.AttributeTypeEnum;
import com.fatwire.system.Session;
import com.fatwire.system.SessionFactory;

import org.apache.commons.lang.StringEscapeUtils;

public class CodeGen {

    static Set<String> KEYWORDS = new HashSet<String>(Arrays
            .asList(("abstract,assert,boolean,break,byte,case,catch,char,class,const,continue"
                    + ",default,do,double,else,enum,extends,final,finally,float"
                    + ",for,goto,if,implements,import,instanceof,int,interface,long,native,new,"
                    + "ackage,private,protected,public,return,short,static,strictfp,super,switch,"
                    + "synchronized,this,throw,throws,transient,try,void,volatile,while").split(",")));
    static Set<String> USELESS_ATTRIBUTES = new HashSet<String>(Arrays
            .asList(("urlexternaldoc,externaldoctype,urlexternaldocxml").split(",")));

    interface Generator {

        void printAssetTypeDef(AssetTypeDef def);

    }

    static class DefPrint implements Generator {
        PrintWriter out;

        public DefPrint(Writer out2) {
            out = new PrintWriter(out2);
        }

        public void printAssetTypeDef(AssetTypeDef def) {
            if (def == null)
                return;
            if (def.getSubtype() == null && def.getProperties().getIsFlexAsset()) {
                return;
            }
            String s = def.getSubtype() == null ? "" : ("/" + def.getSubtype());

            out.print(def.getName() + s);
            out.print("   // description: '" + def.getDescription() + "'");
            out.print(" plural: '" + def.getPlural() + "'");
            out.print(" canbechild: " + def.getCanBeChild() + "");
            AssetTypeDefProperties p = def.getProperties();

            out.print(" canAddSubtypes: " + p.getCanAddSubtypes());
            String type = p.getIsAssetmakerAsset() ? "AssetmakerAsset" : "Unknown Type";
            if (p.getIsCoreAsset())
                type = "CoreAsset";
            if (p.getIsFlexAsset())
                type = "FlexAsset";
            out.print(" type: '" + type + "'");
            out.println(" nameMustUnique: " + p.getIsNameMustUnique());

            printAttributes(def.getAttributeDefs());
            printParentDefs(def.getParentDefs());
            printAssocations(def.getAssociations());

            out.println();
            out.println();
        }

        private void printParentDefs(List<AttributeDef> list) {
            printAttributeList(list, "parents");

        }

        private void printAssocations(List<AssetAssociationDef> assocs) {
            if (assocs != null && !assocs.isEmpty()) {
                out.println("associations: ");
                for (AssetAssociationDef assoc : assocs) {

                    out.println("assoc name: '" + assoc.getName() + "'");
                    out.println("description: '" + assoc.getDescription() + "'");
                    out.println("multiple: '" + assoc.isMultiple() + "'");
                    out.println("legalChildAssetTypes: '" + assoc.getLegalChildAssetTypes() + "'");
                    out.println("childAssetType: '" + assoc.getChildAssetType() + "'");
                    out.println("subTypes: '" + assoc.getSubTypes() + "'");
                    out.println();
                }
            }

        }

        private void printAttributes(List<AttributeDef> list) {
            printAttributeList(list, "attributes");
        }

        private void printAttributeList(List<AttributeDef> attributes, String label) {

            if (attributes != null && !attributes.isEmpty()) {
                out.println(label + ": ");

                for (AttributeDef a : attributes) {

                    out.print("name: '" + a.getName() + "'");
                    out.print(" description: '" + a.getDescription() + "'");
                    out.print(" type: '" + a.getType() + "'");

                    AttributeDefProperties p = a.getProperties();
                    out.print(" size: '" + p.getSize() + "'");
                    out.print(" count: '" + p.getValueCount() + "'");
                    if (a.getType() == AttributeTypeEnum.ASSET) {
                        out.print(" assettype: '" + p.getAssetType() + "'");
                        out.print(" assettype: '" + p.getAssetType() + "'");
                    }
                    if (a.isDataMandatory())
                        out.print(" mandatory");
                    if (a.isMetaDataAttribute())
                        out.print(" meta");

                    if (p.isAllowEmbeddedLinks())
                        out.print(" allow embedded");
                    if (p.isDerivedFlexAttribute())
                        out.print(" derived");
                    if (p.isInheritedFlexAttribute())
                        out.print(" inherited");

                    if (p.getMultiple() != null)
                        out.print(" multiple: '" + p.getMultiple() + "'");
                    if (p.getOrdinal() != null)
                        out.print(" ordinal: '" + p.getOrdinal() + "'");
                    if (p.getRequired() != null)
                        out.print(" required: '" + p.getRequired() + "'");
                    out.println();
                }
            }
        }

    }

    static class AssetApiGen implements Generator {
        protected PrintWriter out;

        public AssetApiGen(Writer writer) {
            out = new PrintWriter(writer);
        }

        public void printAssetTypeDef(AssetTypeDef def) {
            if (def == null)
                return;
            if (def.getSubtype() == null && def.getProperties().getIsFlexAsset()) {
                return;
            }
            String s = def.getSubtype() == null ? "" : def.getSubtype() + "/";

            out.print(def.getName() + "/" + s + "Detail");
            out.print("   // description: '" + def.getDescription() + "'");
            out.print(" plural: '" + def.getPlural() + "'");
            out.print(" canbechild: " + def.getCanBeChild() + "");
            AssetTypeDefProperties p = def.getProperties();
            out.print(" canAddSubtypes: " + p.getCanAddSubtypes());
            String type = p.getIsAssetmakerAsset() ? "AssetmakerAsset" : "Unknown Type";
            if (p.getIsCoreAsset())
                type = "CoreAsset";
            if (p.getIsFlexAsset())
                type = "FlexAsset";
            out.print(" type: '" + type + "'");
            out.println(" nameMustUnique: " + p.getIsNameMustUnique());

            printAttributes(def);
            printParentDefs(def.getParentDefs());
            printAssocations(def.getAssociations());
            out.println();
            out.println();
        }

        private void printAssocations(List<AssetAssociationDef> associations) {
            // TODO Auto-generated method stub

        }

        private void printParentDefs(List<AttributeDef> parentDefs) {
            // TODO Auto-generated method stub

        }

        protected String toVarName(String name) {
            if (name == null || name.length() == 0)
                return "noname";
            if (KEYWORDS.contains(name))
                return name + "_";
            char[] t = name.replace("-", "_").toCharArray();
            t[0] = Character.toLowerCase(t[0]);
            return new String(t);
        }

        protected void printAttributes(AssetTypeDef def) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.println("//sample copy/paste code");
            pw.println("public void read" + def.getName() + (def.getSubtype() != null ? "_" + def.getSubtype() : "")
                    + "(ICS ics) throws AssetNotExistException, AssetAccessException {");
            pw.println();
            pw.println("    Session ses = SessionFactory.getSession(ics);");
            pw
                    .println("    AssetDataManager mgr = (AssetDataManager) ses.getManager(AssetDataManager.class.getName());");
            pw.println("    AssetId id = new AssetIdImpl(ics.GetVar(\"c\"), Long.parseLong(ics.GetVar(\"cid\")));");
            pw.println("    Iterable<AssetData> assets = mgr.read(Collections.singletonList(id));");

            pw.println("    for (AssetData asset : assets) {");
            pw.println("");

            List<AttributeDef> attributes = def.getAttributeDefs();
            if (attributes != null) {
                pw.println("        // attributes: ");
                pw.println("        AttributeData attribute = null;");

                for (AttributeDef a : attributes) {
                    if (USELESS_ATTRIBUTES.contains(a.getName()))
                        continue;
                    AttributeTypeEnum t = a.getType();
                    String cast = toCastType(t);
                    String name = toVarName(a.getName());
                    pw.println("       attribute = asset.getAttributeData(\"" + a.getName() + "\", "
                            + a.isMetaDataAttribute() + ");");
                    pw.println("       if (attribute != null){");
                    pw.println("            // type = " + t + ", valueCount = " + a.getProperties().getValueCount());
                    boolean singleValued = AttributeDefProperties.ValueCount.SINGLE.equals(a.getProperties()
                            .getValueCount());
                    if (singleValued) {
                        pw.println("           " + cast + " " + name + " = (" + cast + ")attribute.getData();");
                    } else {
                        pw.println("           List<?> " + name + " = attribute.getDataAsList();");
                    }
                    if (a.getProperties().getMultiple() != null) {
                        pw.println("           multiple " + a.getProperties().getMultiple());

                    }
                    // List<?> valueList = attribute.getDataAsList();
                    // for (Object o : valueList)
                    // {

                    // }
                    switch (t) {
                        case BLOB:
                        case URL:
                            pw.println("           BlobAddress address = " + name + ".getBlobAddress();");
                            pw.println("           String blobcol = address.getColumnName();");
                            pw.println("           Object blobid = address.getIdentifier();");
                            pw.println("           String idcol = address.getIdentifierColumnName();");
                            pw.println("           String blobtable = address.getTableName();");
                            pw.println("           //InputStream stream = " + name + ".getBinaryStream();");
                            break;

                    }

                    pw.println("        }");
                }
                pw.println("    }");
                pw.println("}");
                out.print(org.apache.commons.lang.StringEscapeUtils.escapeHtml(sw.toString()));
            }
        }

        String toCastType(AttributeTypeEnum t) {

            switch (t) {
                case INT:
                    return "Integer";
                case FLOAT:
                    return "Double";
                case STRING:
                    return "String";
                case LONG:
                    return "Long";
                case DATE:
                    return "Date";
                case MONEY:
                    return "Double";
                case LARGE_TEXT:
                    return "String";
                case ASSET:
                    return "AssetId";
                case ASSETREFERENCE:
                    return "AssetId";
                case BLOB:
                    return "BlobObject";
                case URL:
                    return "BlobObject";
                case ARRAY:
                    return "List<?>";
                case STRUCT:
                    return "Map<?,?>";
                case LIST:
                    return "List<?>";
                case ONEOF:
                    return "Object";
            }
            throw new IllegalArgumentException("Don't know about " + t);
        }
    }

    class HtmlWriter extends Writer {

        final Writer writer;

        /**
         * @param writer
         */
        private HtmlWriter(Writer writer) {
            super();
            this.writer = writer;
        }

        @Override
        public void close() throws IOException {
            writer.close();

        }

        @Override
        public void flush() throws IOException {
            writer.flush();

        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            StringEscapeUtils.escapeHtml(writer, new String(cbuf, off, len));

        }

    }

    static public class GsfJavaGen extends AssetApiGen {

        public GsfJavaGen(Writer writer) {
            super(writer);
        }

        protected void printAttributes(List<AttributeDef> attributes) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            if (attributes != null) {
                // pw.println("    // attributes: ");

                for (AttributeDef a : attributes) {
                    if (USELESS_ATTRIBUTES.contains(a.getName()))
                        continue;
                    AttributeTypeEnum t = a.getType();
                    String cast = toCastType(t);
                    String name = toVarName(a.getName());
                    String method = toMethodName(a.getType());
                    pw.println("");
                    // pw.println("    // type = " + t + ", valueCount = " +
                    // a.getProperties().getValueCount());
                    boolean singleValued = AttributeDefProperties.ValueCount.SINGLE.equals(a.getProperties()
                            .getValueCount());
                    if (singleValued) {
                        pw.println("    " + cast + " " + name + " = asset." + method + "(\"" + a.getName() + "\");");
                    } else {
                        pw.println("    List<?> " + name + " = asset.asList(\"" + a.getName() + "\");");
                    }

                    switch (t) {
                        case BLOB:
                        case URL:
                            pw.println("");
                            pw.println("    /*");
                            pw.println("    BlobAddress address = asset.asBlobAddress(\"" + a.getName() + "\");");
                            pw.println("    String blobcol = address.getColumnName();");
                            pw.println("    Object blobid = address.getIdentifier();");
                            pw.println("    String idcol = address.getIdentifierColumnName();");
                            pw.println("    String blobtable = address.getTableName();");
                            pw.println("");
                            pw
                                    .println("    String uri =new BlobUriBuilder(address).mimeType(\"image/jpeg\").toURI(ics);");

                            pw.println("    */");
                            pw.println("    //InputStream stream = " + name + ".getBinaryStream();");
                            break;

                    }

                }

                // org.apache.commons.lang.StringEscapeUtils.escapeHtml(out,
                // sw.toString());
            }
        }

        private String toMethodName(AttributeTypeEnum t) {

            switch (t) {
                case INT:
                    return "asInt";
                case FLOAT:
                    return "asDouble";
                case STRING:
                    return "asString";
                case LONG:
                    return "asLong";
                case DATE:
                    return "asDate";
                case MONEY:
                    return "asDouble";
                case LARGE_TEXT:
                    return "asString";
                case ASSET:
                    return "asAssetId";
                case ASSETREFERENCE:
                    return "asAssetId";
                case BLOB:
                    return "asBlob";
                case URL:
                    return "asBlob";
                case ARRAY:
                    return "asList";
                case STRUCT:
                    return "asMap";
                case LIST:
                    return "asList";
                case ONEOF:
                    return "asObject";
            }
            throw new IllegalArgumentException("Don't know about " + t);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.fatwire.gst.foundation.facade.assetapi.code.CodeGen.AssetApiGen
         * #printAssetTypeDef(com.fatwire.assetapi.def.AssetTypeDef)
         */
        @Override
        public void printAssetTypeDef(AssetTypeDef def) {

            out.println("//sample copy/paste code");
            out.println("public void read" + def.getName() + (def.getSubtype() != null ? "_" + def.getSubtype() : "")
                    + "(ICS ics) throws AssetNotExistException, AssetAccessException {");
            out.println();
            out.println("    AssetAccessTemplate aat = new AssetAccessTemplate(ics);");
            out
                    .println("    TemplateAsset asset = aat.readAsset(ics.GetVar(\"c\"), ics.GetVar(\"cid\"), new TemplateAssetMapper());");
            out.println("");
            printAttributes(def.getAttributeDefs());
            out.println("}");

        }

    }

    static public class GsfJspGen implements Generator {
        PrintWriter out;

        public GsfJspGen(Writer writer) {
            out = new PrintWriter(writer);
        }

        String getAttributeNames(List<AttributeDef> list) {
            StringBuilder b = new StringBuilder();
            for (AttributeDef def : list) {
                if (b.length() > 0)
                    b.append(",");
                b.append(def.getName());
            }
            return b.toString();

        }

        public void printAssetTypeDef(AssetTypeDef def) {
            out.println("<%@ taglib prefix=\"cs\" uri=\"futuretense_cs/ftcs1_0.tld\"");
            out.println("%><%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\"");
            out.println("%><%@ taglib uri=\"http://gst.fatwire.com/foundation/tags/gsf\" prefix=\"gsf\"");
            out.println("%><cs:ftcs><gsf:root>");

            out.println("<gsf:asset-load name=\"asset\" attributes=\"" + getAttributeNames(def.getAttributeDefs())
                    + "\" />");

            printAttributes(def.getAttributeDefs());
            out.println("%></gsf:root></cs:ftcs>");
        }

        private void printAttributes(List<AttributeDef> list) {
            for (AttributeDef def : list) {
                if (def.getType() == AttributeTypeEnum.BLOB || def.getType() == AttributeTypeEnum.URL) {
                    PageContext x;
                    //x.getVariableResolver().resolveVariable("asset." + def.getName());
                    //<gsf:link assetname="asset" field="" mimetype="" var="" >
                } else {
                    out.println(def.getName() + ": ${asset." + def.getName() + "} <br/>");
                }

            }

        }
    }

    void gen(ICS ics, JspWriter out) throws AssetAccessException, IOException {

        Session s = SessionFactory.getSession(ics);
        AssetTypeDefManager mgr = (AssetTypeDefManager) s.getManager(com.fatwire.assetapi.def.AssetTypeDefManager.class
                .getName());

        String ats = ics.GetVar("assettype");
        Generator generator = null;
        String what = ics.GetVar("what");
        if ("assetapicode".equals(what)) {
            generator = new AssetApiGen(out);
        } else if ("def".equals(what)) {
            generator = new DefPrint(out);
        } else if ("gsfjspcode".equals(what)) {
            generator = new GsfJspGen(out);
        } else if ("gsfjavacode".equals(what)) {
            generator = new GsfJavaGen(out);
        }

        if (ats != null && generator != null) {
            for (String at : ats.split(";")) {
                String[] p = at.split(":");
                String a = p[0];
                String st = p.length == 2 ? p[1] : null;
                try {
                    out.write("<pre>");
                    AssetTypeDef def = mgr.findByName(a, st);
                    if (def != null) {
                        generator.printAssetTypeDef(def);
                    }
                } catch (Exception e) {
                    out.println(e.getMessage() + " on " + a + "/" + st);
                    e.printStackTrace(new PrintWriter(out));
                }
                out.write("</pre>");

            }

        }

        for (String assetType : mgr.getAssetTypes()) {
            for (String subtype : mgr.getSubTypes(assetType)) {
                // %><input type="checkbox" name="assettype"
                // value="<%=assetType%>:<%=subtype%>" /><%=assetType%> /
                // <%=subtype%><br/><%
            }
            try {
                AssetTypeDef def = mgr.findByName(assetType, null);
                if (!def.getProperties().getIsFlexAsset()) {
                    // %><input type="checkbox" name="assettype"
                    // value="<%=assetType%>" /><%=assetType%><br/><%
                }
            } catch (Exception e) {
                out.println(e.getMessage());
                e.printStackTrace(new PrintWriter(out));
            }

        }
    }

}
