/*
 * Copyright 2010 FatWire Corporation. All Rights Reserved.
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
package com.fatwire.gst.foundation.url;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import com.fatwire.cs.core.uri.Assembler;
import com.fatwire.cs.core.uri.Definition;
import com.fatwire.cs.core.uri.QueryAssembler;
import com.fatwire.cs.core.uri.Simple;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dispatching assembler, dispatching to Web-referenceable asset path
 * assembler only invoked if rendermode=live of blank, in other cases to the
 * QueryAssembler.
 * 
 * @author Dolf Dijkstra
 * @since November 16,2011
 */
public final class WraPathPreviewAssembler extends LightweightAbstractAssembler {
    protected static final Logger LOG = LoggerFactory.getLogger("tools.gsf.foundation.url.WraPathPreviewAssembler");
    /**
     * Name of query string parameter for virtual webroot
     */
    private static final String VIRTUAL_WEBROOT = "virtual-webroot";

    /**
     * Name of query string parameter for url-path
     */
    private static final String URL_PATH = "url-path";

    /**
     * Name of packedargs param
     */
    private static final String PACKEDARGS = "packedargs";

    private static final Collection<String> BAD_ARGS = Arrays.asList(VIRTUAL_WEBROOT, URL_PATH);
    /**
     * The assembler to use in case the input does not support the WRAPath
     * approach
     */
    private Assembler queryAssembler;

    private Assembler wraPathAssembler;

    /**
     * Set properties, initializing the assembler
     * 
     * @param properties configuration properties
     */
    public void setProperties(Properties properties) {
        wraPathAssembler = _instantiateAssembler(WraPathAssembler.class);
        wraPathAssembler.setProperties(properties);

        queryAssembler = _instantiateAssembler(QueryAssembler.class);
        queryAssembler.setProperties(properties);

    }

    private Assembler _instantiateAssembler(Class<?> c) {
        try {
            Object o = c.newInstance();
            return (Assembler) o;

        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate assembler: " + c.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not instantiate assembler: " + c.getName(), e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Assembler class is not an instance of Assembler: " + c.getName(), e);
        }
    }

    /**
     * Looks for virtual-webroot and url-path. If found, concatenates
     * virtual-webroot and url-path. Once core query params are suppressed, the
     * remaining params are appended to the URL.
     * 
     * @param definition definition object
     * @return valid URI
     * @throws URISyntaxException exception thrown on URI syntax error
     */
    public URI assemble(Definition definition) throws URISyntaxException {
        String rendermode = definition.getParameter("rendermode");
        LOG.trace("rendermode: " + rendermode);
        if (StringUtils.isBlank(rendermode) || "live".equals(rendermode)) {
            // rendermode is not set or live
            return wraPathAssembler.assemble(definition);
        }
        // preview etc
        Simple copy = new Simple(definition.sessionEncode(), definition.getSatelliteContext(),
                definition.getContainerType(), definition.getScheme(), definition.getAuthority(),
                definition.getAppType(), definition.getFragment());
        for (Object o : definition.getParameterNames()) {
            String key = (String) o;

            if (PACKEDARGS.equals(key)) {
                String[] pa = excludeFromPackedargs(definition.getParameters(PACKEDARGS), BAD_ARGS);
                if (pa != null && pa.length > 0 && StringUtils.isNotBlank(pa[0])) {
                    copy.setQueryStringParameter(PACKEDARGS, pa);
                }
            } else if (VIRTUAL_WEBROOT.equals(key)) {
                // ignore
            } else if (URL_PATH.equals(key)) {
                // ignore
            } else {
                copy.setQueryStringParameter(key, definition.getParameters(key));
            }

        }

        return queryAssembler.assemble(copy);
    }

    public Definition disassemble(URI uri, Definition.ContainerType containerType) throws URISyntaxException {
        return wraPathAssembler.disassemble(uri, containerType);
    }

    public Assembler getTheBackupAssembler() {
        return queryAssembler;
    }
}
