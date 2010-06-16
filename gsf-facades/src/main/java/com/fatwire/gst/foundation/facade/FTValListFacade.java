package com.fatwire.gst.foundation.facade;

import java.util.Date;

import COM.FutureTense.Interfaces.FTValList;

import com.fatwire.cs.core.db.Util;

/**
 * Base class for all ICS commands that accept an FTValList (like
 * CatalogManager, runTag and CallElement)
 * 
 * Converts the different java types to the correct FTValList type
 * 
 * @author Dolf.Dijkstra
 * 
 */

public class FTValListFacade {

	protected final FTValList list = new FTValList();

	public FTValListFacade() {
		super();
	}

	protected final void set(final String key, final String value) {
		list.setValString(key, value);
	}

	protected final void set(final String key, final boolean value) {
		list.setValString(key, Boolean.toString(value));
	}

	protected final void set(final String key, final int value) {
		list.setValInt(key, value);
	}

	protected final void set(final String key, final byte[] value) {
		list.setValBLOB(key, value);
	}

	protected final void set(final String key, final long value) {
		list.setValString(key, Long.toString(value));
	}

	protected final void set(final String key, final Date value) {
		list.setValString(key, Util.formatJdbcDate(value));
	}

    protected final FTValList getList() {
        return list;
    }

}