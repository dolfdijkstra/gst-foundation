/**
 * 
 */
package com.yourcompany.owcs.seed;

import org.apache.commons.lang.StringUtils;

import COM.FutureTense.Interfaces.FTValList;
import COM.FutureTense.Interfaces.ICS;
import COM.FutureTense.Interfaces.IPS;
import COM.FutureTense.XML.Template.Seed2;

import com.fatwire.gst.foundation.mobile.DeviceDetector;
import com.fatwire.gst.foundation.mobile.DeviceType;
import com.fatwire.gst.foundation.mobile.mobiforge.MobiForgeDeviceDetector;

/**
 * A sample on how to manipulate the childpagename in an Seed, to be used in an XML Wrapper element.
 * 
 * @author Dolf.Dijkstra
 * @since 25 jun. 2012
 */
public class DetectorSeed implements Seed2 {

    private static final String CHILDPAGENAME = "childpagename";

    static DeviceDetector detector = new MobiForgeDeviceDetector();

    private ICS ics;

    /*
     * (non-Javadoc)
     * 
     * @see
     * COM.FutureTense.XML.Template.Seed#Execute(COM.FutureTense.Interfaces.
     * FTValList, COM.FutureTense.Interfaces.FTValList)
     */
    @Override
    public String Execute(FTValList in, FTValList out) {

    	DeviceType type = detector.detectDeviceType(ics);

        String childpagename = ics.GetVar(CHILDPAGENAME);
        if (StringUtils.isNotBlank(childpagename)) {
            if (type == DeviceType.MOBILE) {
                ics.SetVar(CHILDPAGENAME, childpagename + "_mobile");
            } else if (type == DeviceType.TABLET) {
                ics.SetVar(CHILDPAGENAME, childpagename + "_tablet");
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * COM.FutureTense.XML.Template.Seed2#SetAppLogic(COM.FutureTense.Interfaces
     * .IPS)
     */
    @Override
    public void SetAppLogic(IPS ips) {
        ics = ips.GetICSObject();
    }

}
