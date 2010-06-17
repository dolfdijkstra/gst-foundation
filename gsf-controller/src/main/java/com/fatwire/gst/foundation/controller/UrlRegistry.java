package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn.Type;


/**
 * Installer for the GSTUrlRegistry
 * 
 * @author Dolf.Dijkstra
 * @since Jun 17, 2010
 */
public class UrlRegistry {

    public void install(ICS ics) {
        TableDef def = new TableDef("GSTUrlRegistry", ftMessage.Browser, ftMessage.objecttbl);

        def.addColumn(new TableColumn("id", Type.ccbigint, true));
        def.addColumn(new TableColumn("path", Type.ccvarchar).setLength(4000).setNullable(false));
        def.addColumn(new TableColumn("opt_vwebroot", Type.ccvarchar).setLength(255));
        def.addColumn(new TableColumn("opt_url_path", Type.ccvarchar).setLength(4000));
        def.addColumn(new TableColumn("opt_depth", Type.ccinteger).setLength(10));
        def.addColumn(new TableColumn("assettype", Type.ccvarchar).setLength(255));
        def.addColumn(new TableColumn("assetid", Type.ccbigint).setLength(38));
        def.addColumn(new TableColumn("startdate", Type.ccdatetime));
        def.addColumn(new TableColumn("enddate", Type.ccdatetime));
        def.addColumn(new TableColumn("opt_vwebroot", Type.ccvarchar).setLength(255));
        new TableCreator(ics).createTable(def);
    }

}
