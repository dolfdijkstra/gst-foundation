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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fatwire.cs.core.uri.Assembler;
import com.fatwire.cs.core.uri.Definition;
import com.fatwire.cs.core.uri.QueryAssembler;
import com.fatwire.cs.core.uri.Simple;
import com.openmarket.xcelerate.publish.PubConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static COM.FutureTense.Interfaces.Utilities.goodString;

/**
 * Web-referenceable asset path assembler.
 *
 * @author Tony Field
 * @since Jul 20, 2010
 */
public final class WraPathAssembler extends LightweightAbstractAssembler {
    protected static final Log LOG = LogFactory.getLog(WraPathAssembler.class.getName());

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

    /**
     * The assembler to use in case the input does not support the WRAPath approach
     */
    private final Assembler theBackupAssembler = new QueryAssembler();

    /**
     * The pagename that is used when disassembling URLs
     */
    private String[] pagename = {"GST/Dispatcher"};

    /**
     * List of parameters that are effectively embedded in the URL.  These parameters will
     * not be relayed through the URL as query string parameters.
     */
    private static List<String> EMBEDDED_PARAMS = Arrays.asList(PubConstants.PAGENAME, PubConstants.CHILDPAGENAME, VIRTUAL_WEBROOT, URL_PATH, PubConstants.c, PubConstants.cid);
    public static final String DISPATCHER_PROPNAME = "com.fatwire.gst.foundation.url.wrapathassembler.dispatcher";

