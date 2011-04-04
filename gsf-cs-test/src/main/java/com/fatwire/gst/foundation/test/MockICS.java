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

package com.fatwire.gst.foundation.test;

import java.io.OutputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import COM.FutureTense.Cache.Satellite;
import COM.FutureTense.ContentServer.PageData;
import COM.FutureTense.Interfaces.FTVAL;
import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IJSPObject;
import COM.FutureTense.Interfaces.IList;
import COM.FutureTense.Interfaces.IMIMENotifier;
import COM.FutureTense.Interfaces.IProperties;
import COM.FutureTense.Interfaces.ISearchEngine;
import COM.FutureTense.Interfaces.IServlet;
import COM.FutureTense.Interfaces.ISyncHash;
import COM.FutureTense.Interfaces.IURLDefinition;
import COM.FutureTense.Interfaces.PastramiEngine;
import COM.FutureTense.Util.ftErrors;
import COM.FutureTense.XML.Template.Seed;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.cs.core.uri.Definition;

@SuppressWarnings("deprecation")
public class MockICS implements ICS {

    public boolean AppEvent(String arg0, String arg1, String arg2, FTValList arg3) {

        return false;
    }

    public boolean BlobServer(FTValList arg0, IMIMENotifier arg1, OutputStream arg2) {

        return false;
    }

    public boolean CallElement(String arg0, FTValList arg1) {

        return false;
    }

    public IList CallSQL(String arg0, String arg1, int arg2, boolean arg3, StringBuffer arg4) {

        return null;
    }

    public String getSQL(String s) {
        throw new UnsupportedOperationException("NYI");
    }

    public IList CallSQL(String arg0, String arg1, int arg2, boolean arg3, boolean arg4, StringBuffer arg5) {

        return null;
    }

    public IList CatalogDef(String arg0, String arg1, StringBuffer arg2) {

        return null;
    }

    public boolean CatalogManager(FTValList arg0) {

        return false;
    }

    public boolean CatalogManager(FTValList arg0, Object arg1) {

        return false;
    }

    public void ClearErrno() {

    }

    public boolean CommitBatchedCommands(Object arg0) {

        return false;
    }

    public boolean CopyList(String arg0, String arg1) {

        return false;
    }

    public boolean DeleteSynchronizedHash(String arg0) {

        return false;
    }

    public boolean DestroyEvent(String arg0) {

        return false;
    }

    public void DisableCache() {

    }

    public boolean DisableEvent(String arg0) {

        return false;
    }

    public void DisableFragmentCache() {

    }

    public boolean EmailEvent(String arg0, String arg1, String arg2, String arg3) {

        return false;
    }

    public boolean EmailEvent(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5,
            FTValList arg6) {

        return false;
    }

    public boolean EnableEvent(String arg0) {

        return false;
    }

    public boolean FlushCatalog(String arg0) {

        return false;
    }

    public void FlushStream() {

    }

    public byte[] GetBin(String arg0) {

        return null;
    }

    public int GetCatalogType(String arg0) {

        return 0;
    }

    public FTVAL GetCgi(String arg0) {

        return null;
    }

    public int GetCounter(String arg0) throws Exception {

        return 0;
    }

    public int GetErrno() {

        return 0;
    }

    public IList GetList(String arg0) {

        return null;
    }

    public IList GetList(String arg0, boolean arg1) {

        return null;
    }

    public Object GetObj(String arg0) {

        return null;
    }

    public String GetProperty(String arg0) {

        return null;
    }

    public String GetProperty(String arg0, String arg1, boolean arg2) {

        return null;
    }

    public String GetSSVar(String arg0) {

        return null;
    }

    @SuppressWarnings("unchecked")
    public Enumeration GetSSVars() {

        return null;
    }

    public ISearchEngine GetSearchEngine(String arg0, String arg1, StringBuffer arg2) {

        return null;
    }

