/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
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

package com.fatwire.gst.foundation.facade.install;

import static com.fatwire.gst.foundation.facade.sql.SqlHelper.quote;
import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.sql.SqlHelper;

/**
 * Helper for registering AssetEventListener in the database.
 * 
 * @author Dolf.Dijkstra
 * @since May 23, 2011
 */
public class AssetListenerInstall {
	public static final String REGISTRY_TABLE = "AssetListener_reg";

	/**
	 * Regsisters the AssetEventListener in the AssetListener table
	 * 
	 * @param ics Content Server context object
	 * @param classname
	 * @param blocking
	 */
	public static void register(ICS ics, String classname, boolean blocking) {
		String id = ics.genID(true);
		String listener = classname;
		SqlHelper.execute(ics, REGISTRY_TABLE, "DELETE FROM " + REGISTRY_TABLE
				+ " WHERE listener = " + quote(listener));
		SqlHelper.execute(ics, REGISTRY_TABLE, "INSERT INTO " + REGISTRY_TABLE
				+ " (id, listener, blocking) VALUES (" + quote(id) + ","
				+ quote(listener) + "," + quote(blocking ? "Y" : "N") + ")");
	}

	public static boolean isRegistered(ICS ics, String classname) {
		return SqlHelper.select(
				ics,
				REGISTRY_TABLE,
				"SELECT * FROM " + REGISTRY_TABLE + " WHERE listener = "
						+ quote(classname)).size() > 0;
	}

}
