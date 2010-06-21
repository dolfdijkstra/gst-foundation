<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld" %>
<%//
// GST/Foundation/UrlRegistryInstaller
//
// INPUT
//
// OUTPUT
//%>
<cs:ftcs>

<% new com.fatwire.gst.foundation.controller.UrlRegistry().install(ics); %>

</cs:ftcs>