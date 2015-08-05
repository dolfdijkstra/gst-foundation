/*
 * Copyright (c) 2015 Function1 Inc. All rights reserved.
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
package com.fatwire.gst.foundation.facade.engage;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.gst.foundation.facade.ilist.TwoColumnIList;
import com.fatwire.gst.foundation.facade.runtag.vdm.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Map;

/**
 * @author Tony Field
 * @since 15-08-04 6:07 PM
 */
public class VisitorDataManagerServiceImpl implements VisitorDataManagerService {

    private static final Log LOG = LogFactory.getLog(VisitorDataManagerService.class);
    private final ICS ics;

    public VisitorDataManagerServiceImpl(ICS ics) {
        this.ics = ics;
        LOG.trace("Created VisitorDataManagerServiceImpl");
        LOG.warn("Implementation is not complete");
    }

    @Override
    public void setAlias(String name, String value) {
        SetAliasWithoutReset tag = new SetAliasWithoutReset(name, value);
        tag.execute(ics);
    }

    @Override
    public String getAlias(String name) {
        String var = "getalias_"+ ics.genID(false);
        GetAlias tag = new GetAlias(name, var);
        tag.execute(ics);
        String val = ics.GetVar(var);
        ics.RemoveVar(var);
        return val;
    }

    @Override
    public void setScalar(String name, String value) {
        SetScalarWithoutReset tag = new SetScalarWithoutReset(name, value);
        tag.execute(ics);
    }

    @Override
    public String getScalar(String name) {
        String var = "getscalar_"+ics.genID(false);
        GetScalar tag = new GetScalar(name, var);
        tag.execute(ics);
        String val = ics.GetVar(var);
        ics.RemoveVar(val);
        return val;
    }

    @Override
    public void saveScalarObject(String name, Object value) {
        String var = "saveScalar_"+ics.genID(false);
        ics.SetObj(var, value);
        SaveScalarObject tag = new SaveScalarObject();
        tag.setAttribute(name);
        tag.setObject(var);
        tag.execute(ics);
        ics.RemoveVar(var);
    }

    @Override
    public Object loadScalarObject(String name) {
        String var = "loadscalar_"+ics.genID(false);
        LoadScalarObject tag = new LoadScalarObject(name, var);
        tag.execute(ics);
        Object result = ics.GetObj(var);
        ics.SetObj(var, null);
        return result;
    }

    @Override
    public void recordHistory(String definition, String name, Object value) {
        String listName = "recordHistory_"+ics.genID(false);
        TwoColumnIList list = new TwoColumnIList(listName, "field", "value");
        list.addRow(name, value);
        ics.RegisterList(listName, list);
        RecordHistory tag = new RecordHistory(definition, listName);
        tag.execute(ics);
        ics.RegisterList(listName, null);
    }

    @Override
    public void recordHistory(String definition, Map<String, Object> values) {
        String listName = "recordHistory_"+ics.genID(false);
        TwoColumnIList list = new TwoColumnIList(listName, "field", "value");
        for (String key : values.keySet()) {
            list.addRow(key, values.get(key));
        }
        ics.RegisterList(listName, list);
        RecordHistory tag = new RecordHistory(definition, listName);
        tag.execute(ics);
        ics.RegisterList(listName, null);
    }

    @Override
    public void flushInactive(Date cutoff) {
        FlushInactive tag = new FlushInactive();
        tag.setStartdate(cutoff);
        tag.execute(ics);
    }

    @Override
    public void mergeInactive(Date cutoff) {
        MergeInactive tag = new MergeInactive();
        tag.setStartdate(cutoff);
        tag.execute(ics);
    }
}
