package com.fatwire.gst.foundation.controller;

import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Util.ftMessage;

import com.fatwire.gst.foundation.facade.sql.table.TableColumn;
import com.fatwire.gst.foundation.facade.sql.table.TableCreator;
import com.fatwire.gst.foundation.facade.sql.table.TableDef;
import com.fatwire.gst.foundation.facade.sql.table.TableColumn.Type;

public class UrlRegistry {

    public void install(ICS ics) {
        TableDef def = new TableDef("GSTUrlRegistry", ftMessage.Browser,
                ftMessage.objecttbl);

        def.addColumn(new TableColumn("id", Type.propbigint, true));
        def.addColumn(new TableColumn("path", Type.propvarchar).setLength(4000)
                .setNullable(false));
        def.addColumn(new TableColumn("opt_vwebroot", Type.propvarchar)
                .setLength(255));
        def.addColumn(new TableColumn("opt_url_path", Type.propvarchar)
                .setLength(4000));
        def.addColumn(new TableColumn("opt_depth", Type.propinteger)
                .setLength(10));
        def.addColumn(new TableColumn("assettype", Type.propvarchar)
                .setLength(255));
        def
                .addColumn(new TableColumn("assetid", Type.propbigint)
                        .setLength(38));
        def.addColumn(new TableColumn("startdate", Type.proptimestamp));
        def.addColumn(new TableColumn("enddate", Type.proptimestamp));
        def.addColumn(new TableColumn("opt_vwebroot", Type.propvarchar)
                .setLength(255));
        new TableCreator(ics).createTable(def);
    }

}
