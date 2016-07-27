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
package com.fatwire.gst.foundation.controller.support;

import com.fatwire.gst.foundation.controller.AppContext;

import javax.servlet.ServletContext;

/**
 * @deprecated see {@link tools.gsf.config.WebAppContext}
 */
public class WebAppContext extends tools.gsf.config.WebAppContext {
    private final ServletContext context;

    public WebAppContext(ServletContext context, AppContext parent) {
        super(context, parent);
        this.context = context;
    }

    public WebAppContext(ServletContext context) {
        this(context, null);
    }

    protected ServletContext getServletContext() {
        return context;
    }
}
