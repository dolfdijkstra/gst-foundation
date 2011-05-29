<%@ taglib prefix="cs" uri="futuretense_cs/ftcs1_0.tld" %>
<%@ taglib prefix="ics" uri="futuretense_cs/ics.tld" %>
<%//
// GST/Foundation/InstallAssembler
//
// INPUT
//
// OUTPUT
//%>
<%@ page import="COM.FutureTense.Interfaces.ICS" %>
<%@ page import="COM.FutureTense.Interfaces.IList" %>
<%@ page import="COM.FutureTense.Interfaces.Utilities" %>
<%@ page import="COM.FutureTense.Util.ftErrors" %>
<%@ page import="COM.FutureTense.Util.ftMessage"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<cs:ftcs>

<%
        String inipath = getServletConfig().getServletContext().getInitParameter("inipath");

        String srProps = Utilities.osSafeSpec(inipath +"/futuretense_xcel.ini");
        InputStream in = new FileInputStream(srProps);
        Properties prop = new Properties();
        prop.load(in);
        in.close();
        if(true){
                    StringWriter sw = new StringWriter();
                    //FileOutputStream out = new FileOutputStream(orig);
                    prop.store(sw, "Modified to GSF installer on " + new Date());
                    out.write("<pre>");
                    out.write(sw.toString());
                    out.write("</pre>");

        }
        int i = 1;
        boolean hasPageRef = false;
        String pageref = prop.getProperty("xcelerate.pageref");
        if ("com.fatwire.gst.foundation.url.WraPageReference".equals(pageref)) {
            hasPageRef = true;
        }


        if (!hasPageRef) {
            out.write("Registring the GSF pageref in futuretense_xcel.ini.<br/>");
            prop.setProperty("xcelerate.pageref", "com.fatwire.gst.foundation.url.WraPageReference");
            File orig = new File(srProps);
            File bk = new File(srProps + "." + System.currentTimeMillis() + ".bk");
            if (orig.exists()) {
                orig.renameTo(bk);
                FileOutputStream fout = new FileOutputStream(orig);
                prop.store(fout, "Modified by GSF installer.");
                fout.close();

            }

        }else {
            out.write("GSF pageref is already registered in futuretense_xcel.ini. No changes made.<br/>");
        }



%>

</cs:ftcs>