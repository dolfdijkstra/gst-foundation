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
package com.fatwire.gst.foundation.wra;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IList;

import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.IListUtils;
import com.fatwire.gst.foundation.facade.assetapi.DirectSqlAccessTools;
import com.fatwire.gst.foundation.facade.sql.IListIterable;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

/**
 * Backdoor implementation of WraCoreFieldDao that does not utilize any Asset
 * APIs. This class should be used sparingly and may result in some
 * dependencies, that would ordinarily be recorded, being skipped.
 * <p/>
 * User: Tony Field Date: 2011-05-06
 * 
 * @author Dolf Dijkstra
 */
public class WraCoreFieldApiBypassDao extends AssetApiWraCoreFieldDao {

	/**
	 * @deprecated makes unsafe calls and references to unsupported ICS objects
	 * @param ics
	 * @return
	 */
	@Deprecated
	public static WraCoreFieldApiBypassDao getBackdoorInstance(ICS ics) {

		Object o = ics.GetObj(WraCoreFieldApiBypassDao.class.getName());
		if (o == null) {
			o = new WraCoreFieldApiBypassDao(ics);
			ics.SetObj(WraCoreFieldApiBypassDao.class.getName(), o);
		}
		return (WraCoreFieldApiBypassDao) o;
	}

	private final ICS ics;
	private final DirectSqlAccessTools directSqlAccessTools;

	private WraCoreFieldApiBypassDao(final ICS ics) {
		super(ics);
		this.ics = ics;
		directSqlAccessTools = new DirectSqlAccessTools(ics);
	}

	private static final Log LOG = LogFactory
			.getLog(WraCoreFieldApiBypassDao.class);

