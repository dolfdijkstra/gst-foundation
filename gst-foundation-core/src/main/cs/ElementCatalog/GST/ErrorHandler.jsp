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
<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><cs:ftcs>
<h1>Error</h1>
<pre>
<%
    Throwable t =(Throwable)ics.GetObj("com.fatwire.gst.foundation.exception");
    %><%=com.fatwire.gst.foundation.DebugHelper.toString(t)
    %></pre>
</cs:ftcs>