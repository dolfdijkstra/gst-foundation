<%--

    Copyright 2010 FatWire Corporation. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ page import="com.fatwire.gst.foundation.url.WraAssetEventListener" %>
<%@ page import="com.fatwire.gst.foundation.url.WraPathTranslationServiceFactory" %>
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%@ taglib prefix="satellite" uri="futuretense_cs/satellite.tld" %>
<%
    //
    // GST/Foundation/UrlRegistryInstaller
    //
    // INPUT
    //
    // OUTPUT
    //
%>
<cs:ftcs>

    <% WraPathTranslationServiceFactory.getService(ics).install(); %>
    <% new WraAssetEventListener().install(ics); %>

</cs:ftcs>