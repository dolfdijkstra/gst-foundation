package com.fatwire.gst.foundation.wra;

import java.util.Collections;

import COM.FutureTense.Interfaces.ICS;

import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SqlAssetSiteService implements AssetSiteService {
    private static final Log LOG = LogFactory.getLog(AssetApiWraCoreFieldDao.class);
    private final ICS ics;

    /**
     * @param ics
     */
    public SqlAssetSiteService(ICS ics) {
        super();
        this.ics = ics;
    }

    private static final String ASSETPUBLICATION_QRY = "SELECT p.name from Publication p, AssetPublication ap "
            + "WHERE ap.assettype = ? " + "AND ap.assetid = ? " + "AND ap.pubid=p.id";
    static final PreparedStmt AP_STMT = new PreparedStmt(ASSETPUBLICATION_QRY,
            Collections.singletonList("AssetPublication")); // todo: low
                                                            // priority:
                                                            // determine
    // why publication
    // cannot fit there.

    static {
        AP_STMT.setElement(0, "AssetPublication", "assettype");
        AP_STMT.setElement(1, "AssetPublication", "assetid");
    }

    public String resolveSite(String c, String cid) {
        final StatementParam param = AP_STMT.newParam();
        param.setString(0, c);
        param.setLong(1, Long.parseLong(cid));
        String result = null;
        for (Row pubid : SqlHelper.select(ics, AP_STMT, param)) {
            if (result != null) {
                LOG.warn("Found asset "
                        + c
                        + ":"
                        + cid
                        + " in more than one publication. It should not be shared; aliases are to be used for cross-site sharing.  Controller will use first site found: "
                        + result);
            } else {
                result = pubid.getString("name");
            }
        }
        return result;
    }

}
