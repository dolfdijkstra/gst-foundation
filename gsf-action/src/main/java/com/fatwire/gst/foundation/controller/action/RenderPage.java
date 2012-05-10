/*
 * Copyright 2011 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.controller.action;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.controller.BaseRenderPage;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.facade.logging.LogUtil;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

import org.apache.commons.logging.Log;

/**
 * Generic page-rendering action.
 * 
 * @author Tony Field
 * @author Dolf Dijkstra
 * @since 2011-03-15
 */
public class RenderPage extends BaseRenderPage implements Action {

    public static final String PACKEDARGS = "packedargs";

    protected static final Log LOG = LogUtil.getLog(RenderPage.class);

    public void handleRequest(final ICS ics) {

        renderPage();
        LOG.debug("RenderPage execution complete");
    }

    @InjectForRequest
    public void setIcs(ICS ics) {
        this.ics = ics;
    }

    @InjectForRequest
    public void setWraCoreFieldDao(WraCoreFieldDao wraCoreFieldDao) {
        this.wraCoreFieldDao = wraCoreFieldDao;
    }

    @InjectForRequest
    public void setAliasCoreFieldDao(AliasCoreFieldDao aliasCoreFieldDao) {
        this.aliasCoreFieldDao = aliasCoreFieldDao;
    }

    @InjectForRequest
    public void setWraPathTranslationService(WraPathTranslationService wraPathTranslationService) {
        this.pathTranslationService = wraPathTranslationService;
    }

}