    public String GetSearchEngineList() {

        return null;
    }

    public ISyncHash GetSynchronizedHash(String arg0, boolean arg1, int arg2, int arg3, boolean arg4, boolean arg5) {

        return null;
    }

    @SuppressWarnings("unchecked")
    public ISyncHash GetSynchronizedHash(String arg0, boolean arg1, int arg2, int arg3, boolean arg4, boolean arg5,
            Collection arg6) {

        return null;
    }

    public String GetVar(String arg0) {

        return null;
    }

    @SuppressWarnings("unchecked")
    public Enumeration GetVars() {

        return null;
    }

    public boolean IndexAdd(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6,
            FTValList arg7, FTValList arg8, FTValList arg9, String arg10, String arg11, StringBuffer arg12) {

        return false;
    }

    public boolean IndexCreate(String arg0, FTValList arg1, String arg2, String arg3, StringBuffer arg4) {

        return false;
    }

    public boolean IndexDestroy(String arg0, String arg1, String arg2, StringBuffer arg3) {

        return false;
    }

    public boolean IndexExists(String arg0, String arg1, String arg2, StringBuffer arg3) {

        return false;
    }

    public boolean IndexRemove(String arg0, String arg1, String arg2, String arg3, StringBuffer arg4) {

        return false;
    }

    public boolean IndexReplace(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5,
            String arg6, FTValList arg7, FTValList arg8, FTValList arg9, String arg10, String arg11, StringBuffer arg12) {

        return false;
    }

    public boolean InsertPage(String arg0, FTValList arg1) {

        return false;
    }

    public boolean IsElement(String arg0) {

        return false;
    }

    public boolean IsSystemSecure() {

        return false;
    }

    public boolean IsTracked(String arg0) {

        return false;
    }

    public boolean LoadProperty(String arg0) {

        return false;
    }

    public void LogMsg(String arg0) {

    }

    @SuppressWarnings("unchecked")
    public int Mirror(Vector arg0, String arg1, String arg2, String arg3, String arg4, String arg5, int arg6,
            boolean arg7, boolean arg8, int arg9, StringBuffer arg10) {

        return 0;
    }

    @SuppressWarnings("unchecked")
    public int Mirror(Vector arg0, Vector arg1, String arg2, String arg3, String arg4, String arg5, String arg6,
            int arg7, boolean arg8, boolean arg9, int arg10, StringBuffer arg11) {

        return 0;
    }

    @SuppressWarnings("unchecked")
    public int Mirror(Vector arg0, String arg1, String arg2, String arg3, String arg4, String arg5, int arg6,
            boolean arg7, String arg8, String arg9, String arg10, String arg11, boolean arg12, int arg13,
            StringBuffer arg14) {

        return 0;
    }

    public int Mirror(IList arg0, String arg1, String arg2, String arg3, String arg4, String arg5, int arg6,
            boolean arg7, String arg8, String arg9, String arg10, String arg11, boolean arg12, int arg13, String arg14,
            StringBuffer arg15) {

        return 0;
    }

    @SuppressWarnings("unchecked")
    public int Mirror(Vector arg0, Vector arg1, String arg2, String arg3, String arg4, String arg5, String arg6,
            int arg7, boolean arg8, String arg9, String arg10, String arg11, String arg12, boolean arg13, int arg14,
            StringBuffer arg15) {

        return 0;
    }

    public Seed NewSeedFromTagname(String arg0) {

        return null;
    }

    public Object PopObj(String arg0) {

        return null;
    }

    public void PopVars() {

    }

    public boolean PushObj(String arg0, Object arg1) {

        return false;
    }

    public void PushVars() {

    }

    public IList QueryEvents(String arg0, String arg1, Boolean arg2, String arg3) {

        return null;
    }

    public int RTCommit(String arg0, String arg1, String arg2, boolean arg3) {

        return 0;
    }