	/**
	 * Method to test whether or not an asset is web-referenceable. todo: low
	 * priority: optimize as this will be called at runtime (assest api incache
	 * will mitigate the performance issue)
	 * 
	 * @param id
	 *            asset ID to check
	 * @return true if the asset is a valid web-referenceable asset, false if it
	 *         is not
	 */
	@Override
	public boolean isWebReferenceable(final AssetId id) {

		if (directSqlAccessTools.isFlex(id)) {
			if (!isWraEnabledFlexAssetType(id)) {
				return false;
			}
		} else {
			if (!isWraEnabledBasicAssetType(id)) {
				return false;
			}

		}
		// type is wra, now lookup the asset data
		try {
			final WebReferenceableAsset wra = getWra(id);
			final boolean b = StringUtils.isNotBlank(wra.getPath());
			if (LOG.isTraceEnabled()) {
				LOG.trace("Asset "
						+ id
						+ (b ? " is " : " is not ")
						+ "web-referenceable, as determinted by the presence of a path attribute.");
			}
			return b;
		} catch (final RuntimeException e) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Asset " + id + " is not web-referenceable: " + e, e);
			}
			return false;
		}
	}

	private boolean isWraEnabledFlexAssetType(final AssetId id) {
		// TODO:medium implements proper check
		return true;
	}

	/**
	 * Return a web referenceable asset bean given an input id. Required fields
	 * must be set or an exception is thrown.
	 * 
	 * @param id
	 *            asset id
	 * @return WebReferenceableAsset, never null
	 * @see #isWebReferenceable(AssetId)
	 */
	@Override
	public WebReferenceableAsset getWra(final AssetId id) {
		if (directSqlAccessTools.isFlex(id)) {
			// todo: medium: optimize as this is very inefficient for flex
			// assets
			final PreparedStmt basicFields = new PreparedStmt(
					"SELECT id,name,description,subtype,status,path,template,startdate,enddate"
							+ " FROM " + id.getType() + " WHERE id = ?",
					Collections.singletonList(id.getType()));
			basicFields.setElement(0, id.getType(), "id");

			final StatementParam param = basicFields.newParam();
			param.setLong(0, id.getId());
			final Row row = SqlHelper.selectSingle(ics, basicFields, param);

			final WraBeanImpl wra = new WraBeanImpl();
			wra.setId(id);
			wra.setName(row.getString("name"));
			wra.setDescription(row.getString("description"));
			wra.setSubtype(row.getString("subtype"));
			wra.setPath(row.getString("path"));
			wra.setTemplate(row.getString("template"));
			if (StringUtils.isNotBlank(row.getString("startdate"))) {
				wra.setStartDate(row.getDate("startdate"));
			}
			if (StringUtils.isNotBlank(row.getString("enddate"))) {
				wra.setEndDate(row.getDate("enddate"));
			}

			wra.setMetaTitle(directSqlAccessTools.getFlexAttributeValue(id,
					"metatitle"));
			wra.setMetaDescription(directSqlAccessTools.getFlexAttributeValue(
					id, "metadescription"));
			wra.setMetaKeyword(directSqlAccessTools.getFlexAttributeValue(id,
					"metakeywords"));
			wra.setH1Title(directSqlAccessTools.getFlexAttributeValue(id,
					"h1title"));
			wra.setLinkText(directSqlAccessTools.getFlexAttributeValue(id,
					"linktext"));

			return wra;
		} else {

			final PreparedStmt basicFields = new PreparedStmt(
					"SELECT id,name,description,subtype,status,path,template,startdate,enddate,"
							+ "metatitle,metadescription,metakeyword,h1title,linktext FROM "
							+ id.getType() + " WHERE id = ?",
					Collections.singletonList(id.getType()));
			basicFields.setElement(0, id.getType(), "id");

			final StatementParam param = basicFields.newParam();
			param.setLong(0, id.getId());
			final Row row = SqlHelper.selectSingle(ics, basicFields, param);

			final WraBeanImpl wra = new WraBeanImpl();
			wra.setId(id);
			wra.setName(row.getString("name"));
			wra.setDescription(row.getString("description"));
			wra.setSubtype(row.getString("subtype"));
			wra.setMetaTitle(row.getString("metatitle"));
			wra.setMetaDescription(row.getString("metadescription"));
			wra.setMetaKeyword(row.getString("metakeywords"));
			wra.setH1Title(row.getString("h1title"));
			wra.setLinkText(row.getString("linktext"));
			wra.setPath(row.getString("path"));
			wra.setTemplate(row.getString("template"));
			if (StringUtils.isNotBlank(row.getString("startdate"))) {
				wra.setStartDate(row.getDate("startdate"));
			}
			if (StringUtils.isNotBlank(row.getString("enddate"))) {
				wra.setEndDate(row.getDate("enddate"));
			}
			return wra;
		}
	}

	/**
	 * Checks if the table definition for a basic asset has all the wra fields.
	 * 
	 * @param id
	 */
	private boolean isWraEnabledBasicAssetType(final AssetId id) {
		boolean wraTable;
		final String listname = IListUtils.generateRandomListName();
		final IList list = ics.CatalogDef(id.getType(), listname,
				new StringBuffer());
		ics.RegisterList(listname, null);
		final List<String> attr = Arrays.asList(WRA_ATTRIBUTE_NAMES);
		int count = 0;
		for (final Row row : new IListIterable(list)) {
			/*
			 * "COLNAME" "COLTYPE" "COLSIZE" "KEY"
			 */
			if (attr.contains(row.getString("COLNAME").toLowerCase())) {
				count++;
			}
		}
		wraTable = count == attr.size();// all wra attributes are found in the
		// table def.
		if (LOG.isTraceEnabled()) {
			LOG.trace("Asset "
					+ id
					+ (wraTable ? " is " : " is not ")
					+ "web-referenceable, as determinted by the table definition.");
		}

		return wraTable;
	}

	@Override
	public boolean isVanityAsset(AssetId id) {
		try {
			final PreparedStmt basicFields = new PreparedStmt(
					"SELECT path FROM " + id.getType() + " WHERE id = ?",
					Collections.singletonList(id.getType()));
			basicFields.setElement(0, id.getType(), "id");

			final StatementParam param = basicFields.newParam();
			param.setLong(0, id.getId());
			final Row row = SqlHelper.selectSingle(ics, basicFields, param);
			if (row == null)
				return false;
			return StringUtils.isNotBlank(row.getString("path"));
		} catch (RuntimeException e) {
			return false;
		}
	}

}
