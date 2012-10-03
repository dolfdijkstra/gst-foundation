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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fatwire.gst.foundation.controller.AppContext;
import com.fatwire.gst.foundation.controller.action.ActionNameResolver;
import com.fatwire.gst.foundation.controller.action.support.NullActionNameResolver;
import com.fatwire.gst.foundation.facade.logging.LogUtil;

public class SpringWebAppContext implements AppContext {
    protected static final Log LOG = LogUtil.getLog(SpringWebAppContext.class);
    private static final ActionNameResolver nullActionNameResolver = new NullActionNameResolver();
    private final WebApplicationContext wac;

    /**
     * This constructor is needed for the {@link WebAppContextLoader}. 
     * 
     * @param ctx
     * @param app
     */
    public SpringWebAppContext(ServletContext ctx, AppContext app) {
        this.wac = WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
        Assert.notNull(wac);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String name, Class<T> t) {
        T bean = null;
        if (wac.containsBean(name)) {
            bean = (T) wac.getBean(name, t);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using " + name + " as configured in spring: " + bean.getClass().getName());
            }
        } else {
            LOG.debug("Cannot find the '" + name + "' bean in the Spring WebApplicationContext: " + wac.toString());
            try {
                bean = TemplateMethodFactory.createByMethod(this, t);
            } catch (final Exception e) {
                LOG.debug(e.getMessage() + " while trying to create  a" + t.getName());
            }
        }
        return bean;
    }

    public ActionNameResolver createActionNameResolver() {
        return nullActionNameResolver;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

}
