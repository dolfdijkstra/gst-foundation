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

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletContext;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.web.context.support.WebApplicationObjectSupport;

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
public class GroovyLoader extends WebApplicationObjectSupport {
    private GroovyScriptEngine gse;
    private File scriptPath;
    private String configPath = "/WEB-INF/gsf-groovy";
    private int minimumRecompilationInterval = 0;

    public GroovyLoader() {
        super();

    }

    public GroovyLoader(ServletContext servletContext) {
        bootEngine(servletContext.getRealPath(configPath));

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.web.context.support.WebApplicationObjectSupport#
     * initServletContext(javax.servlet.ServletContext)
     */
    @Override
    protected void initServletContext(ServletContext servletContext) {
        bootEngine(servletContext.getRealPath(configPath));

    }

    void bootEngine(String path) {
        scriptPath = new File(path).getAbsoluteFile();
        scriptPath.mkdirs();
        if (!scriptPath.exists() || !scriptPath.isDirectory())
            throw new IllegalStateException("The realPath " + scriptPath + " is not a directory.");
        URI u = scriptPath.toURI();
        URL[] paths;
        try {
            paths = new URL[] { u.toURL() };
            gse = new GroovyScriptEngine(paths, Thread.currentThread().getContextClassLoader());
            gse.getConfig().setRecompileGroovySource(true);
            gse.getConfig().setMinimumRecompilationInterval(minimumRecompilationInterval);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("The realPath " + scriptPath + " can't be made into a URL. "
                    + e.getMessage(), e);
        }

    }

    /**
     * @param name the name of the class
     * @return the Object loaded by Groovy
     * @throws Exception
     */
    public Object load(String name) throws Exception {
        Class<?> c;
        try {
            c = gse.loadScriptByName(toScriptName(name));
        } catch (Exception e) {
            logger.debug("GroovyScriptEngine was not able to load " + name + " as a script: " + e.getMessage()
                    + ". Now trying as a class.");
            c = gse.getGroovyClassLoader().loadClass(name);
        }

        return c.newInstance();
    }

    protected String toScriptName(String name) {
        return name.replace('.', '/') + ".groovy";
    }

    protected String toClassName(String name) {

        return name.replace('/', '.').replace('\\', '.').substring(0, name.length() - 7);
    }

    public boolean isValidScript(String script) {
        return new File(scriptPath, script).exists();
    }

    public void precompile() {
        doDir(scriptPath);

    }

    protected void doDir(File dir) {
        File[] listFiles = dir.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".groovy");
            }

        });
        for (File file : listFiles) {
            if (file.isDirectory()) {
                doDir(file);
            } else {
                String name = file.getAbsolutePath().substring(scriptPath.getAbsolutePath().length() + 1);
                try {

                    gse.loadScriptByName(name);
                } catch (CompilationFailedException e) {
                    logger.warn(e.getMessage() + " on " + name + " during precompilation.");
                } catch (ResourceException e) {
                    logger.warn(e.getMessage() + " on " + name + " during precompilation.");
                } catch (ScriptException e) {
                    logger.warn(e.getMessage() + " on " + name + " during precompilation.");
                }
            }
        }
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
    }

}
