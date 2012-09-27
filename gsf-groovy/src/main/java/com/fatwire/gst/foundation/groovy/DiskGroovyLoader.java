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

package com.fatwire.gst.foundation.groovy;

import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.codehaus.groovy.control.CompilationFailedException;

import com.fatwire.gst.foundation.facade.logging.LogUtil;

/**
 * Loader for groovy script classes, configured via the ServletContext
 * 
 * @author Dolf Dijkstra
 * @since Mar 28, 2011
 */
/*
 * alternative method:
 * http://groovy.codehaus.org/Alternate+Spring-Groovy-Integration
 */
public class DiskGroovyLoader implements GroovyLoader {

    private Log logger = LogUtil.getLog(getClass());
    private GroovyScriptEngine groovyScriptEngine;

    private File scriptPath;
    private String configPath = "/WEB-INF/gsf-groovy";
    private int minimumRecompilationInterval = 0;

    public DiskGroovyLoader() {
        super();

    }

    public DiskGroovyLoader(ServletContext servletContext) {
        bootEngine(servletContext.getRealPath(configPath));

    }

    public void bootEngine(String path) {
        scriptPath = new File(path).getAbsoluteFile();
        scriptPath.mkdirs();
        if (!scriptPath.exists() || !scriptPath.isDirectory())
            throw new IllegalStateException("The realPath " + scriptPath + " is not a directory.");
        URI u = scriptPath.toURI();
        URL[] paths;
        try {
            paths = new URL[] { u.toURL() };
            groovyScriptEngine = new GroovyScriptEngine(paths, Thread.currentThread().getContextClassLoader());
            groovyScriptEngine.getConfig().setRecompileGroovySource(true);
            groovyScriptEngine.getConfig().setMinimumRecompilationInterval(minimumRecompilationInterval);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("The realPath " + scriptPath + " can't be made into a URL. "
                    + e.getMessage(), e);
        }

    }

    @Override
    public Object load(final String name) throws Exception {

        Class<?> c;
        try {
            c = groovyScriptEngine.loadScriptByName(toScriptName(name));
        } catch (ResourceException e) {

            if (logger.isDebugEnabled())
                logger.debug("GroovyScriptEngine was not able to load " + name + " as a script: " + e.getMessage()
                        + ". Now trying as a class.");
            String className = name.replace('/', '.');
            try {
                c = groovyScriptEngine.getGroovyClassLoader().loadClass(className);
            } catch (ClassNotFoundException cnfe) {
                if (logger.isDebugEnabled())
                    logger.debug("GroovyClassLoader was not able to load " + className + ": " + cnfe.getMessage()
                            + ". Aborting.");
                return null;

            }
        }

        return c.newInstance();
    }

    protected String toScriptName(String name) {
        if (name.endsWith(".groovy")) {
            return StringUtils.removeEnd(name, ".groovy").replace('.', '/') + ".groovy";
        } else
            return name.replace('.', '/') + ".groovy";
    }

    protected String toClassName(String name) {

        return name.replace('/', '.').replace('\\', '.').substring(0, name.length() - 7);
    }

    public void precompile() {
        doDir(scriptPath);

    }

    protected void doDir(File dir) {
        File[] listFiles = dir.listFiles(new FileFilter() {

            @Override
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

                    groovyScriptEngine.loadScriptByName(name);
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
        GroovyScriptEngine g = getGroovyScriptEngine();
        if (g != null) {
            g.getConfig().setMinimumRecompilationInterval(minimumRecompilationInterval);
        }
    }

    public GroovyScriptEngine getGroovyScriptEngine() {
        return groovyScriptEngine;
    }

    public void setGroovyScriptEngine(GroovyScriptEngine groovyScrptEngine) {
        this.groovyScriptEngine = groovyScrptEngine;
    }

}
