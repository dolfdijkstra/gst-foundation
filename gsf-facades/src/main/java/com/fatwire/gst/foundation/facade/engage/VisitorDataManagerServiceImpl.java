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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

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
        // todo: implement
    }

    @Override
    public String getAlias(String name) {
        // todo: implement
        return null;
    }

    @Override
    public void setScalar(String name, String value) {
        // todo: implement
    }

    @Override
    public String getScalar(String name) {
        // todo: implement
        return null;
    }

    @Override
    public void saveScalarObject(String name, Object value) {
        // todo: implement
    }

    @Override
    public Object loadScalarObject(String name) {
        // todo: implement
        return null;
    }

    @Override
    public void recordHistory(String definition, String name, Object value) {
        // todo: implement
    }

    @Override
    public void flushInactive(Date cutoff) {
        // todo: implement
    }

    @Override
    public void mergeInactive(Date cutoff) {
        // todo: implement
    }
}
