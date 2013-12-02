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

package com.fatwire.gst.foundation.groovy.spring;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationObjectSupport;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.groovy.DiskGroovyLoader;
import com.fatwire.gst.foundation.groovy.GroovyLoader;

/**
 * Loader for groovy script classes, configured via spring and ServletContext
 * 
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
/*
 * alternative method:
 * http://groovy.codehaus.org/Alternate+Spring-Groovy-Integration
 */
public class SpringDiskGroovyLoader extends WebApplicationObjectSupport implements GroovyLoader {
    private DiskGroovyLoader groovyLoader;
    private String configPath;
    private int minimumRecompilationInterval = 0;

    public SpringDiskGroovyLoader() {
        super();

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.web.context.support.WebApplicationObjectSupport#
     * initServletContext(javax.servlet.ServletContext)
     */
    @Override
    protected void initServletContext(ServletContext servletContext) {
        groovyLoader = new DiskGroovyLoader();
        groovyLoader.setConfigPath(configPath);
        groovyLoader.setMinimumRecompilationInterval(minimumRecompilationInterval);
        groovyLoader.bootEngine(getConfigPath());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.fatwire.gst.foundation.groovy.spring.GroovyLoader#load(java.lang.
     * String)
     */
    @Override
    public Object load(ICS ics,String name) throws Exception {

        return groovyLoader.load(ics,name);
    }

    /**
     * @return the configPath
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * @param configPath the configPath to set
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * @return the minimumRecompilationInterval
     */
    public int getMinimumRecompilationInterval() {
        return minimumRecompilationInterval;
    }

    /**
     * Sets the minimumRecompilationInterval of the GroovyScriptEngine
     * configuration.
     * 
     * @param minimumRecompilationInterval the minimumRecompilationInterval to
     *            set
     */
    public void setMinimumRecompilationInterval(int minimumRecompilationInterval) {
        this.minimumRecompilationInterval = minimumRecompilationInterval;

        if (groovyLoader != null) {
            groovyLoader.setMinimumRecompilationInterval(minimumRecompilationInterval);
        }
    }

}
