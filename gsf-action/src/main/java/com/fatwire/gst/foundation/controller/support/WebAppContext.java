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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.facade.logging.Log;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

public class WebAppContext implements AppContext {
    protected static final Log LOG = LogUtil.getLog(WebAppContext.class);

    public static final String WEB_CONTEXT_NAME = "gsf/AppContext";

    private final ServletContext context;
    private final AppContext parent;

    private Map<String, Object> localScope = new HashMap<String, Object>();

    /**
     * This constructor is needed for the {@link WebAppContextLoader}.
     * 
     * @param context
     * @param parent
     */
    public WebAppContext(ServletContext context, AppContext parent) {
        super();
        this.context = context;
        this.parent = parent;
    }

    /**
     * Constructor with null parent.
     * 
     * @param context
     */
    public WebAppContext(ServletContext context) {
        this(context, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getBean(String name, Class<T> c) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("name cannot be null or empty");

        if (c.isArray()) {
            throw new IllegalArgumentException("Arrays are not supported");
        }

        Object o = null;

        o = localScope.get(name);

        if (o == null) {
            LOG.debug("Asking for bean by name %s of type %s.",name,  c.getName());
            try {
                // TODO: medium: check for other method signatures

                o = TemplateMethodFactory.createByMethod(this, c);
                if (o != null && c.isAssignableFrom(o.getClass())) {
                    localScope.put(name, o);

                }

            } catch (final NoSuchMethodException e) {

                try {
                    if (parent != null)
                        o = parent.getBean(name, c); // don't register locally
                                                     // if found
                    else {
                        LOG.debug("Could not create  a %s via a Template method, trying via constructor.",c.getName());
                        o = TemplateMethodFactory.createByConstructor(c);
                        if (o != null && c.isAssignableFrom(o.getClass())) {
                            localScope.put(name, o);
                        }
                    }
                } catch (final RuntimeException e1) {
                    throw e1;
                } catch (final Exception e1) {
                    throw new RuntimeException(e1);
                }
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        }
        return (T) o;
    }

    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }
}
