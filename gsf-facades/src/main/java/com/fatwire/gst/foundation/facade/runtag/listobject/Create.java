package com.fatwire.gst.foundation.facade.runtag.listobject;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 <LISTOBJECT.CREATE
       NAME="listname"
       COLUMNS="col1,col2,...,coln"/>
 *
 * @author Tony Field
 * @since Oct 24, 2008
 */
public final class Create extends AbstractTagRunner
{
    public Create() { super("LISTOBJECT.CREATE"); }

    public void setName(String s) { set("NAME", s); }
    public void setColumns(String commaSepList) { set("COLUMNS", commaSepList); }
    
}
