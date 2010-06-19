package com.fatwire.gst.foundation.facade.runtag.render;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * Wraps the <RENDER.LOGDEP> xml tag.
 * 
 * @author Tony Field
 * @since 10-Jun-2008
 */
public final class LogDep extends AbstractTagRunner {
    public LogDep() {
        super("RENDER.LOGDEP");
    }

    public static enum DependencyType {
        exact, exists, greater, none
    }

    public void setAsset(String s) {
        set("ASSET", s);
    }

    public void setDeptype(DependencyType deptype) {
        switch (deptype) {
            case exact:
                set("DEPTYPE", "exact");
                break;
            case exists:
                set("DEPTYPE", "exists");
                break;
            case greater:
                set("DEPTYPE", "greater");
                break;
            case none:
                set("DEPTYPE", "none");
                break;
        }
    }

    public void setC(String s) {
        set("c", s);
    }

    public void setCid(String s) {
        set("cid", s);
    }

    public static void logDep(ICS ics, String c, String cid) {
        LogDep ld = new LogDep();
        ld.setC(c);
        ld.setCid(cid);
        ld.execute(ics);
    }

    public static void logDep(ICS ics, AssetId id) {
        logDep(ics, id.getType(), Long.toString(id.getId()));
    }
}