    public int RTDeleteRevision(String arg0, String arg1, int arg2) {

        return 0;
    }

    public IList RTHistory(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) {

        return null;
    }

    public IList RTInfo(String arg0, String arg1) {

        return null;
    }

    public int RTLock(String arg0, String arg1) {

        return 0;
    }

    public int RTRelease(String arg0, String arg1) {

        return 0;
    }

    public IList RTRetrieveRevision(String arg0, String arg1, int arg2, String arg3) {

        return null;
    }

    public IList RTRetrieveRevision(String arg0, String arg1, String arg2, String arg3) {

        return null;
    }

    public int RTRollback(String arg0, String arg1, int arg2) {

        return 0;
    }

    public int RTRollback(String arg0, String arg1, String arg2) {

        return 0;
    }

    public int RTSetVersions(String arg0, int arg1) {

        return 0;
    }

    public int RTTrackTable(String arg0, String arg1, int arg2) {

        return 0;
    }

    public int RTUnlockRecord(String arg0, String arg1) {

        return 0;
    }

    public int RTUntrackTable(String arg0) {

        return 0;
    }

    public IList ReadEvent(String arg0, String arg1) {

        return null;
    }

    public String ReadPage(String arg0, FTValList arg1) {

        return null;
    }

    public boolean RegisterList(String arg0, IList arg1) {

        return false;
    }

    public void RemoveCounter(String arg0) {

    }

    public void RemoveSSVar(String arg0) {

    }

    public void RemoveVar(String arg0) {

    }

    public boolean RenameList(String arg0, String arg1) {

        return false;
    }

    public String ResolveVariables(String arg0) {

        return null;
    }

    public String ResolveVariables(String arg0, boolean arg1) {

        return null;
    }

    public boolean RestoreProperty(boolean arg0) {

        return false;
    }

    public boolean RollbackBatchedCommands(Object arg0) {

        return false;
    }

    public IList SQL(PreparedStmt arg0, StatementParam arg1, boolean arg2) {

        return null;
    }

    public IList SQL(String arg0, String arg1, String arg2, int arg3, boolean arg4, StringBuffer arg5) {

        return null;
    }

    public IList SQL(String arg0, String arg1, String arg2, int arg3, boolean arg4, boolean arg5, StringBuffer arg6) {

        return null;
    }

    public String SQLExp(String arg0, String arg1, String arg2, String arg3, String arg4) {

        return null;
    }

    public String SQLExp(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5) {

        return null;
    }

    public IList Search(String arg0, String arg1, String arg2, String arg3, int arg4, FTValList arg5, String arg6,
            String arg7, String arg8, StringBuffer arg9) {

        return null;
    }

    public boolean SearchDateToNative(String arg0, StringBuffer arg1, String arg2, String arg3, StringBuffer arg4) {

        return false;
    }

    public IList SelectTo(String arg0, String arg1, String arg2, String arg3, int arg4, String arg5, boolean arg6,
            StringBuffer arg7) {

        return null;
    }

    public boolean SendMail(String arg0, String arg1, String arg2) {

        return false;
    }

    public boolean SendMail(String arg0, String arg1, String arg2, String arg3, String arg4) {

        return false;
    }

    public boolean SendMail(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, FTValList arg6) {

        return false;
    }

    public boolean SessionExists(String arg0) {

        return false;
    }

    public String SessionID() {

        return null;
    }

    public boolean SetCookie(String arg0, String arg1, int arg2, String arg3, String arg4, boolean arg5) {

        return false;
    }

    public void SetCounter(String arg0, int arg1) throws Exception {

    }

    public void SetErrno(int arg0) {

    }

    public boolean SetObj(String arg0, Object arg1) {

        return false;
    }

    public void SetSSVar(String arg0, String arg1) {

    }

    public void SetSSVar(String arg0, int arg1) {

    }

    public void SetVar(String arg0, String arg1) {

    }

    public void SetVar(String arg0, int arg1) {

    }

