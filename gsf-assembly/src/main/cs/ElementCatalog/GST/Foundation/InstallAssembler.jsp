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


        String srProps = getServletConfig().getServletContext().getRealPath("/WEB-INF/classes/ServletRequest.properties");
        InputStream in = new FileInputStream(srProps);
        Properties prop = new Properties();
        prop.load(in);
        in.close();
        if(false){
                    StringWriter sw = new StringWriter();
                    //FileOutputStream out = new FileOutputStream(orig);
                    prop.store(sw, "Modified to GSF installer on " + new Date());
                    out.write("<pre>");
                    out.write(sw.toString());
                    out.write("</pre>");

        }
        int i = 1;
        boolean hasGSFAssembler = false;
        for (;;) {

            String classname = prop.getProperty("uri.assembler." + i + ".classname");
            //out.write(classname +"<br/>");
            if (classname == null || classname.trim().length()==0) {
                break;
            }

            if ("com.fatwire.gst.foundation.url.WraPathAssembler".equals(classname)) {
                hasGSFAssembler = true;
            }

            i++;
        }

        int max = i;
        //out.write("max:"+i +"<br/>");
        if (!hasGSFAssembler) {
            out.write("Registring the GSF assembler in ServletRequest.properties.<br/>");
            for (i = max + 1; i > 1; i--) {
                String classname = prop.getProperty("uri.assembler." + (i - 1) + ".classname");
                String shortform = prop.getProperty("uri.assembler." + (i - 1) + ".shortform");
                out.write("" + classname + "<br/>");
                out.write("" + shortform + "<br/>");
                prop.setProperty("uri.assembler." + (i) + ".classname", classname);
                prop.setProperty("uri.assembler." + (i) + ".shortform", shortform);
            }
            prop.setProperty("com.fatwire.gst.foundation.url.wrapathassembler.dispatcher", "GST/Dispatcher");
            prop.setProperty("uri.assembler.1.shortform", "wrapath");
            prop.setProperty("uri.assembler.1.classname", "com.fatwire.gst.foundation.url.WraPathAssembler");
            File orig = new File(srProps);
            File bk = new File(srProps + "." + System.currentTimeMillis() + ".bk");
            if (orig.exists()) {
                orig.renameTo(bk);
                StringWriter sw = new StringWriter();
                FileOutputStream fout = new FileOutputStream(orig);
                prop.store(fout, "Modified by GSF installer.");
                fout.close();

            }

        }else {
            out.write("GSF assembler is already registered in ServletRequest.properties.No changes made.<br/>");
        }



%>

</cs:ftcs>