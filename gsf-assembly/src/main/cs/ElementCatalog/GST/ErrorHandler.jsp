<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld"
%><cs:ftcs>
<h1>Error</h1>
<pre>
<%
    Throwable t =(Throwable)ics.GetObj("com.fatwire.gst.foundation.exception");
    %><%=com.fatwire.gst.foundation.DebugHelper.toString(t)
    %></pre>
</cs:ftcs>