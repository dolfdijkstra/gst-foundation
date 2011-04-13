package com.fatwire.gst.foundation.facade.runtag.render;

import com.fatwire.gst.foundation.facade.runtag.AbstractTagRunner;

/**
 * &lt;RENDER.LOOKUP KEY="name of lookup key" VARNAME="output variable name"
 * SITE="site name" [TID="id of template or cselement"]
 * [TTYPE="CSElement|Template"] [MATCH="x|x:|:x"] /&gt;
 * 
 * @author Dolf Dijkstra
 * @since Apr 13, 2011
 */
public class Lookup extends AbstractTagRunner {
    public enum Match {
        X("x"), XCOLON("x:"), COLONX(":x");
        private final String pattern;

        Match(String pattern) {
            this.pattern = pattern;
        }
    }

   

    public Lookup() {
        super("RENDER.LOOKUP");
    }

    public void setKey(String key) {
        set("KEY", key);
    }

    public void setVarname(String name) {
        set("VARNAME", name);
    }

    public void setSite(String site) {
        set("SITE", site);
    }

    public void setTid(String tid) {
        set("TID", tid);
    }

    public void setTtype(CallTemplate.Type ttype) {
        set("TTYPE", ttype.name());
    }

    public void setMatch(Match match) {
        set("MATCH", match.pattern);
    }

}