    public void SetVar(String arg0, FTVAL arg1) {

    }

    public Object StartBatchContext() {

        return null;
    }

    public void StreamBinary(byte[] arg0, int arg1, int arg2) {

    }

    public void StreamEvalBytes(String arg0) {

    }

    public void StreamHeader(String arg0, String arg1) {

    }

    public void StreamText(String arg0) {

    }

    public void ThrowException() {

    }

    public boolean TreeManager(FTValList arg0) {

        return false;
    }

    public boolean TreeManager(FTValList arg0, Object arg1) {

        return false;
    }

    public boolean UserIsMember(String arg0) {

        return false;
    }

    public void close() {

    }

    public boolean dbDebug() {

        return false;
    }

    @SuppressWarnings("unchecked")
    public void decode(String arg0, Map arg1) {

    }

    public IJSPObject deployJSPData(String arg0, String arg1, StringBuffer arg2) {

        return null;
    }

    public IJSPObject deployJSPFile(String arg0, String arg1, StringBuffer arg2) {

        return null;
    }

    public String diskFileName() {

        return null;
    }

    public String diskFileName(String arg0, FTValList arg1) {

        return null;
    }

    public String diskFileName(String arg0, String arg1) {

        return null;
    }

    @SuppressWarnings("unchecked")
    public String encode(String arg0, Map arg1, boolean arg2) {

        return null;
    }

    public boolean eventDebug() {

        return false;
    }

    public String genID(boolean arg0) {

        return null;
    }

    public Object getAttribute(String arg0) {

        return null;
    }

    @SuppressWarnings("unchecked")
    public Enumeration getAttributeNames() {

        return null;
    }

    public ftErrors getComplexError() {

        return null;
    }

    public String getCookie(String arg0) {

        return null;
    }

    public IProperties getIProperties() {

        return null;
    }

    public IServlet getIServlet() {

        return null;
    }

    public String getLocaleString(String arg0, String arg1) {

        return null;
    }

    public String getNamespace() {

        return null;
    }

    public PageData getPageData(String arg0) {

        return null;
    }

    public PastramiEngine getPastramiEngine() {

        return null;
    }

    public Satellite getSatellite(String arg0) {

        return null;
    }

    public int getTrackingStatus(String arg0, String arg1) {

        return 0;
    }

    public String getURL(IURLDefinition arg0) {

        return null;
    }

    public String getURL(Definition arg0, String arg1) {

        return null;
    }

    public Principal getUserPrincipal() {

        return null;
    }

    public byte[] grabCacheStatus() {

        return null;
    }

    public FTValList grabHeaders() {

        return null;
    }

    public boolean ioErrorThrown() {

        return false;
    }

    public boolean isCacheable(String arg0) {

        return false;
    }

    public String literal(String arg0, String arg1, String arg2) {

        return null;
    }

    public String[] pageCriteriaKeys(String arg0) {

        return null;
    }

    public String pageURL() {

        return null;
    }

    public String pageURL(String arg0, FTValList arg1) {

        return null;
    }

    public boolean pastramiDebug() {

        return false;
    }

    public boolean pgCacheDebug() {

        return false;
    }

    public void removeAttribute(String arg0) {

    }

    public boolean rsCacheDebug() {

        return false;
    }

    public String runTag(String arg0, FTValList arg1) {

        return null;
    }

    public boolean sessionDebug() {

        return false;
    }

    public void setAttribute(String arg0, Object arg1) {

    }

    public void setComplexError(ftErrors arg0) {

    }

    public boolean syncDebug() {

        return false;
    }

    public boolean systemDebug() {

        return false;
    }

    public boolean systemSession() {

        return false;
    }

    public boolean timeDebug() {

        return false;
    }

    public boolean xmlDebug() {

        return false;
    }

	public IList CatalogIndexDef(String arg0, String arg1, StringBuffer arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
