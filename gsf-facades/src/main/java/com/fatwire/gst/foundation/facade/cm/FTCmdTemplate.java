package com.fatwire.gst.foundation.facade.cm;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.gst.foundation.facade.FTValListFacade;

public abstract class FTCmdTemplate extends FTValListFacade {

	public static final String FTCMD = "ftcmd";

	private final String ftcmd;

	protected FTCmdTemplate(final String ftcmd, final String table) {
		super();
		list.setValString(FTCMD, ftcmd);
		list.setValString("tablename", table);
		this.ftcmd = ftcmd;
	}

	/**
	 * We can do preliminary check on the values for lst to see if they are
	 * complete
	 * 
	 * @return true to continue with ics.CatalogManager call
	 */
	protected boolean preExcecuteAssert(final ICS ics) {
		return true;
	}

	protected void postExcecuteCheck(final ICS ics) {

	}

	final public void execute(final ICS ics) {
		if (this.preExcecuteAssert(ics)) {
			ics.ClearErrno();
			if (!ics.CatalogManager(list)) {
				throw new RuntimeException("CatalogManager said no to " + ftcmd
						+ "with errno: " + ics.GetErrno());
			}
			this.postExcecuteCheck(ics);
		}
	}

}