    /**
     * Set properties, initializing the assembler
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        theBackupAssembler.setProperties(properties);
        pagename[0] = getProperty(DISPATCHER_PROPNAME, "GST/Dispatcher");
        LOG.info("Initialized " + WraPathAssembler.class + " with properties " + properties);
    }

    /**
     * Looks for virtual-webroot and url-path.  If found, concatenates virtual-webroot and url-path.
     * Once core query params are suppressed, the remaining params are appended to the URL.
     *
     * @param definition
     * @return valid URI
     * @throws URISyntaxException
     */
    public URI assemble(Definition definition) throws URISyntaxException {

        // get packedargs just in case we need them
        Map<String, String[]> packedargs = getPackedargs(definition);

        String virtualWebroot = definition.getParameter(VIRTUAL_WEBROOT);
        if (!goodString(virtualWebroot) && packedargs.containsKey(VIRTUAL_WEBROOT)) {
            String[] s = packedargs.get(VIRTUAL_WEBROOT);
            if (s != null && s.length > 0) virtualWebroot = s[0];
        }
        String urlPath = definition.getParameter(URL_PATH);
        if (!goodString(urlPath) && packedargs.containsKey(URL_PATH)) {
            String[] s = packedargs.get(URL_PATH);
            if (s != null && s.length > 0) urlPath = s[0];
        }
        if (!goodString(virtualWebroot) || !goodString(urlPath)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("WRAPathAssembler can't assemble definition due to missing core params. Definition: " + definition);
            }
            return theBackupAssembler.assemble(definition); // Can't assemble this URL.
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("WRAPathAssembler is assembling definition: " + definition);
        }
        String quotedQueryString = _getQuotedQueryString(definition);
        return constructURI(virtualWebroot, urlPath, quotedQueryString, definition.getFragment());
    }

    private String _getQuotedQueryString(Definition definition) {
        Map<String, String[]> newQryParams = new HashMap<String, String[]>();

        // build the query string if there is one
        for (Object o : definition.getParameterNames()) {
            // Get the parameter name
            String key = (String) o;

            // Don't add embedded params to the query string
            if (!EMBEDDED_PARAMS.contains(key)) {
                String[] vals = definition.getParameters(key);
                if (key.equals(PACKEDARGS)) {
                    vals = excludeFromPackedargs(vals, EMBEDDED_PARAMS);
                }
                newQryParams.put(key, vals);
            }
        }
        return constructQueryString(newQryParams);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String[]> getPackedargs(Definition definition) {
        String[] packedargs = definition.getParameters(PACKEDARGS);
        if (packedargs != null && packedargs.length > 0) {
            return parseQueryString(packedargs[0]);
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * Construct a query string using the required input for this assembler.
     *
     * @param virtualWebroot    as defined in the GST Site Foundation spec
     * @param uriPath           as defined in the GST Site Foundation spec
     * @param quotedQueryString query string excluding embedded params, quoted
     * @param fragment          fragment from definition
     * @return valid URL
     * @throws URISyntaxException on bad input data
     * @see LightweightAbstractAssembler#constructURI for inspiration if edits are required
     */
    private static final URI constructURI(final String virtualWebroot, String uriPath, String quotedQueryString, String fragment) throws URISyntaxException {
        StringBuilder bf = new StringBuilder();
        bf.append(virtualWebroot);
        // Path needs quoting though, so let the URI object do it for us.
        // Use the toASCIIString() method because we need the quoted values.
        // (toString() is really just for readability and debugging, not programmatic use)
        bf.append(new URI(null, null, uriPath, null, null).getRawPath());
        if (goodString(quotedQueryString)) {
            bf.append('?').append(quotedQueryString); // already quoted
        }
        // needs quoting
        if (goodString(fragment)) {
            bf.append(new URI(null, null, null, null, fragment).toASCIIString());
        }
        URI uri = new URI(bf.toString());

        if (LOG.isDebugEnabled()) {
            StringBuilder msg = new StringBuilder();
            if (LOG.isTraceEnabled()) {
                msg.append("Constructing new URI using the following components: \n\tvirtual-webroot=").append(virtualWebroot).append("\n\turi-path=").append(uriPath).append("\n\tquotedQueryString=").append(quotedQueryString).append("\n\tfragment=").append(fragment);
                msg.append("\n");
            }
            msg.append("Assembled URI").append(uri.toASCIIString());
            if (LOG.isTraceEnabled()) LOG.trace(msg);
            else LOG.debug(msg);
        }
        return uri;
    }

    public Definition disassemble(URI uri, Definition.ContainerType containerType) throws URISyntaxException {
        Map<String, String[]> params = parseQueryString(uri.getRawQuery());

        String[] virtualWebrootArr = params.get(VIRTUAL_WEBROOT);
        String[] uriPathArr = params.get(URL_PATH);
        if (virtualWebrootArr == null || virtualWebrootArr.length != 1) {
            if (LOG.isTraceEnabled())
                LOG.trace("WRAPathAssembler cannot disassemble URI '" + uri + "' because the " + VIRTUAL_WEBROOT + " parameter is either missing or has more than one value");
            return theBackupAssembler.disassemble(uri, containerType);
        }
        if (uriPathArr == null || uriPathArr.length != 1) {
            if (LOG.isTraceEnabled())
                LOG.trace("WRAPathAssembler cannot disassemble URI '" + uri + "' because the " + URL_PATH + " parameter is either missing or has more than one value");
            return theBackupAssembler.disassemble(uri, containerType);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Disassembling URI: " + uri);
        }

        Simple result;
        try {
            Map<String, String[]> qryParams = parseQueryString(uri.getRawQuery());
            qryParams.put(PubConstants.PAGENAME, pagename);
            final Definition.AppType appType = Definition.AppType.CONTENT_SERVER;
            final Definition.SatelliteContext satelliteContext = Definition.SatelliteContext.SATELLITE_SERVER;
            final boolean sessionEncode = false;
            final String scheme = uri.getScheme();
            final String authority = uri.getAuthority();
            final String fragment = uri.getFragment();

            result = new Simple(sessionEncode, satelliteContext, containerType, scheme, authority, appType, fragment);
            result.setQueryStringParameters(qryParams);
        } catch (IllegalArgumentException e) {
            // Something bad happened
            throw new URISyntaxException(uri.toString(), e.toString());
        }
        return result;
    }
}