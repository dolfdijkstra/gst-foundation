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

import com.fatwire.gst.foundation.controller.WraRenderPage;
import com.fatwire.gst.foundation.controller.annotation.InjectForRequest;
import com.fatwire.gst.foundation.url.WraPathTranslationService;
import com.fatwire.gst.foundation.wra.AliasCoreFieldDao;
import com.fatwire.gst.foundation.wra.WraCoreFieldDao;

/**
 * Generic page-rendering action. The logic is implemented in
 * {@link WraRenderPage}.
 * 
 * @author Dolf Dijkstra
 * @since 2011-03-15
 * 
 * @deprecated as of release 12.x, replace GSF Actions with WCS 12c's native Controllers and/or wrappers
 * 
 */
public class RenderPage extends WraRenderPage implements Action {

    public void handleRequest(final ICS ics) {

        renderPage();
        
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
