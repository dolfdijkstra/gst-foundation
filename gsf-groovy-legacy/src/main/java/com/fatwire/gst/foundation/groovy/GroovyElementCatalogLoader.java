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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.Utilities;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;

import com.fatwire.gst.foundation.facade.runtag.render.LogDep;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

/**
 * Loader for groovy script classes from the ElementCatalog
 * 
 * @author Dolf Dijkstra
 * @since September 21,2012
 */
/*
 * alternative method:
 * http://groovy.codehaus.org/Alternate+Spring-Groovy-Integration
 */
public class GroovyElementCatalogLoader extends DiskGroovyLoader {
	private PreparedStmt stmt;
	private Logger logger = LoggerFactory.getLogger("tools.gsf.groovy.GroovyElementCatalogLoader");
	private boolean isLoaded = false;
	private String path;

	public GroovyElementCatalogLoader(ServletContext servletContext) {
		super(servletContext);
		/*
		 * ElementCatalog does not have Browser ACL, assumption here is that
		 * elements are updated as assets
		 */
		stmt = new PreparedStmt(
				"SELECT * FROM ElementCatalog WHERE elementname=?",
				Collections.singletonList("CSElement"));
		stmt.setElement(0, java.sql.Types.VARCHAR);

	}

	@Override
	public Object load(ICS ics, String resourceName) throws Exception {
		if (!isLoaded) {
			bootEngine(ics, path);
			isLoaded = true;
		}

		if (logger.isDebugEnabled())
			logger.debug("Loading groovy script " + resourceName);
		if (ics.IsElement(resourceName)) {

			final StatementParam param = stmt.newParam();
			param.setString(0, resourceName);
			Row row = SqlHelper.selectSingle(ics, stmt, param);

			// ELEMENTNAME DESCRIPTION URL RESDETAILS1
			// RESDETAILS2

			String url = row.getString("url");
			String res1 = row.getString("resdetails1");
			String res2 = row.getString("resdetails2");
			Map<String, String> m = new HashMap<String, String>();
			Utilities.getParams(res1, m, false);
			Utilities.getParams(res2, m, false);
			String tid = m.get("tid");
			String eid = m.get("eid");
			if (StringUtils.isNotBlank(tid)) {
				LogDep.logDep(ics, "Template", tid);
			}
			if (StringUtils.isNotBlank(eid)) {
				LogDep.logDep(ics, "CSElement", eid);
			}
			// prevent case where resourcename is same as a jsp element.
			if (url.endsWith(".groovy")) {
				// no loading based on fallback class name as the super method
				// has.
				if (logger.isDebugEnabled()) {
					logger.debug("Found element for " + resourceName + " => "
							+ url);
				}
				Class<?> x = getGroovyScriptEngine().loadScriptByName(url);
				return x.newInstance();

			} else {
				return super.load(ics, resourceName);
			}
		} else {
			return super.load(ics, resourceName);
		}

	}

	@Override
	public void bootEngine(final String path) {
		this.path = path;
	}

	public void bootEngine(ICS ics, final String path) {
		String[] root = new String[2];

		String elementCatalogDefDir = ics
				.ResolveVariables("CS.CatalogDir.ElementCatalog");
		root[0] = elementCatalogDefDir;
		root[1] = path;

		GroovyScriptEngine gse;
		try {
			gse = new GroovyScriptEngine(root, Thread.currentThread()
					.getContextClassLoader());
			gse.getConfig().setRecompileGroovySource(true);
			gse.getConfig().setMinimumRecompilationInterval(
					getMinimumRecompilationInterval());
			setGroovyScriptEngine(gse);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
