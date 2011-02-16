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

package com.fatwire.gst.foundation.facade.runtag.render;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Implements the RENDER.GETBLOBURL tag.
 * <p/>
 * 
 * <pre>
 * &lt;RENDER.GETBLOBURL
 *       OUTSTR="VariableName"
 *       [BLOBTABLE="blobTable"]
 *       [BLOBKEY="primaryKeyName"]
 *       [BLOBWHERE="primaryKeyValue"]
 *       [BLOBCOL="column name"]
 *       [C="asset type"]
 *       [CID="asset id"]
 *       [ASSET="asset instance name"]
 *       [FIELD="asset field name"]
 *       [BLOBHEADER="MIMEtype"]
 *       [BLOBNOCACHE="false"]
 *       [ADDSESSION="true"]
 *       [DYNAMIC="true"]
 *       [BLOBHEADERNAMEN="value"]
 *       [BLOBHEADERVALUEN="value"]
 *       [CSBLOBID="session variable value"]
 *       [ASSEMBLER="uri assembler shortform"]
 *       [CONTAINER="servlet|portlet"]
 *       [FRAGMENT="fragment value"]
 *       [PARENTID="parent to log this blob as a dependency"]
 *       [SATELLITE="true|false"]
 *       [SCHEME="scheme value"]
 *       [AUTHORITY="authority value"]
 *       [PREFERREDFILE="filename"]
 *       [PREFERREDDIR="path"]
 *       &gt;
 *    &lt;/RENDER.GETBLOBURL&gt;
 * </pre>
 * 
 * @author Dolf Dijkstra
 * @since Feb 15, 2011
 */
/**
 * @author dolf
 *
 */
public final class GetBlobUrl extends AbstractTagRunner {
    //private static Log LOG = LogFactory.getLog(GetBlobUrl.class);

    public GetBlobUrl() {
        super("RENDER.GETBLOBURL");
    }

    /**
     * @param s
     */
    public void setAsset(String s) {
        set("ASSET", s);
    }

    /**
     * @param b
     */
    public void setAddSession(boolean b) {
        set("ADDSESSION", b ? "TRUE" : "FALSE");
    }

    /**
     * @param s
     */
    public void setAssembler(String s) {
        set("ASSEMBLER", s);
    }

    /**
     * @param s
     */
    public void setAuthority(String s) {
        set("AUTHORITY", s);
    }

    /**
     * @param s
     */
    public void setC(String s) {
        set("C", s);
    }

    /**
     * @param s
     */
    public void setCid(String s) {
        set("CID", s);
    }

    /**
     * @param s
     */
    public void setContainer(String s) {
        set("CONTAINER", s);
    }

    /**
     * @param s
     */
    public void setDynamic(String s) {
        set("DYNAMIC", s);
    }

    /**
     * @param s
     */
    public void setFragment(String s) {
        set("FRAGMENT", s);
    }

    /**
     * @param s
     */
    public void setOutstr(String s) {
        set("OUTSTR", s);
    }

    /**
     * @param s
     */
    public void setSatellite(String s) {
        set("SATELLITE", s);
    }

    /**
     * @param s
     */
    public void setScheme(String s) {
        set("SCHEME", s);
    }

    /**
     * @param s
     */
    public void setBlobTable(String s) {
        set("BLOBTABLE", s);
    }

    /**
     * @param s
     */
    public void setBlobKey(String s) {
        set("BLOBKEY", s);
    }

    /**
     * @param s
     */
    public void setBlobWhere(String s) {
        set("BLOBWHERE", s);
    }

    /**
     * @param s
     */
    public void setBlobCol(String s) {
        set("BLOBCOL", s);
    }

    /**
     * @param s
     */
    public void setField(String s) {
        set("FIELD", s);
    }

    /**
     * @param s
     */
    public void setBobHeader(String s) {
        set("BLOBHEADER", s);
    }

    /**
     * @param s
     */
    public void setBlobNoCache(String s) {
        set("BLOBNOCACHE", s);
    }

    /**
     * @param n
     * @param s
     */
    public void setBlobHeaderName(int n, String s) {
        set("BLOBHEADERNAME" + n, s);
    }

    /**
     * @param n
     * @param s
     */
    public void setBlobHeaderValue(int n, String s) {
        set("BLOBHEADERVALUE" + n, s);
    }

    /**
     * @param s
     */
    public void setCSBlobId(String s) {
        set("CSBLOBID", s);
    }

    /**
     * @param s
     */
    public void setParentId(String s) {
        set("PARENTID", s);
    }

    /**
     * @param s
     */
    public void setPreferredFile(String s) {
        set("PREFERREDFILE", s);
    }

    /**
     * @param s
     */
    public void setPreferredDir(String s) {
        set("PREFERREDDIR", s);
    }

}
