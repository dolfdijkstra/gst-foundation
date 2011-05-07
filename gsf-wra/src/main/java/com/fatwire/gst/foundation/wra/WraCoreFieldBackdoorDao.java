package com.fatwire.gst.foundation.wra;

import COM.FutureTense.Interfaces.ICS;
import com.fatwire.assetapi.data.AssetData;
import com.fatwire.assetapi.data.AssetId;
import com.fatwire.cs.core.db.PreparedStmt;
import com.fatwire.cs.core.db.StatementParam;
import com.fatwire.gst.foundation.facade.assetapi.BackdoorUtils;
import com.fatwire.gst.foundation.facade.ics.ICSFactory;
import com.fatwire.gst.foundation.facade.sql.Row;
import com.fatwire.gst.foundation.facade.sql.SqlHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;

/**
 * Backdoor implementation of WraCoreFieldDao that does not utilize
 * any Asset APIs.  This class should be used sparingly and may result
 * in some dependencies, that would ordinarily be recorded, being skipped.
 * <p/>
 * User: Tony Field
 * Date: 2011-05-06
 */
public class WraCoreFieldBackdoorDao extends WraCoreFieldDao {
    public static WraCoreFieldBackdoorDao getBackdoorInstance(ICS ics) {
        if (ics == null) {
            ics = ICSFactory.getOrCreateICS();
        }

        Object o = ics.GetObj(WraCoreFieldBackdoorDao.class.getName());
        if (o == null) {
            o = new WraCoreFieldBackdoorDao(ics);
            ics.SetObj(WraCoreFieldBackdoorDao.class.getName(), o);
        }
        return (WraCoreFieldBackdoorDao) o;
    }

    private final ICS ics;
    private final BackdoorUtils backdoorUtils;

    private WraCoreFieldBackdoorDao(ICS ics) {
        this.ics = ics;
        backdoorUtils = new BackdoorUtils(ics);
    }

    private static final Log LOG = LogFactory.getLog(WraCoreFieldBackdoorDao.class);

    /**
     * @throws UnsupportedOperationException - not possible in this implementation
     */
    public AssetData getAsAssetData(AssetId id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Method to test whether or not an asset is web-referenceable. todo: low
     * priority: optimize as this will be called at runtime (assest api incache
     * will mitigate the performance issue)
     *
     * @param id asset ID to check
     * @return true if the asset is a valid web-referenceable asset, false if it
     *         is not
     */
    public boolean isWebReferenceable(AssetId id) {
        try {
            WebReferenceableAsset wra = getWra(id);
            return StringUtils.isNotBlank(wra.getPath());
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * @throws UnsupportedOperationException - not possible in this implementation
     */
    public boolean isWebReferenceable(WebReferenceableAsset wra) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * @throws UnsupportedOperationException - not possible in this implementation
     */
    public boolean hasPathAttribute(AssetData data) {
        throw new UnsupportedOperationException("Not implemented");

    }

    /**
     * Return a web referenceable asset bean given an input id. Required fields
     * must be set or an exception is thrown.
     *
     * @param id asset id
     * @return WebReferenceableAsset, never null
     * @see #isWebReferenceable(AssetId)
     */
    public WebReferenceableAsset getWra(AssetId id) {
        if (backdoorUtils.isFlex(id)) {
            // todo: medium: optimize as this is very inefficient for flex assets
            PreparedStmt basicFields = new PreparedStmt("select id,name,description,subtype,status,path,template,startdate,enddate " +
                    "from " + id.getType() +
                    "where id = ?", Collections.singletonList(id.getType()));
            basicFields.setElement(0, id.getType(), "id");

            StatementParam param = basicFields.newParam();
            param.setLong(0, id.getId());
            Row row = SqlHelper.selectSingle(ics, basicFields, param);

            WraBeanImpl wra = new WraBeanImpl();
            wra.setId(id);
            wra.setName(row.getString("name"));
            wra.setDescription(row.getString("description"));
            wra.setSubtype(row.getString("subtype"));
            wra.setPath(row.getString("path"));
            wra.setTemplate(row.getString("template"));
            if (StringUtils.isNotBlank(row.getString("startdate")))
                wra.setStartDate(row.getDate("startdate"));
            if (StringUtils.isNotBlank(row.getString("enddate")))
                wra.setEndDate(row.getDate("enddate"));

            wra.setMetaTitle(backdoorUtils.getFlexAttributeValue(id, "metatitle"));
            wra.setMetaDescription(backdoorUtils.getFlexAttributeValue(id, "metadescription"));
            wra.setMetaKeyword(backdoorUtils.getFlexAttributeValue(id, "metakeywords"));
            wra.setH1Title(backdoorUtils.getFlexAttributeValue(id, "h1title"));
            wra.setLinkText(backdoorUtils.getFlexAttributeValue(id, "linktext"));

            return wra;
        } else {
            PreparedStmt basicFields = new PreparedStmt("select id,name,description,subtype,status,path,template,startdate,enddate," +
                    "metatitle,metadescription,metakeyword,h1title,linktext " +
                    "from " + id.getType() +
                    "where id = ?", Collections.singletonList(id.getType()));
            basicFields.setElement(0, id.getType(), "id");

            StatementParam param = basicFields.newParam();
            param.setLong(0, id.getId());
            Row row = SqlHelper.selectSingle(ics, basicFields, param);

            WraBeanImpl wra = new WraBeanImpl();
            wra.setId(id);
            wra.setName(row.getString("name"));
            wra.setDescription(row.getString("description"));
            wra.setSubtype(row.getString("subtype"));
            wra.setMetaTitle(row.getString("metatitle"));
            wra.setMetaDescription(row.getString("metadescription"));
            wra.setMetaKeyword(row.getString("metakeywords"));
            wra.setH1Title(row.getString("h1title"));
            wra.setLinkText(row.getString("linktext"));
            wra.setPath(row.getString("path"));
            wra.setTemplate(row.getString("template"));
            if (StringUtils.isNotBlank(row.getString("startdate")))
                wra.setStartDate(row.getDate("startdate"));
            if (StringUtils.isNotBlank(row.getString("enddate")))
                wra.setEndDate(row.getDate("enddate"));
            return wra;
        }
    }

    /**
     * @throws UnsupportedOperationException - not possible in this implementation
     */
    public WebReferenceableAsset getWra(AssetData data) {
        throw new UnsupportedOperationException("Not implemented");
    }
}