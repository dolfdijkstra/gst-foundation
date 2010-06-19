package com.fatwire.gst.foundation.facade.runtag.listobject;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <LISTOBJECT.ADDROW NAME="listname" colx="valx" ... colz="valz"/>
 * 
 * @author Tony Field
 * @since Oct 24, 2008
 */
public final class AddRow extends AbstractTagRunner {
    public AddRow() {
        super("LISTOBJECT.ADDROW");
    }

    public void setName(String s) {
        set("NAME", s);
    }

    public void setColumnValue(String colname, String colvalue) {
        set(colname, colvalue);
    }

}
