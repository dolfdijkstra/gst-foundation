package com.fatwire.gst.foundation.facade.runtag.listobject;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * <LISTOBJECT.TOLIST
 * NAME="listname"
 * LISTVARNAME="varname"/> *
 *
 * @author Tony Field
 * @since Oct 24, 2008
 */
public final class ToList extends AbstractTagRunner
{
    public ToList() { super("LISTOBJECT.TOLIST"); }

    public void setName(String s) { set("NAME", s); }

    public void setListVarName(String s) { set("LISTVARNAME", s); }
}
