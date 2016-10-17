/*
 * Copyright 2012 Metastratus Web Solutions Limited.  All Rights Reserved.
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
package com.yourcompany.owcs.config;

import javax.servlet.ServletContext;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.support.DefaultWebAppContext;

/**
 * This class is used to configure the GSF for a project.  It provides a lot of default functionality and
 * can be used to eliminate xml-based configuration.
 *
 * @author Tony Field
 * @since 2012-08-24
 */
public final class MyCompanyWebAppContext extends DefaultWebAppContext {

    public MyCompanyWebAppContext(final ServletContext context) {
        super(context);

    }

    public MyCompanyWebAppContext(final ServletContext context, final AppContext parent) {
        super(context, parent);

    }

    // TODO: Something interesting

}
